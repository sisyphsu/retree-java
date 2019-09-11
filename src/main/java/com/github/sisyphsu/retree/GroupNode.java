package com.github.sisyphsu.retree;

/**
 * GroupHead represents the group's head
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class GroupNode extends Node {

    private final int groupIndex;
    private final Node tailNode;

    private final int groupStartIndex;
    private final int groupEndIndex;

    public GroupNode(int groupIndex) {
        this.groupIndex = groupIndex;
        if (groupIndex > 0) {
            this.groupStartIndex = groupIndex * 2;
            this.groupEndIndex = groupIndex * 2 + 1;
        } else {
            this.groupStartIndex = 0;
            this.groupEndIndex = 0;
        }
        this.tailNode = new Tail();
    }

    @Override
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        int startOff = matcher.groupVars[groupStartIndex];
        int endOff = matcher.groupVars[groupEndIndex];
        if (groupIndex > 0) {
            matcher.groupVars[groupStartIndex] = cursor;
        }
        boolean succ = next.match(matcher, input, cursor);
        if (!succ && groupIndex > 0) {
            matcher.groupVars[groupStartIndex] = startOff;
            matcher.groupVars[groupEndIndex] = endOff;
        }
        return succ;
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof GroupNode) {
            return this.groupIndex == ((GroupNode) node).groupIndex;
        }
        return false;
    }

    public Node getTailNode() {
        return tailNode;
    }

    private class Tail extends Node {

        @Override
        public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
            if (groupIndex > 0) {
                matcher.groupVars[groupEndIndex] = cursor; // mark the end postion of this group
            }
            return next.match(matcher, input, cursor);
        }

        @Override
        public boolean alike(Node node) {
            return node instanceof Tail;
        }
    }

}
