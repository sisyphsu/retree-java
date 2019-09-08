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
        this.contexts = new ReContext[2];
        this.contexts[0] = new ReContext(this, tree);
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
        for (ReContext context : this.contexts) {
            if (context != null) {
                context.from = this.from;
                context.to = this.to;
                context.input = this.input;
            }
        }
        return this;
    }

    /**
     * Execute regular expression matching operation, and return the result.
     *
     * @return MatchResult if success
     */
    public Result matches() {
        if (search(0)) {
            for (int i = 0; i < donePos; i++) {
                if (contexts[i].end() == to) {
                    return contexts[i];
                }
            }
        }
        return null;
    }

    /**
     * Execute once regular expression finding operation, and return the result.
     *
     * @return MatchResult if success
     */
    public Result find() {
        Result result = null;
        int fromPos = Math.max(0, this.last);
        int endPos = this.to - tree.root.minInput;
        for (int from = fromPos; from <= endPos; from++) {
            if (!search(from)) {
                continue;
            }
            if (donePos == 1) {
                result = contexts[0];
            } else {
                result = tree.selector.select(this.result);
            }
            break;
        }
        if (result != null) {
            this.last = result.end(); // update the last cursor for next round matching.
        }
        return result;
    }

    /**
     * Search the next matched subsequence of input from the specified position.
     *
     * @param from The beginning position for searching operation.
     * @return Success or not
     */
    public boolean search(final int from) {
        this.donePos = 0;
        this.matchPos = 1;
        this.contexts[0].reset(tree.root, from);

        // execute retree search in multiple MatchContext
        for (int off = from; off <= to; off++) {
            for (int i = donePos; i < matchPos; i++) {
                ReContext context = contexts[i];
                switch (doMatch(context, off)) {
                    case Node.FAIL:
                        this.matchPos--;
                        if (i != matchPos) {
                            this.contexts[i] = this.contexts[matchPos];
                            this.contexts[matchPos] = context; // put context into cache
                        }
                        i--;
                        break;

                    case Node.DONE:
                        if (i != donePos) {
                            this.contexts[i] = this.contexts[donePos];
                            this.contexts[donePos] = context; // put context into done
                        }
                        this.donePos++;
                        break;

                    case Node.SPLIT:
                        i--; // retry
                        break;
                }
            }
        }

        return donePos > 0;
    }

    /**
     * Execute matching use the specified MatchContext, until fail or hit offset.
     * If fail, it would do backtracking
     *
     * @param cxt    The context of matching operation.
     * @param offset The final offset, which means the end.
     * @return result code
     */
    private int doMatch(ReContext cxt, int offset) {
        int status = Node.CONTINE;
        while (cxt.cursor <= offset && status == Node.CONTINE) {
            status = cxt.node.match(cxt, input, cxt.cursor);
        }
        while (status == Node.FAIL) {
            ReContext.Point point = cxt.popStack();
            if (point == null) {
                break;
            }
            if (!point.node.onBack(cxt, point.data)) {
                continue; // this BackPoint is used for data restoring
            }
            cxt.node = point.node;
            cxt.cursor = point.offset;
            // backtracking
            status = Node.CONTINE;
            while (cxt.cursor <= offset && status == Node.CONTINE) {
                status = cxt.node.match(cxt, input, cxt.cursor);
            }
        }
        return status;
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
