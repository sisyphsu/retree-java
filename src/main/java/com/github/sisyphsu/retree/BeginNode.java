package com.github.sisyphsu.retree;

/**
 * Normal beginning node, should be the first node of all node-chain.
 *
 * @author sulin
 * @since 2019-09-02 19:51:09
 */
public final class BeginNode extends Node {

    public BeginNode(Node next) {
        this.next = next;
    }

    @Override
    public boolean match(ReContext cxt, CharSequence input, int cursor) {
        if (cxt.to - cursor < minInput) {
            return false;
        }
        cxt.groupVars[0] = cursor;
        return next.match(cxt, input, cursor);
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof BeginNode;
    }

}
