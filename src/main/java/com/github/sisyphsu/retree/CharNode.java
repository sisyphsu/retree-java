package com.github.sisyphsu.retree;

/**
 * This node is the base class for char matching
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public abstract class CharNode extends Node {

    protected boolean matched;

    protected CharNode(boolean matched) {
        this.matched = matched;
    }

    @Override
    public void study() {
        if (minInput < 0) {
            this.minInput = 0;
            next.study();
            this.minInput = next.minInput + 1;
        }
    }

    @Override
    public boolean match(ReContext cxt) {
        if (cxt.to - cxt.cursor < minInput || matched != isMatch(cxt.input.charAt(cxt.cursor))) {
            return false;
        }
        // switch to next
        cxt.cursor++;
        cxt.node = next;
        return next.match(cxt);
    }

    public CharNode complement() {
        this.matched = !this.matched;
        return this;
    }

    /**
     * Detect the specified charcode is matched or not
     *
     * @param ch char code
     * @return matched or not
     */
    abstract boolean isMatch(int ch);

}