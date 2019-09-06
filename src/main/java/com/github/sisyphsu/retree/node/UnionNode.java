package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.MatchContext;

/**
 * UnionNode is an special Node, which isn't created by Pattern, but ReTree.
 * It's used for uniting multiple regular expression.
 *
 * @author sulin
 * @since 2019-08-29 11:55:31
 */
public final class UnionNode extends Node {

    private final int index;
    private final Node[] nexts;

    public UnionNode(int index, Node[] nexts) {
        this.index = index;
        this.nexts = nexts;
    }

    @Override
    public void study() {
        if (minInput < 0) {
            this.minInput = 0;
            int min = Integer.MAX_VALUE;
            for (Node node : nexts) {
                node.study();
                min = Math.min(min, node.minInput);
            }
            this.minInput = min;
        }
    }

    @Override
    public int match(MatchContext cxt, CharSequence input, int offset) {
        int off = cxt.getCrossVar(index);
        if (off >= 0) {
            cxt.setActivedNode(nexts[off]);
            return CONTINE;
        }
        int rest = cxt.getTo() - offset;

        // fast-fail
        if (rest < this.minInput) {
            return FAIL;
        }

        // need split the context into multi-context.
        boolean first = true;
        for (int i = 0; i < nexts.length; i++) {
            if (rest < nexts[i].minInput) {
                continue; // fast-fail
            }
            if (first) {
                cxt.setCrossVar(index, i);
                first = false;
            } else {
                cxt.split().setCrossVar(index, i);
            }
        }
        return SPLIT;
    }

    @Override
    public boolean alike(Node node) {
        return false;
    }

}
