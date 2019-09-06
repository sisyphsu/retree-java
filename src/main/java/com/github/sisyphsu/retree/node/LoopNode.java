package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.MatchContext;
import com.github.sisyphsu.retree.Util;

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

        tail.setNext(this);
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
    public int match(MatchContext cxt, CharSequence input, int offset) {
        int times = cxt.getLoopVar(timesVar);
        int prevOffset = cxt.getLoopVar(offsetVar);

        boolean backtracking = false;
        if (times < 0) {
            cxt.addBackPoint(this, offset, RESET);
            times = 0;
            prevOffset = 0;
        } else {
            backtracking = (times & FLAG) != 0;
            times &= ~FLAG;
        }

        // POSSESSIVE: clear all added back-point
        if (type == Util.POSSESSIVE) {
            int stackDeep = cxt.getLoopVar(deepVar);
            if (times > 0 && stackDeep >= 0) {
                cxt.resetStack(stackDeep);
            }
            cxt.setLoopVar(deepVar, cxt.getStackDeep()); // backup deep
        }

        int rest = cxt.getTo() - offset;

        if (times < minTimes) {
            if (times > 0 && offset <= prevOffset) {
                return FAIL;
            }
            if (rest < next.minInput + body.minInput * (minTimes - times)) {
                return FAIL; // fast-fail
            }
            cxt.addBackPoint(this, offset, ((long) times << 32) | Integer.MAX_VALUE);
            return goBody(cxt, times, offset);
        }

        // fast-fail
        if (rest < next.minInput) {
            return FAIL;
        }

        if (times >= maxTimes) {
            // if body is complex, need add back-point to recover loop status.
            if (complex) {
                cxt.addBackPoint(this, offset, ((long) times << 32) | Integer.MAX_VALUE);
            }
            return goNext(cxt);
        }

        if (backtracking) {
            if (type != Util.LAZY) {
                cxt.addBackPoint(this, offset, ((long) times << 32) | Integer.MAX_VALUE);
                return goNext(cxt);
            }
            if (times > 0 && offset <= prevOffset) {
                return FAIL; // treat empty match as failure
            }
            if (rest < body.minInput + next.minInput) {
                return FAIL; // fast fail
            }
            return goBody(cxt, times, offset);
        }

        // LAZY: go to next first
        if (type == Util.LAZY) {
            cxt.addBackPoint(this, offset, ((long) times << 32) | prevOffset); // prevOffset is important
            return goNext(cxt);
        }

        // treat empty match as failure
        if (times > 0 && offset <= prevOffset) {
            return FAIL;
        }

        // if rest isn't enough for body+next, goto next directly
        if (type != Util.POSSESSIVE && rest < body.minInput + next.minInput) {
            return goNext(cxt);
        }

        if (type == Util.POSSESSIVE && rest < body.minInput) {
            return goNext(cxt);
        }

        cxt.addBackPoint(this, offset, ((long) times << 32) | offset);
        return goBody(cxt, times, offset);
    }

    @Override
    public boolean onBack(MatchContext cxt, long data) {
        cxt.setLoopVar(deepVar, -1);
        if (data == RESET) {
            cxt.setLoopVar(timesVar, -1);
            cxt.setLoopVar(offsetVar, -1);
            return false;
        }
        int times = (int) (data >>> 32);
        int offset = (int) data;
        if (offset == Integer.MAX_VALUE) {
            cxt.setLoopVar(timesVar, times);
            cxt.setLoopVar(offsetVar, 0); // don't use the real offset, use 0 as default
            return false;
        }
        cxt.setLoopVar(timesVar, times | FLAG); // bind flags in times
        cxt.setLoopVar(offsetVar, offset);
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

    private int goBody(MatchContext cxt, int times, int offset) {
        cxt.setLoopVar(timesVar, times + 1);
        cxt.setLoopVar(offsetVar, offset);
        cxt.setActivedNode(body);
        return CONTINE;
    }

    private int goNext(MatchContext cxt) {
        cxt.setLoopVar(timesVar, -1);
        cxt.setLoopVar(offsetVar, -1);
        cxt.setLoopVar(deepVar, -1);
        cxt.setActivedNode(next);
        return CONTINE;
    }

}
