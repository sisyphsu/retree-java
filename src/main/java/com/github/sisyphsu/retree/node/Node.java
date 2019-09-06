package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.MatchContext;

/**
 * Node is all regular expression node's base class.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public abstract class Node {

    public static final int FAIL = -1;
    public static final int CONTINE = 0;
    public static final int SUCCESS = 1;
    public static final int DONE = 2;
    public static final int SPLIT = 3;

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
     * @param cxt    The context of matching operation.
     * @param input  The input charsequence.
     * @param offset Match position
     * @return result code
     */
    public abstract int match(MatchContext cxt, CharSequence input, int offset);

    /**
     * The callback of backtracking, this node may need to do some data recovering.
     *
     * @param cxt  The context of matching operation.
     * @param data The data attached on back-point.
     * @return false means this node didn't want to retry matching.
     */
    public boolean onBack(MatchContext cxt, long data) {
        return true;
    }

    public final void setNext(Node next) {
        this.next = next;
    }

    public final Node getNext() {
        return next;
    }

    public abstract boolean alike(Node node);

    public final int getMinInput() {
        return minInput;
    }
}