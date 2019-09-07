package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.ReMatchContext;

/**
 * This node support backreference, like '(\d)(\w)\2' could match '1aa', the '\2' could refer to '\w'.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class CharRefNode extends Node {

    private final int refGroupIndex;
    private final int refStartOffset;
    private final int refEndOffset;

    public CharRefNode(int refIndex) {
        this.refGroupIndex = refIndex;
        this.refStartOffset = refIndex * 2;
        this.refEndOffset = refIndex * 2 + 1;
    }

    @Override
    public int match(ReMatchContext cxt, CharSequence input, int offset) {
        final int groupStart = cxt.getGroupOffset(refStartOffset);
        final int groupEnd = cxt.getGroupOffset(refEndOffset);
        final int groupLen = groupEnd - groupStart;
        // fail if the group referenced is invalid
        if (groupStart < 0 || groupLen < 0) {
            return FAIL;
        }
        // continue if the group referenced is empty
        if (groupLen == 0) {
            cxt.setTempVar(-1);
            cxt.setActivedNode(next);
            return CONTINE;
        }

        int startOff = cxt.getTempVar();
        if (startOff < 0) {
            startOff = offset;
            cxt.setTempVar(startOff);
        }

        // fast fail
        if (cxt.getTo() - startOff < groupLen) {
            cxt.setTempVar(-1);
            return FAIL;
        }

        int refOffset = groupStart + (offset - startOff);

        // matched
        if (refOffset >= groupEnd) {
            cxt.setTempVar(-1);
            cxt.setActivedNode(next);
            return CONTINE;
        }

        // failed
        if (input.charAt(refOffset) != input.charAt(offset)) {
            cxt.setTempVar(-1);
            return FAIL;
        }

        // success, but not finished
        return SUCCESS;
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof CharRefNode) {
            return this.refGroupIndex == ((CharRefNode) node).refGroupIndex;
        }
        return false;
    }

}
