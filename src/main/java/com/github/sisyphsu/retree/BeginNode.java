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
    public int match(ReContext cxt, CharSequence input, int offset) {
        if (cxt.to - offset < minInput) {
            return FAIL;
        }

        cxt.groupVars[0] = offset;
        cxt.activedNode = next;
        return CONTINE;
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof BeginNode;
    }

}
