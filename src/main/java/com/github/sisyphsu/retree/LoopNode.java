package com.github.sisyphsu.retree;

import java.util.Arrays;

import static com.github.sisyphsu.retree.Util.*;

/**
 * This node supports all loop match, like '+', '++', '{1,10}' etc.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class LoopNode extends Node {

    private final static long RESET = 1L << 63;
    private final static int FLAG = 1 << 30;

    private final int type;
    private final int minTimes;
    private final int maxTimes;
    private final Node body;

    private final int timesVar;
    private final int offsetVar;
    private final int deepVar;

    private boolean complex = false;

    public LoopNode(Node head, Node tail, int cmin, int cmax, int type, int localOffset) {
        this.body = head;
        this.type = type;
        this.minTimes = cmin;
        this.maxTimes = cmax;

        this.timesVar = localOffset - 3;
        this.offsetVar = localOffset - 2;
        this.deepVar = localOffset - 1;

        tail.next = this;
    }

    @Override
    public void study() {
        if (minInput < 0) {
            this.minInput = 0;
            body.study();
            next.study();
            this.minInput = next.minInput + body.minInput * minTimes;
            // detect the body is complex or not
            Node node = this.body;
            while (true) {
                if (node instanceof BranchNode || node instanceof LoopNode) {
                    complex = true;
                    break;
                }
                if (node.next == this) {
                    break;
                }
                node = node.next;
            }
            if (!complex) {
                node.next = EMPTY;
            }
        }
    }

    private boolean matchSimple(ReContext cxt, CharSequence input, int cursor) {
        int times = 0;

        for (; times < minTimes; times++) {
            if (!body.match(cxt, input, cursor)) {
                return false;
            }
            cursor = cxt.last;
        }

        int backCount = 0;
        switch (type) {
            case LAZY:
                for (; ; times++) {
                    if (next.match(cxt, input, cursor)) {
                        return true;
                    }
                    if (times >= maxTimes) {
                        return false;
                    }
                    if (!body.match(cxt, input, cursor) || cursor == cxt.last) {
                        return false;
                    }
                }

            case GREEDY:
                for (; times < maxTimes; times++) {
                    if (!body.match(cxt, input, cursor) || cursor == cxt.last) {
                        break;
                    }
                    cursor = cxt.last;
                    if (cxt.backs.length <= backCount) {
                        cxt.backs = Arrays.copyOf(cxt.backs, cxt.backs.length * 2);
                    }
                    cxt.backs[backCount++] = cursor;
                }
                break;

            case POSSESSIVE:
                for (; times < maxTimes; times++) {
                    if (!body.match(cxt, input, cursor)) {
                        break;
                    }
                    cursor = cxt.last;
                }
                break;
        }

        boolean result;
        for (; ; ) {
            result = next.match(cxt, input, cursor);
            if (result || backCount == 0)
                break;
            cursor = cxt.backs[--backCount]; // backtracking
        }
        return result;
    }

    @Override
    public boolean match(ReContext cxt, CharSequence input, int cursor) {
        if (!complex) {
            return this.matchSimple(cxt, input, cursor);
        }
        int times = cxt.localVars[timesVar];
        int prevOffset = cxt.localVars[offsetVar];

        boolean backtracking = false;
        if (times < 0) {
            cxt.addBackPoint(this, cursor, RESET);
            times = 0;
            prevOffset = 0;
        } else {
            backtracking = (times & FLAG) != 0;
            times &= ~FLAG;
        }

        // POSSESSIVE: clear all added back-point
        if (type == Util.POSSESSIVE) {
            int stackDeep = cxt.localVars[deepVar];
            if (times > 0 && stackDeep >= 0) {
                cxt.stackDeep = stackDeep;
            }
            cxt.localVars[deepVar] = cxt.stackDeep; // backup deep
        }

        int rest = cxt.to - cursor;

        if (times < minTimes) {
            if (times > 0 && cursor <= prevOffset) {
                return false;
            }
            if (rest < next.minInput + body.minInput * (minTimes - times)) {
                return false; // fast-fail
            }
            cxt.addBackPoint(this, cursor, ((long) times << 32) | Integer.MAX_VALUE);
            return matchBody(cxt, times, input, cursor);
        }

        // fast-fail
        if (rest < next.minInput) {
            return false;
        }

        if (times >= maxTimes) {
            // if body is complex, need add back-point to recover loop status.
            if (complex) {
                cxt.addBackPoint(this, cursor, ((long) times << 32) | Integer.MAX_VALUE);
            }
            return matchNext(cxt, input, cursor);
        }

        if (backtracking) {
            if (type != Util.LAZY) {
                cxt.addBackPoint(this, cursor, ((long) times << 32) | Integer.MAX_VALUE);
                return matchNext(cxt, input, cursor);
            }
            if (times > 0 && cursor <= prevOffset) {
                return false; // treat empty match as failure
            }
            if (rest < body.minInput + next.minInput) {
                return false; // fast fail
            }
            return matchBody(cxt, times, input, cursor);
        }

        // LAZY: go to next first
        if (type == Util.LAZY) {
            cxt.addBackPoint(this, cursor, ((long) times << 32) | prevOffset); // prevOffset is important
            return matchNext(cxt, input, cursor);
        }

        // treat empty match as failure
        if (times > 0 && cursor <= prevOffset) {
            return false;
        }

        // if rest isn't enough for body+next, goto next directly
        if (type != Util.POSSESSIVE && rest < body.minInput + next.minInput) {
            return matchNext(cxt, input, cursor);
        }

        if (type == Util.POSSESSIVE && rest < body.minInput) {
            return matchNext(cxt, input, cursor);
        }

        cxt.addBackPoint(this, cursor, ((long) times << 32) | cursor);
        return matchBody(cxt, times, input, cursor);
    }

    @Override
    public boolean onBack(ReContext cxt, long data) {
        cxt.localVars[deepVar] = -1;
        if (data == RESET) {
            cxt.localVars[timesVar] = -1;
            cxt.localVars[offsetVar] = -1;
            return false;
        }
        int times = (int) (data >>> 32);
        int offset = (int) data;
        if (offset == Integer.MAX_VALUE) {
            cxt.localVars[timesVar] = times;
            cxt.localVars[offsetVar] = 0; // don't use the real offset, use 0 as default
            return false;
        }
        cxt.localVars[timesVar] = times | FLAG; // bind flags in times
        cxt.localVars[offsetVar] = offset;
        return true;
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof LoopNode) {
            if (type != ((LoopNode) node).type)
                return false;
            if (minTimes != ((LoopNode) node).minTimes)
                return false;
            if (maxTimes != ((LoopNode) node).maxTimes)
                return false;
            if (offsetVar != ((LoopNode) node).offsetVar)
                return false;
            return body.alike(((LoopNode) node).body);
        }
        return false;
    }

    private boolean matchBody(ReContext cxt, int times, CharSequence input, int cursor) {
        cxt.localVars[timesVar] = times + 1;
        cxt.localVars[offsetVar] = cursor;
        return body.match(cxt, input, cursor);
    }

    private boolean matchNext(ReContext cxt, CharSequence input, int cursor) {
        cxt.localVars[timesVar] = -1;
        cxt.localVars[offsetVar] = -1;
        cxt.localVars[deepVar] = -1;
        return next.match(cxt, input, cursor);
    }

    private static Node EMPTY = new Node() {
        @Override
        public boolean match(ReContext cxt, CharSequence input, int cursor) {
            return true;
        }

        @Override
        public boolean alike(Node node) {
            return true;
        }
    };

}
