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
    public int match(ReContext cxt) {
        int rest = cxt.to - cxt.cursor;

        if (rest == 0) {
            cxt.node = next;
            return CONTINE;
        }

        if (this.absolute) {
            return FAIL;
        }

        if (rest > 2) {
            return FAIL;
        }

        // if has 2 chars remained, must be '\r\n'
        if (rest == 2) {
            if (cxt.input.charAt(cxt.cursor) != '\r')
                return FAIL;
            cxt.cursor++;
            return CONTINE;
        }

        // if previous char is '\r', so this char must be '\n'
        if (cxt.cursor > cxt.from && cxt.input.charAt(cxt.cursor - 1) == '\r') {
            if (cxt.input.charAt(cxt.cursor) != '\n')
                return FAIL;
            cxt.node = next;
            cxt.cursor++;
            return CONTINE;
        }

        char ch = cxt.input.charAt(cxt.cursor);
        if (ch != '\n' && ch != '\r' && ch != '\u0085' && (ch | 1) != '\u2029') {
            return FAIL;
        }
        cxt.node = next;
        cxt.cursor++;
        return CONTINE;
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof AnchorEndNode) {
            return absolute == ((AnchorEndNode) node).absolute;
        }
        return false;
    }

}