package com.github.sisyphsu.retree;

/**
 * Node is all regular expression node's base class.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public abstract class Node {

    transient int flag = 0;

    protected Node next;
    protected int minInput = -1;

    /**
     * Try to study this node's global status, used for performance optimization.
     */
    public void study() {
        if (minInput < 0) {
            minInput = 0;
            if (next != null) {
                next.study();
                minInput = next.minInput;
            }
        }
    }

    /**
     * Execute once matching operation
     *
     * @param matcher The context of matching operation
     * @param input   The original input
     * @param cursor  The position of matching operation
     * @return result code
     */
    public abstract boolean match(ReMatcher matcher, CharSequence input, int cursor);

    public abstract boolean alike(Node node);

    public int getMinInput() {
        return minInput;
    }
}