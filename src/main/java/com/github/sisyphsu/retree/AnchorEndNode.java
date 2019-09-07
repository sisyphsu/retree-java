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
    public int match(ReMatchContext cxt, CharSequence input, int offset) {
        int rest = cxt.getTo() - offset;

        if (rest == 0) {
            cxt.setActivedNode(next);
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
            if (input.charAt(offset) != '\r')
                return FAIL;
            return SUCCESS;
        }

        // if previous char is '\r', so this char must be '\n'
        if (offset > cxt.getFrom() && input.charAt(offset - 1) == '\r') {
            if (input.charAt(offset) != '\n')
                return FAIL;
            cxt.setActivedNode(next);
            return SUCCESS;
        }

        char ch = input.charAt(offset);
        if (ch != '\n' && ch != '\r' && ch != '\u0085' && (ch | 1) != '\u2029') {
            return FAIL;
        }
        cxt.setActivedNode(next);
        return SUCCESS;
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof AnchorEndNode) {
            return absolute == ((AnchorEndNode) node).absolute;
        }
        return false;
    }

}