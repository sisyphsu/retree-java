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
    public boolean match(ReContext cxt) {
        int rest = cxt.to - cxt.cursor;

        if (rest == 0) {
            cxt.node = next;
            return next.match(cxt);
        }

        if (this.absolute) {
            return false;
        }

        if (rest > 2) {
            return false;
        }

        // if has 2 chars remained, must be '\r\n'
        if (rest == 2) {
            if (cxt.input.charAt(cxt.cursor) != '\r') {
                return false;
            }
            cxt.cursor++;
            if (cxt.input.charAt(cxt.cursor) != '\n') {
                return false;
            }
            cxt.node = next;
            return next.match(cxt);
        }

        char ch = cxt.input.charAt(cxt.cursor);
        if (ch != '\n' && ch != '\r' && ch != '\u0085' && (ch | 1) != '\u2029') {
            return false;
        }
        cxt.cursor++;
        cxt.node = next;
        return next.match(cxt);
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof AnchorEndNode) {
            return absolute == ((AnchorEndNode) node).absolute;
        }
        return false;
    }

}