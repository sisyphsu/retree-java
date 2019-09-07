package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.ReMatchContext;

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
    private final String groupName;

    public GroupNode(int groupIndex, String groupName) {
        this.groupIndex = groupIndex;
        this.groupStartIndex = groupIndex * 2;
        this.groupEndIndex = groupIndex * 2 + 1;
        this.groupName = groupName;
        this.tailNode = new Tail();
    }

    @Override
    public int match(ReMatchContext cxt, CharSequence input, int offset) {
        if (groupIndex > 0) {
            long startOff = cxt.getGroupOffset(groupStartIndex);
            long endOff = cxt.getGroupOffset(groupEndIndex);

            cxt.addBackPoint(this, offset, (startOff << 32) | endOff);
            cxt.setGroupOffset(groupStartIndex, offset);
        }

        cxt.setActivedNode(next);
        return CONTINE;
    }

    @Override
    public boolean onBack(ReMatchContext cxt, long data) {
        // restore the old start and end position.
        if (groupIndex > 0) {
            cxt.setGroupOffset(groupStartIndex, (int) (data >>> 32));
            cxt.setGroupOffset(groupEndIndex, (int) (data));
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
        public int match(ReMatchContext cxt, CharSequence input, int offset) {
            // mark the end postion of this group
            if (groupIndex > 0) {
                cxt.setGroupOffset(groupEndIndex, offset);
            }

            cxt.setActivedNode(next);
            return CONTINE;
        }

        @Override
        public boolean alike(Node node) {
            return node instanceof Tail;
        }
    }

}
