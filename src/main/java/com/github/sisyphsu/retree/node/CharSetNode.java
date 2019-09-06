package com.github.sisyphsu.retree.node;

import java.util.Arrays;

/**
 * This node supports [abcde], only supports Latin-1 char.
 *
 * @author sulin
 * @since 2019-09-02 12:11:33
 */
public final class CharSetNode extends CharNode {

    private final boolean[] bits = new boolean[256];

    public CharSetNode() {
        super(true);
    }

    @Override
    protected boolean isMatch(int ch) {
        return (ch < 256 && bits[ch]);
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof CharSetNode
                && Arrays.equals(bits, ((CharSetNode) node).bits)
                && matched == ((CharSetNode) node).matched;
    }

    public CharSetNode add(int c) {
        bits[c] = true;
        return this;
    }

}