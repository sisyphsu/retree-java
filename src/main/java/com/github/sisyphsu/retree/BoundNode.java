package com.github.sisyphsu.retree;

/**
 * This Node supports '\b' and '\B'.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class BoundNode extends Node {

    public static int NON_WORD = 0;
    public static int WORD = 1;

    private final int type;

    public BoundNode(int n) {
        type = n;
    }

    @Override
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        // execute matching
        boolean leftIsWord = false;
        boolean rightIsWord = false;
        if (cursor > matcher.from) {
            leftIsWord = isWord(Character.codePointBefore(input, cursor));
        }
        if (cursor < matcher.to) {
            rightIsWord = isWord(Character.codePointAt(input, cursor));
        }
        if (type == WORD && leftIsWord == rightIsWord) {
            return false; // must be bound of word
        }
        if (type == NON_WORD && leftIsWord != rightIsWord) {
            return false; // must not be bound of word
        }
        // switch to next
        if (next == null) {
            matcher.last = cursor;
            return true;
        }
        return next.match(matcher, input, cursor);
    }

    private boolean isWord(int ch) {
        return ch == '_' || Character.isLetterOrDigit(ch);
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof BoundNode) {
            return ((BoundNode) node).type == this.type;
        }
        return false;
    }

}