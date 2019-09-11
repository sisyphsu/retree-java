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

    private final int timesVar;
    private final int offsetVar;
    private final int stateVar;

    public LoopNode(Node head, Node tail, int cmin, int cmax, int type, int localOffset) {
        this.body = head;
        this.type = type;
        this.minTimes = cmin;
        this.maxTimes = cmax;

        this.timesVar = localOffset - 3;
        this.offsetVar = localOffset - 2;
        this.stateVar = localOffset - 1;

        tail.next = this;
    }

    @Override
    public void study() {
        if (minInput < 0) {
            this.minInput = 0;
            body.study();
            next.study();
            this.minInput = next.minInput + body.minInput * minTimes;
        }
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

    @Override
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        int times = matcher.localVars[timesVar];
        int offset = matcher.localVars[offsetVar];

        if (times > 0 && cursor <= offset) {
            return this.matchNext(matcher, input, cursor);
        }
        if (times < 0) {
            times = 0;
            matcher.localVars[timesVar] = 0;
            matcher.localVars[stateVar] = 0;
        }
        if (times < minTimes) {
            return this.matchBody(matcher, input, cursor);
        }
        if (type == LAZY && this.matchNext(matcher, input, cursor)) {
            return true;
        }
        if (times < maxTimes && this.matchBody(matcher, input, cursor)) {
            return true;
        }
        return this.matchNext(matcher, input, cursor);
    }

    private boolean matchBody(ReMatcher cxt, CharSequence input, int cursor) {
        int oldTimes = cxt.localVars[timesVar];
        cxt.localVars[timesVar] = oldTimes + 1;
        cxt.localVars[offsetVar] = cursor;
        boolean succ = body.match(cxt, input, cursor);
        if (!succ) {
            cxt.localVars[timesVar] = oldTimes;
        }
        return succ;
    }

    private boolean matchNext(ReMatcher cxt, CharSequence input, int cursor) {
        int oldTimes = cxt.localVars[timesVar];
        if (type == POSSESSIVE) {
            if (cxt.localVars[stateVar] > 0) {
                cxt.localVars[timesVar] = -1;
                return false;
            }
            cxt.localVars[stateVar] = 1;
        }
        cxt.localVars[timesVar] = -1; // clean current times
        boolean succ = next.match(cxt, input, cursor);
        if (!succ) {
            cxt.localVars[timesVar] = oldTimes;
        }
        return succ;
    }

}
