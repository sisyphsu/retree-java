package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.MatchContext;

/**
 * This Node supports '\b' and '\B'.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class ArchorBoundNode extends Node {

    private static final int LEFT = 0x1;  // 01
    private static final int RIGHT = 0x2; // 10

    public static int NONE = 0x0;  // 00
    public static int BOTH = 0x3;  // 11

    private final int type;

    public ArchorBoundNode(int n) {
        type = n;
    }

    @Override
    public int match(MatchContext cxt, CharSequence input, int offset) {
        if (cxt.getTo() - offset < minInput) {
            return FAIL;
        }

        // execute matching
        boolean left = false;
        boolean right = false;
        if (offset > cxt.getFrom()) {
            left = isWord(Character.codePointBefore(input, offset));
        }
        if (offset < cxt.getTo()) {
            right = isWord(Character.codePointAt(input, offset));
        }

        int curType = (left ^ right) ? (right ? LEFT : RIGHT) : NONE;
        if ((curType & type) <= 0) {
            return FAIL;
        }

        // switch to next
        cxt.setActivedNode(next);
        return CONTINE;
    }

    private boolean isWord(int ch) {
        return ch == '_' || Character.isLetterOrDigit(ch);
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof ArchorBoundNode) {
            return ((ArchorBoundNode) node).type == this.type;
        }
        return false;
    }

}