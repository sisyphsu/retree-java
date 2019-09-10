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
    public boolean match(ReContext cxt, CharSequence input, int cursor) {
        if (groupIndex > 0) {
            if (cxt.stackDeep == 0) {
                long startOff = cxt.groupVars[groupStartIndex];
                long endOff = cxt.groupVars[groupEndIndex];
                cxt.addBackPoint(this, cursor, (startOff << 32) | endOff);
            }
            cxt.groupVars[groupStartIndex] = cursor;
        }
        return next.match(cxt, input, cursor);
    }

    @Override
    public boolean onBack(ReContext cxt, long data) {
        // restore the old start and end position.
        if (groupIndex > 0) {
            cxt.groupVars[groupStartIndex] = (int) (data >>> 32);
            cxt.groupVars[groupEndIndex] = (int) (data);
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
        public boolean match(ReContext cxt, CharSequence input, int cursor) {
            if (groupIndex > 0) {
                cxt.groupVars[groupEndIndex] = cursor; // mark the end postion of this group
            }
            return next.match(cxt, input, cursor);
        }

        @Override
        public boolean alike(Node node) {
            return node instanceof Tail;
        }
    }

}
