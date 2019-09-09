package com.github.sisyphsu.retree;

/**
 * This node support backreference, like '(\d)(\w)\2' could match '1aa', the '\2' could refer to '\w'.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class CharRefNode extends Node {

    private final int refGroupIndex;
    private final int refStartOffset;
    private final int refEndOffset;

    public CharRefNode(int refIndex) {
        this.refGroupIndex = refIndex;
        this.refStartOffset = refIndex * 2;
        this.refEndOffset = refIndex * 2 + 1;
    }

    @Override
    public boolean match(ReContext cxt) {
        final int groupStart = cxt.groupVars[refStartOffset];
        final int groupEnd = cxt.groupVars[refEndOffset];
        final int groupLen = groupEnd - groupStart;
        // fail if the group referenced is invalid
        if (groupStart < 0 || groupLen < 0) {
            return false;
        }
        // continue if the group referenced is empty
        if (groupLen == 0) {
            cxt.node = next;
            return next.match(cxt);
        }
        // fast fail
        if (cxt.to - cxt.cursor < groupLen) {
            return false;
        }
        // do matching
        for (int i = 0; i < groupLen; i++) {
            if (cxt.input.charAt(cxt.cursor++) != cxt.input.charAt(groupStart + i)) {
                return false;
            }
        }
        cxt.node = next;
        return next.match(cxt);
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof CharRefNode) {
            return this.refGroupIndex == ((CharRefNode) node).refGroupIndex;
        }
        return false;
    }

}
