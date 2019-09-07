package com.github.sisyphsu.retree;

/**
 * This node supports one single char, like 'A'
 *
 * @author sulin
 * @since 2019-09-02 12:08:45
 */
public final class CharSingleNode extends CharNode {

    private final int ch;

    public CharSingleNode(int c) {
        super(true);
        this.ch = c;
    }

    @Override
    public String toString() {
        return String.valueOf((char) ch);
    }

    @Override
    protected boolean isMatch(int ch) {
        return (ch == this.ch);
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof CharSingleNode
                && ch == ((CharSingleNode) node).ch
                && matched == ((CharSingleNode) node).matched;
    }
}