package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.MatchContext;

/**
 * This node support '^' and '\A'
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class AnchorStartNode extends Node {

    @Override
    public int match(MatchContext cxt, CharSequence input, int offset) {
        if (offset != cxt.getFrom()) {
            return FAIL;
        }

        cxt.setActivedNode(next);
        return CONTINE;
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof AnchorStartNode;
    }

}
