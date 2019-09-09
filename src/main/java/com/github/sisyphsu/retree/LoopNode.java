package com.github.sisyphsu.retree;

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
            Node next = this.next;
            while (next != null && next != this) {
                if (next instanceof BranchNode || next instanceof LoopNode) {
                    complex = true;
                    break;
                }
                next = next.next;
            }
        }
    }

    @Override
    public boolean match(ReContext cxt) {
        int times = cxt.localVars[timesVar];
        int prevOffset = cxt.localVars[offsetVar];

        boolean backtracking = false;
        if (times < 0) {
            cxt.addBackPoint(this, cxt.cursor, RESET);
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

        int rest = cxt.to - cxt.cursor;

        if (times < minTimes) {
            if (times > 0 && cxt.cursor <= prevOffset) {
                return false;
            }
            if (rest < next.minInput + body.minInput * (minTimes - times)) {
                return false; // fast-fail
            }
            cxt.addBackPoint(this, cxt.cursor, ((long) times << 32) | Integer.MAX_VALUE);
            return matchBody(cxt, times, cxt.cursor);
        }

        // fast-fail
        if (rest < next.minInput) {
            return false;
        }

        if (times >= maxTimes) {
            // if body is complex, need add back-point to recover loop status.
            if (complex) {
                cxt.addBackPoint(this, cxt.cursor, ((long) times << 32) | Integer.MAX_VALUE);
            }
            return matchNext(cxt);
        }

        if (backtracking) {
            if (type != Util.LAZY) {
                cxt.addBackPoint(this, cxt.cursor, ((long) times << 32) | Integer.MAX_VALUE);
                return matchNext(cxt);
            }
            if (times > 0 && cxt.cursor <= prevOffset) {
                return false; // treat empty match as failure
            }
            if (rest < body.minInput + next.minInput) {
                return false; // fast fail
            }
            return matchBody(cxt, times, cxt.cursor);
        }

        // LAZY: go to next first
        if (type == Util.LAZY) {
            cxt.addBackPoint(this, cxt.cursor, ((long) times << 32) | prevOffset); // prevOffset is important
            return matchNext(cxt);
        }

        // treat empty match as failure
        if (times > 0 && cxt.cursor <= prevOffset) {
            return false;
        }

        // if rest isn't enough for body+next, goto next directly
        if (type != Util.POSSESSIVE && rest < body.minInput + next.minInput) {
            return matchNext(cxt);
        }

        if (type == Util.POSSESSIVE && rest < body.minInput) {
            return matchNext(cxt);
        }

        cxt.addBackPoint(this, cxt.cursor, ((long) times << 32) | cxt.cursor);
        return matchBody(cxt, times, cxt.cursor);
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

    private boolean matchBody(ReContext cxt, int times, int offset) {
        cxt.localVars[timesVar] = times + 1;
        cxt.localVars[offsetVar] = offset;
        cxt.node = body;
        return body.match(cxt);
    }

    private boolean matchNext(ReContext cxt) {
        cxt.localVars[timesVar] = -1;
        cxt.localVars[offsetVar] = -1;
        cxt.localVars[deepVar] = -1;
        cxt.node = next;
        return next.match(cxt);
    }

}
