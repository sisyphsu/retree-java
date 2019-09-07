package com.github.sisyphsu.retree;

/**
 * This node supports whitespace char like '\V', '\v', '\H', '\h'.
 *
 * @author sulin
 * @since 2019-09-02 12:20:03
 */
public final class CharWhitespaceNode extends CharNode {

    private final boolean horizontal;

    public CharWhitespaceNode(boolean horizontal, boolean matched) {
        super(matched);
        this.horizontal = horizontal;
    }

    @Override
    protected boolean isMatch(int cp) {
        if (horizontal) {
            return (cp == 0x09 || cp == 0x20 || cp == 0xa0
                    || cp == 0x1680 || cp == 0x180e || cp >= 0x2000 && cp <= 0x200a
                    || cp == 0x202f || cp == 0x205f || cp == 0x3000);
        }
        return (cp >= 0x0A && cp <= 0x0D) || cp == 0x85 || cp == 0x2028 || cp == 0x2029;
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof CharWhitespaceNode) {
            return horizontal == ((CharWhitespaceNode) node).horizontal
                    && matched == ((CharWhitespaceNode) node).matched;
        }
        return false;
    }

}
