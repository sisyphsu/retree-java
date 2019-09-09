package com.github.sisyphsu.retree;

/**
 * GroupHead represents the group's head
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class GroupNode extends Node {

    private final int groupStartIndex;
    private final int groupEndIndex;
    private final Node tailNode;

    private final int groupIndex;

    public GroupNode(int groupIndex) {
        this.groupIndex = groupIndex;
        this.groupStartIndex = groupIndex * 2;
        this.groupEndIndex = groupIndex * 2 + 1;
        this.tailNode = new Tail();
    }

    @Override
    public boolean match(ReContext cxt) {
        int oldStartPos = cxt.groupVars[groupStartIndex];
        int oldEndPos = cxt.groupVars[groupEndIndex];

        if (groupIndex > 0) {
            cxt.groupVars[groupStartIndex] = cxt.cursor;
        }
        cxt.node = next;
        if (next.match(cxt)) {
            return true;
        }
        if (groupIndex > 0) {
            cxt.groupVars[groupStartIndex] = oldStartPos;
            cxt.groupVars[groupEndIndex] = oldEndPos;
        }
        return false;
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
        public boolean match(ReContext cxt) {
            if (groupIndex > 0) {
                cxt.groupVars[groupEndIndex] = cxt.cursor; // mark the end postion of this group
            }
            cxt.node = next;
            return next.match(cxt);
        }

        @Override
        public boolean alike(Node node) {
            return node instanceof Tail;
        }
    }

}
