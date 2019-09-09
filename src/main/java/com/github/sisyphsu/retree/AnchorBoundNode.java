package com.github.sisyphsu.retree;

/**
 * This Node supports '\b' and '\B'.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class AnchorBoundNode extends Node {

    public static int NON_WORD = 0;
    public static int WORD = 1;

    private final int type;

    public AnchorBoundNode(int n) {
        type = n;
    }

    @Override
    public boolean match(ReContext cxt) {
        // execute matching
        boolean leftIsWord = false;
        boolean rightIsWord = false;
        if (cxt.cursor > cxt.from) {
            leftIsWord = isWord(Character.codePointBefore(cxt.input, cxt.cursor));
        }
        if (cxt.cursor < cxt.to) {
            rightIsWord = isWord(Character.codePointAt(cxt.input, cxt.cursor));
        }

        if (type == WORD && leftIsWord == rightIsWord) {
            return false; // must be bound of word
        }

        if (type == NON_WORD && leftIsWord != rightIsWord) {
            return false; // must not be bound of word
        }

        // switch to next
        cxt.node = next;
        return next.match(cxt);
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