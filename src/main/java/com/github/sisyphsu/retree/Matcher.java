package com.github.sisyphsu.retree;

import com.github.sisyphsu.retree.node.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Matcher could match lots regular expressions concurrently which are merged as ReTree.
 * <p>
 * Matcher shouldn't be used in multiple threads concurrently, it's not thread-safe.
 *
 * @author sulin
 * @since 2019-09-02 11:04:22
 */
public final class Matcher {

    private static final int MATCH_FAIL = -1;
    private static final int MATCH_DONE = 1;
    private static final int MATCH_MATCH = 0;
    private static final int MATCH_SPLIT = 2;

    final ReTree tree;
    final List<MatchContext> contexts = new ArrayList<>(2);
    final List<MatchContext> contextPool = new ArrayList<>(2);
    final List<MatchContext> results = new ArrayList<>(1);

    private int from;
    private int to;
    private CharSequence input;

    private int last;

    /**
     * Initialize Matcher by the specified regular expression tree and input.
     *
     * @param tree  ReTree represents multiple regular expression.
     * @param input The default input, which could be changed by {{@link #reset(CharSequence)}}
     */
    public Matcher(ReTree tree, CharSequence input) {
        this.tree = tree;
        this.from = 0;
        this.to = input.length();
        this.input = input;
    }

    /**
     * Reset this matcher by another input, could be used for reusing
     *
     * @param input New input
     */
    public Matcher reset(CharSequence input) {
        this.from = 0;
        this.to = input.length();
        this.input = input;
        this.last = 0;
        return this;
    }

    /**
     * Execute regular expression matching operation, and return the result.
     *
     * @return MatchResult if success
     */
    public MatchResult matches() {
        if (search(0)) {
            for (MatchContext result : results) {
                if (result.end() == to) {
                    return result;
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
    public MatchResult find() {
        MatchResult result = null;
        int fromPos = Math.max(0, this.last);
        int endPos = this.to - tree.root.getMinInput();
        for (int from = fromPos; from <= endPos; from++) {
            if (!search(from)) {
                continue;
            }
            if (results.size() == 1) {
                result = results.get(0);
            } else {
                result = tree.selector.select(results);
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
    private boolean search(final int from) {
        if (results.size() > 0) {
            contextPool.addAll(results);
            results.clear();
        }

        if (contexts.isEmpty()) {
            MatchContext rootCxt;
            if (contextPool.isEmpty()) {
                rootCxt = new MatchContext(this, tree);
            } else {
                rootCxt = contextPool.remove(contextPool.size() - 1);
            }
            rootCxt.reset(tree.root, this.input, this.from, this.to, from);
            contexts.add(rootCxt);
        }

        // execute retree search in multiple MatchContext
        for (int off = from; off <= to; off++) {
            for (int i = 0; i < contexts.size(); i++) {
                MatchContext cxt = contexts.get(i);
                switch (doMatch(cxt, off)) {
                    case MATCH_FAIL:
                        this.contexts.remove(i);
                        this.contextPool.add(cxt);
                        i--;
                        break;

                    case MATCH_DONE:
                        this.contexts.remove(i);
                        this.results.add(cxt);
                        i--;
                        break;

                    case MATCH_SPLIT:
                        i--;
                        break;
                }
            }
        }

        return results.size() > 0;
    }

    /**
     * Execute matching use the specified MatchContext, until fail or hit offset.
     * If fail, it would do backtracking
     *
     * @param cxt    The context of matching operation.
     * @param offset The final offset, which means the end.
     * @return result code
     */
    private int doMatch(MatchContext cxt, int offset) {
        int status = this.tryMatch(cxt, offset);
        while (status == MATCH_FAIL) {
            MatchContext.Point point = cxt.popStack();
            if (point == null) {
                break;
            }
            if (!point.node.onBack(cxt, point.data)) {
                continue; // this BackPoint is used for data restoring
            }
            cxt.setActivedNode(point.node);
            cxt.setCursor(point.offset);
            status = this.tryMatch(cxt, offset);
        }
        return status;
    }

    /**
     * Try to execute once matching, return util failed or hit offset.
     *
     * @param cxt    The context of matching operation.
     * @param offset The final offset, which means the end.
     * @return result code
     */
    private int tryMatch(MatchContext cxt, int offset) {
        Node node;
        while (cxt.getCursor() <= offset) {
            node = cxt.getActivedNode();
            switch (node.match(cxt, cxt.getInput(), cxt.getCursor())) {
                case Node.FAIL:
                    return MATCH_FAIL;
                case Node.DONE:
                    return MATCH_DONE;
                case Node.SPLIT:
                    return MATCH_SPLIT;
                case Node.SUCCESS:
                    cxt.setCursor(cxt.getCursor() + 1);
            }
        }
        return MATCH_MATCH;
    }

    /**
     * Fetch all MatchResult of the previous match operation.
     *
     * @return All MatchResult
     */
    public List<? extends MatchResult> getResults() {
        return results;
    }

}
