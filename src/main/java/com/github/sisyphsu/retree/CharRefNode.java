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
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        final int groupStart = matcher.groupVars[refStartOffset];
        final int groupEnd = matcher.groupVars[refEndOffset];
        final int groupLen = groupEnd - groupStart;
        // fast fail
        if (matcher.to - cursor < groupLen) {
            return false;
        }
        // do match
        if (groupLen > 0) {
            for (int i = 0; i < groupLen; i++) {
                if (input.charAt(groupStart + i) != input.charAt(cursor++)) {
                    return false;
                }
            }
        }
        if (next == null) {
            matcher.last = cursor;
            return true;
        }
        return next.match(matcher, input, cursor);
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof CharRefNode) {
            return this.refGroupIndex == ((CharRefNode) node).refGroupIndex;
        }
        return false;
    }

}
