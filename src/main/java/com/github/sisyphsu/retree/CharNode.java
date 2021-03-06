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
            this.minInput = 1;
            if (next != null) {
                next.study();
                this.minInput += next.minInput;
            }
        }
    }

    @Override
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        if (minInput > matcher.to - cursor || matched != isMatch(input.charAt(cursor))) {
            return false;
        }
        // switch to next
        if (next == null) {
            matcher.last = cursor + 1;
            return true;
        }
        return next.match(matcher, input, cursor + 1);
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