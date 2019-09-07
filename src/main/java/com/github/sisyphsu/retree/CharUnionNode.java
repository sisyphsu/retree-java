package com.github.sisyphsu.retree;

/**
 * This node supports multiple CharNode's union
 *
 * @author sulin
 * @since 2019-09-02 12:25:57
 */
public final class CharUnionNode extends CharNode {

    private final CharNode prev;
    private final CharNode next;

    public CharUnionNode(CharNode prev, CharNode next) {
        super(true);
        this.prev = prev;
        this.next = next;
    }

    @Override
    protected boolean isMatch(int ch) {
        return prev.matched == prev.isMatch(ch) || next.matched == next.isMatch(ch);
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof CharUnionNode
                && prev.alike(((CharUnionNode) node).prev)
                && next.alike(((CharUnionNode) node).next)
                && matched == ((CharUnionNode) node).matched;
    }

}
