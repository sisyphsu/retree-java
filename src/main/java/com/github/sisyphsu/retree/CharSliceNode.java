package com.github.sisyphsu.retree;

import java.util.Arrays;

/**
 * This node support the original char slice, like string.
 *
 * @author sulin
 * @since 2019-09-11 21:40:04
 */
public class CharSliceNode extends Node {

    int[] chars;

    public CharSliceNode(int[] chars) {
        this.chars = chars;
    }

    @Override
    public void study() {
        if (this.minInput >= 0) {
            return;
        }
        this.minInput = chars.length;
        if (next != null) {
            next.study();
            this.minInput += next.minInput;
        }
    }

    @Override
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        if (matcher.to - cursor < chars.length) {
            return false;
        }
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != input.charAt(cursor + i)) {
                return false;
            }
        }
        if (next == null) {
            matcher.last = cursor + chars.length;
            return true;
        }
        return next.match(matcher, input, cursor + chars.length);
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof CharSliceNode && Arrays.equals(chars, ((CharSliceNode) node).chars);
    }

}
