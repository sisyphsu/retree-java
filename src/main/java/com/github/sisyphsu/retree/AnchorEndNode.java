package com.github.sisyphsu.retree;

/**
 * This node supports '$' and '\Z'
 *
 * @author sulinµ
 * @since 2019-08-26 11:10:27
 */
public final class AnchorEndNode extends Node {

    private final boolean absolute;

    public AnchorEndNode(boolean absolute) {
        this.absolute = absolute;
    }

    @Override
    public boolean match(ReContext cxt, CharSequence input, int cursor) {
        int rest = cxt.to - cursor;

        if (rest == 0) {
            return next.match(cxt, input, cursor);
        }

        if (this.absolute) {
            return false;
        }

        if (rest > 2) {
            return false;
        }

        // if has 2 chars remained, must be '\r\n'
        if (rest == 2) {
            if (input.charAt(cursor) != '\r' || input.charAt(cursor + 1) != '\n') {
                return false;
            }
            return next.match(cxt, input, cursor + 2);
        }

        // if previous char is '\r', so this char must be '\n'
        if (cursor > cxt.from && input.charAt(cursor - 1) == '\r') {
            if (input.charAt(cursor) != '\n') {
                return false;
            }
            return next.match(cxt, input, cursor + 1);
        }

        char ch = input.charAt(cursor);
        if (ch != '\n' && ch != '\r' && ch != '\u0085' && (ch | 1) != '\u2029') {
            return false;
        }
        
        return next.match(cxt, input, cursor + 1);
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof AnchorEndNode) {
            return absolute == ((AnchorEndNode) node).absolute;
        }
        return false;
    }

}