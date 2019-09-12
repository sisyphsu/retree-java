package com.github.sisyphsu.retree;

/**
 * This node supports escape char, like '\d' '\w'
 *
 * @author sulin
 * @since 2019-09-02 11:53:48
 */
public final class CharTypeNode extends CharNode {

    public static final int DIGIT = 1;
    public static final int SPACE = 2;
    public static final int WORD = 3;

    private final int ctype;

    public CharTypeNode(final int ctype, boolean matched) {
        super(matched);
        this.ctype = ctype;
    }

    @Override
    protected boolean isMatch(int ch) {
        if (ch < 128) {
            switch (ctype) {
                case DIGIT:
                    return DIGIT_MAP[ch];
                case WORD:
                    return WORD_MAP[ch];
                default:
                    return SPACE_MAP[ch];
            }
        }
        return false;
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof CharTypeNode
                && ctype == ((CharTypeNode) node).ctype
                && matched == ((CharTypeNode) node).matched;
    }

    private static final boolean[] DIGIT_MAP = new boolean[128];
    private static final boolean[] WORD_MAP = new boolean[128];
    private static final boolean[] SPACE_MAP = new boolean[128];

    static {
        for (int i = '0'; i <= '9'; i++) {
            DIGIT_MAP[i] = true;
            WORD_MAP[i] = true;
        }
        for (int i = 'a'; i <= 'z'; i++) {
            WORD_MAP[i] = true;
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            WORD_MAP[i] = true;
        }
        for (int i = 9; i <= 13; i++) {
            SPACE_MAP[i] = true;
        }
        WORD_MAP['_'] = true;
        SPACE_MAP[' '] = true;
    }
}
