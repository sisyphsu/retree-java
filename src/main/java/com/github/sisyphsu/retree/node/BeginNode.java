package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.MatchContext;

/**
 * Normal beginning node, should be the first node of all node-chain.
 *
 * @author sulin
 * @since 2019-09-02 19:51:09
 */
public final class BeginNode extends Node {

    public BeginNode(Node next) {
        setNext(next);
    }

    @Override
    public int match(MatchContext cxt, CharSequence input, int offset) {
        if (cxt.getTo() - offset < minInput) {
            return FAIL;
        }

        cxt.setGroupStart(0, offset);
        cxt.setActivedNode(next);
        return CONTINE;
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof BeginNode;
    }

}
