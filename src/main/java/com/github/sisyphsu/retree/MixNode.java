package com.github.sisyphsu.retree;

/**
 * UnionNode is an special Node, which isn't created by Pattern, but ReTree.
 * It's used for uniting multiple regular expression.
 *
 * @author sulin
 * @since 2019-08-29 11:55:31
 */
public final class MixNode extends Node {

    private final int index;
    private final Node[] nexts;

    public MixNode(int index, Node[] nexts) {
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
    public boolean match(ReContext cxt, CharSequence input, int cursor) {
        if (cxt.crossVars[index] < 0) {
            int rest = cxt.to - cursor;
            if (rest < this.minInput) {
                return false; // fast-fail
            }
            // need split the context into multi-context.
            boolean first = true;
            for (int i = 0; i < nexts.length; i++) {
                if (rest < nexts[i].minInput) {
                    continue; // fast-fail
                }
                if (first) {
                    cxt.crossVars[index] = i;
                    cxt.node = nexts[i];
                    cxt.cursor = cursor;
                    first = false;
                } else {
                    ReContext newCxt = cxt.split();
                    newCxt.crossVars[index] = i;
                    newCxt.node = nexts[i];
                    newCxt.cursor = cursor;
                }
            }
        }
        return cxt.node.match(cxt, input, cursor);
    }

    @Override
    public boolean alike(Node node) {
        return false;
    }

}
