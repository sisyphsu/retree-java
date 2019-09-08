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
    private int last;
    private int donePos;
    private int matchPos;
    private ReContext[] contexts;
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
        this.to = input.length();
        this.input = input;
        this.last = 0;
        for (ReContext cxt : this.contexts) {
            if (cxt == null) {
                continue;
            }
            cxt.from = this.from;
            cxt.to = this.to;
            cxt.input = this.input;
        }
        return this;
    }

    /**
     * Execute regular expression matching operation, and return the result.
     *
     * @return MatchResult if success
     */
    public Result matches() {
        this.hitEnd = true;
        if (search(0)) {
            return contexts[0];
        }
        return null;
    }

    /**
     * Execute once regular expression finding operation, and return the result.
     *
     * @return MatchResult if success
     */
    public Result find() {
        return this.find(last > 0 ? last : 0);
    }

    public Result find(int offset) {
        this.hitEnd = false;
        ReContext result = null;
        int endPos = this.to - tree.root.minInput;
        for (int from = offset; from <= endPos; from++) {
            if (search(from)) {
                if (donePos == 1) {
                    result = contexts[0];
                } else {
                    result = (ReContext) tree.selector.select(this.result);
                }
                break;
            }
        }
        if (result != null) {
            this.last = Math.max(result.groupVars[1], last + 1);
        }
        return result;
    }

    /**
     * Search the next matched subsequence of input from the specified position.
     *
     * @param from The beginning position for searching operation.
     * @return Success or not
     */
    private boolean search(final int from) {
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
        int status = Node.CONTINE;
        while (true) {
            switch (status) {
                case Node.CONTINE:
                case Node.SPLIT:
                    status = cxt.node.match(cxt, input, cxt.cursor);
                    break;
                case Node.FAIL:
                    ReContext.Point point = cxt.popStack();
                    if (point == null) {
                        return false;
                    }
                    if (!point.node.onBack(cxt, point.data)) {
                        continue;
                    }
                    cxt.node = point.node;
                    cxt.cursor = point.offset;
                    status = Node.CONTINE;
                    break;
                case Node.DONE:
                    return true;
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
     * Fetch all MatchResult of the previous match operation.
     *
     * @return All MatchResult
     */
    public List<? extends Result> getResults() {
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
