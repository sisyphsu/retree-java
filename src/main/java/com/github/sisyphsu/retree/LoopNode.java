package com.github.sisyphsu.retree;

import static com.github.sisyphsu.retree.Util.LAZY;
import static com.github.sisyphsu.retree.Util.POSSESSIVE;

/**
 * This node supports all loop match, like '+', '++', '{1,10}' etc.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class LoopNode extends Node {

    private final int type;
    private final int minTimes;
    private final int maxTimes;
    private final Node body;

    private final int timesPos;
    private final int offsetPos;
    private final int statePos;

    private boolean complex = false;

    public LoopNode(Node head, Node tail, int cmin, int cmax, int type, int localOffset) {
        this.body = head;
        this.type = type;
        this.minTimes = cmin;
        this.maxTimes = cmax;

        this.timesPos = localOffset - 3;
        this.offsetPos = localOffset - 2;
        this.statePos = localOffset - 1;

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
        int rest = cxt.to - cxt.cursor;
        int times = cxt.localVars[timesPos];
        if (times <= 0) {
            cxt.localVars[timesPos] = 0;
            cxt.localVars[statePos] = 0;
            cxt.localVars[offsetPos] = 0;
            times = 0;
        }
        if (rest < next.minInput) {
            return false; // fast fail
        }
        if (times < minTimes) {
            if (rest < body.minInput + next.minInput) {
                return false; // fast fail
            }
            return this.matchBody(cxt);
        }
        if (times > 0 && cxt.cursor <= cxt.localVars[offsetPos]) {
            return this.matchNext(cxt);
        }
        if (type == LAZY && this.matchNext(cxt)) {
            return true;
        }
        if (times < maxTimes && this.matchBody(cxt)) {
            return true;
        }
        return this.matchNext(cxt);
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
            if (offsetPos != ((LoopNode) node).offsetPos)
                return false;
            return body.alike(((LoopNode) node).body);
        }
        return false;
    }

    private boolean matchBody(ReContext cxt) {
        int oldTimes = cxt.localVars[timesPos];
        int oldOffset = cxt.localVars[offsetPos];
        cxt.localVars[timesPos] = oldTimes + 1;
        cxt.localVars[offsetPos] = cxt.cursor;
        cxt.node = body;
        boolean succ = (cxt.to - cxt.cursor >= body.minInput + next.minInput) && body.match(cxt);
        if (!succ) {
            cxt.localVars[timesPos] = oldTimes;
            cxt.localVars[offsetPos] = oldOffset;
        }
        return succ;
    }

    private boolean matchNext(ReContext cxt) {
        if (type == POSSESSIVE) {
            if (cxt.localVars[statePos] > 0) {
                return false; // Possessive could only backtracking once.
            }
            cxt.localVars[statePos] = 1;
        }
        cxt.node = next;
        return (cxt.to - cxt.cursor >= next.minInput) && next.match(cxt);
    }

}
