package com.github.sisyphsu.retree;

/**
 * This CharNode supports '.', which means any char excludes whitespace.
 *
 * @author sulin
 * @since 2019-09-02 11:49:37
 */
public final class CharAnyNode extends CharNode {

    public CharAnyNode() {
        super(true);
    }

    @Override
    protected boolean isMatch(int ch) {
        return (ch != '\n' && ch != '\r' && (ch | 1) != '\u2029' && ch != '\u0085');
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof CharAnyNode
                && matched == ((CharAnyNode) node).matched;
    }

    @Override
    public String toString() {
        return ".";
    }

}
