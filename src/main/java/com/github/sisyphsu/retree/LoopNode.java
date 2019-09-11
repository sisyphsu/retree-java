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

    private static final long BACK_FLAG = 1L << 61;

    final int type;
    final int minTimes;
    final int maxTimes;
    final Node body;

    private final int timesVar;

    public LoopNode(Node head, Node tail, int cmin, int cmax, int type, int localOffset) {
        this.body = head;
        this.type = type;
        this.minTimes = cmin;
        this.maxTimes = cmax;

        this.timesVar = localOffset;

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
            if (timesVar != ((LoopNode) node).timesVar)
                return false;
            return body.alike(((LoopNode) node).body);
        }
        return false;
    }

    @Override
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        final long var = matcher.loopVars[timesVar];

        if (var < 0) {
            matcher.loopVars[timesVar] = 0;
        }

        boolean result = this.doMatch(matcher, input, cursor);

        if (var < 0) {
            matcher.loopVars[timesVar] = -1;
        }

        return result;
    }

    private boolean doMatch(ReMatcher matcher, CharSequence input, int cursor) {
        long var = matcher.loopVars[timesVar];
        int times = (int) (var & 0x3FFFFFFF);
        int offset = (int) ((var >>> 30) & 0x3FFFFFFF);

        final int rest = matcher.to - cursor;
        final boolean preEmpty = times > 0 && cursor == offset;

        // minTimes frist
        if (times < minTimes) {
            if (preEmpty || rest < body.minInput + next.minInput) {
                return false; // fast fail or empty match
            }
            matcher.loopVars[timesVar] = (times + 1L) | ((long) cursor << 30);
            return body.match(matcher, input, cursor);
        }

        // lazy is special
        if (type == LAZY) {
            matcher.loopVars[timesVar] = -1; // clean for nested loop
            if (next.match(matcher, input, cursor)) {
                return true;
            }
            if (preEmpty || rest < body.minInput) {
                return false; // fast fail or empty match
            }
            matcher.loopVars[timesVar] = (times + 1L) | ((long) cursor << 30);
            return body.match(matcher, input, cursor);
        }

        // greedy body
        if (!preEmpty && times < maxTimes && rest >= body.minInput) {
            matcher.loopVars[timesVar] = (times + 1L) | ((long) cursor << 30);
            if (body.match(matcher, input, cursor)) {
                return true;
            }
        }

        // if not possessive, goto next directly
        if (type != POSSESSIVE) {
            matcher.loopVars[timesVar] = -1; // clean for nested loop
            return next.match(matcher, input, cursor);
        }

        // possessive can't backtracking twice
        if ((matcher.loopVars[timesVar] & BACK_FLAG) != 0) {
            return false;
        }

        matcher.loopVars[timesVar] = -1; // clean for nested loop
        if (next.match(matcher, input, cursor)) {
            return true;
        }

        matcher.loopVars[timesVar] = var | BACK_FLAG; // tell upper to fail directly
        return false;
    }

}
