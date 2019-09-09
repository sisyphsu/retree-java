package com.github.sisyphsu.retree;

import java.util.AbstractList;
import java.util.List;

/**
 * Matcher could match lots regular expressions concurrently which are merged as ReTree.
 * <p>
 * Matcher shouldn't be used in multiple threads concurrently, it's not thread-safe.
 *
 * @author sulin
 * @since 2019-09-02 11:04:22
 */
public final class ReMatcher {

    int from;
    int to;
    CharSequence input;

    private boolean hitEnd;
    private int donePos;
    private int matchPos;
    private ReContext[] contexts;
    private ReContext preResult;
    private final ReTree tree;

    /**
     * Initialize Matcher by the specified regular expression tree and input.
     *
     * @param tree  ReTree represents multiple regular expression.
     * @param input The default input, which could be changed by {{@link #reset(CharSequence)}}
     */
    public ReMatcher(ReTree tree, CharSequence input) {
        this.tree = tree;
        this.from = 0;
        this.to = input.length();
        this.input = input;
        this.donePos = 0;
        this.matchPos = 0;
        this.contexts = new ReContext[]{new ReContext(this, tree), null};
    }

    /**
     * Reset this matcher by another input, could be used for reusing
     *
     * @param input New input
     */
    public ReMatcher reset(CharSequence input) {
        this.from = 0;
        this.donePos = 0;
        this.matchPos = 0;
        this.preResult = null;
        this.to = input.length();
        this.input = input;
        for (ReContext cxt : this.contexts) {
            if (cxt == null) {
                continue;
            }
            cxt.from = this.from;
            cxt.to = this.to;
            cxt.input = this.input;
            cxt.reset();
        }
        return this;
    }

    /**
     * Execute regular expression matching operation
     *
     * @return success of not
     */
    public boolean matches() {
        this.hitEnd = true;
        return search(0);
    }

    /**
     * Execute once regular expression finding operation from the previous offset.
     *
     * @return success or not
     */
    public boolean find() {
        ReContext oldResult = (ReContext) this.getResult();
        if (oldResult == null) {
            return this.find(0);
        } else {
            return this.find(oldResult.groupVars[1]);
        }
    }

    /**
     * Execute once regular expression finding operation from the specefic offset.
     *
     * @param offset The starting position
     * @return success or not
     */
    public boolean find(int offset) {
        this.hitEnd = false;
        int endPos = this.to - tree.root.minInput;
        for (int from = offset; from <= endPos; from++) {
            if (search(from)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search the next matched subsequence of input from the specified position.
     *
     * @param from The beginning position for searching operation.
     * @return Success or not
     */
    private boolean search(final int from) {
        this.preResult = null;
        this.donePos = 0;
        this.matchPos = 1;

        ReContext cxt = this.contexts[0];
        cxt.node = tree.root;
        cxt.cursor = from;
        cxt.reset();

        for (int i = 0; i < matchPos; i++) {
            cxt = contexts[i];
            if (!doSearch(cxt)) {
                continue;
            }
            if (hitEnd && cxt.groupVars[1] != cxt.to) {
                continue;
            }
            // move to done area
            if (i != donePos) {
                this.contexts[i] = this.contexts[donePos];
                this.contexts[donePos] = cxt;
            }
            this.donePos++;
        }
        return donePos > 0;
    }

    /**
     * Execute matching use the specified MatchContext, until fail or hit offset.
     * If fail, it would do backtracking
     *
     * @param cxt The context of matching operation.
     * @return result code
     */
    private boolean doSearch(ReContext cxt) {
        while (true) {
            if (cxt.node.match(cxt)) {
                return true;
            }
            while (true) {
                if (cxt.stackDeep == 0) {
                    return false;
                }
                ReContext.Point point = cxt.stack[--cxt.stackDeep];
                if (point == null) {
                    return false;
                }
                if (point.node.onBack(cxt, point.data)) {
                    cxt.node = point.node;
                    cxt.cursor = point.offset;
                    break;
                }
            }
        }
    }

    /**
     * Allocate an new MatchContext, if has old cached contexts, use it directly
     *
     * @return New allocated MatchContext
     */
    protected ReContext allocContext() {
        if (this.matchPos >= this.contexts.length) {
            ReContext[] cxts = new ReContext[this.contexts.length * 2];
            System.arraycopy(this.contexts, 0, cxts, 0, this.contexts.length);
            this.contexts = cxts;
        }
        int offset = this.matchPos++;
        if (this.contexts[offset] == null) {
            this.contexts[offset] = new ReContext(this, tree);
        }
        return this.contexts[offset];
    }

    /**
     * Fetch the Result of previously matching operation.
     *
     * @return Result instance
     */
    public Result getResult() {
        if (preResult == null && donePos > 0) {
            if (donePos == 1) {
                preResult = contexts[0];
            } else {
                preResult = (ReContext) tree.selector.select(result);
            }
        }
        return preResult;
    }

    /**
     * Fetch all MatchResult of the previous match operation.
     *
     * @return All MatchResult
     */
    public List<? extends Result> getAllResult() {
        return result;
    }

    private final List<Result> result = new AbstractList<Result>() {
        @Override
        public Result get(int index) {
            if (index >= donePos) {
                throw new ArrayIndexOutOfBoundsException(String.format("index(%s) > size(%s)", index, donePos));
            }
            return contexts[index];
        }

        @Override
        public int size() {
            return donePos;
        }
    };

}
