package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.MatchContext;

/**
 * GroupHead represents the group's head
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class GroupNode extends Node {

    private final int groupIndex;
    private final Node tailNode;

    private final String groupName;

    public GroupNode(int groupIndex, String groupName) {
        this.groupIndex = groupIndex;
        this.groupName = groupName;
        this.tailNode = new Tail();
    }

    @Override
    public int match(MatchContext cxt, CharSequence input, int offset) {
        long startOff = cxt.getGroupStart(groupIndex);
        long endOff = cxt.getGroupEnd(groupIndex);

        cxt.addBackPoint(this, offset, (startOff << 32) | endOff);

        // record group's start position
        if (groupIndex > 0) {
            cxt.setGroupStart(groupIndex, offset);
        }

        cxt.setActivedNode(next);
        return CONTINE;
    }

    @Override
    public boolean onBack(MatchContext cxt, long data) {
        // restore the old start and end position.
        if (groupIndex > 0) {
            cxt.setGroupStart(groupIndex, (int) (data >>> 32));
            cxt.setGroupEnd(groupIndex, (int) (data));
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
        public int match(MatchContext cxt, CharSequence input, int offset) {
            // mark the end postion of this group
            if (groupIndex > 0) {
                cxt.setGroupEnd(groupIndex, offset);
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
