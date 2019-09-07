package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.ReMatchContext;

/**
 * This Node supports '\b' and '\B'.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class AnchorBoundNode extends Node {

    public static int NON_WORD = 0x0;
    public static int WORD = 0x3;

    private final int type;

    public AnchorBoundNode(int n) {
        type = n;
    }

    @Override
    public int match(ReMatchContext cxt, CharSequence input, int offset) {
        // execute matching
        boolean leftIsWord = false;
        boolean rightIsWord = false;
        if (offset > cxt.getFrom()) {
            leftIsWord = isWord(Character.codePointBefore(input, offset));
        }
        if (offset < cxt.getTo()) {
            rightIsWord = isWord(Character.codePointAt(input, offset));
        }

        if (type == WORD && leftIsWord == rightIsWord) {
            return FAIL; // must be bound of word
        }

        if (type == NON_WORD && leftIsWord != rightIsWord) {
            return FAIL; // must not be bound of word
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
        if (node instanceof AnchorBoundNode) {
            return ((AnchorBoundNode) node).type == this.type;
        }
        return false;
    }

}