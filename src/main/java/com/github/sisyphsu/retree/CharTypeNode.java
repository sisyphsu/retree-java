package com.github.sisyphsu.retree;

/**
 * This node supports escape char, like '\d' '\w'
 *
 * @author sulin
 * @since 2019-09-02 11:53:48
 */
public final class CharTypeNode extends CharNode {

    private final int ctype;

    public CharTypeNode(final int ctype, boolean matched) {
        super(matched);
        this.ctype = ctype;
    }

    @Override
    protected boolean isMatch(int ch) {
        return ch < 128 && Util.isType(ch, ctype);
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof CharTypeNode
                && ctype == ((CharTypeNode) node).ctype
                && matched == ((CharTypeNode) node).matched;
    }

}
