package com.github.sisyphsu.retree;

/**
 * Node is all regular expression node's base class.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public abstract class Node {

    protected Node next;
    protected int minInput = -1;

    /**
     * Try to study this node's global status, used for performance optimization.
     */
    public void study() {
        if (minInput < 0) {
            minInput = 0;
            next.study();
            minInput = next.minInput;
        }
    }

    /**
     * Execute once matching operation
     *
     * @param cxt The context of matching operation.
     * @return result code
     */
    public abstract boolean match(ReContext cxt, CharSequence input, int cursor);

    /**
     * The callback of backtracking, this node may need to do some data recovering.
     *
     * @param cxt  The context of matching operation.
     * @param data The data attached on back-point.
     * @return false means this node didn't want to retry matching.
     */
    public boolean onBack(ReContext cxt, long data) {
        return true;
    }

    public abstract boolean alike(Node node);

}