package com.github.sisyphsu.retree;

import java.util.regex.MatchResult;

/**
 * The Context of Matcher and MultiMatcher
 *
 * @author sulin
 * @since 2019-09-03 15:06:49
 */
public final class ReMatcher implements MatchResult {

    final int[] groupVars;
    final int[] localVars;

    int from;
    int to;
    CharSequence input;

    Node node;

    int stop;
    int last;
    private boolean hitEnd;
    private final ReTree tree;

    /**
     * Initialize Matcher by the specified regular expression tree and input.
     *
     * @param tree  ReTree represents multiple regular expression.
     * @param input The default input, which could be changed by {{@link #reset(CharSequence)}}
     */
    public ReMatcher(ReTree tree, CharSequence input) {
        this.tree = tree;
        this.localVars = new int[tree.localVarCount];
        this.groupVars = new int[tree.groupVarCount * 2];
        this.reset(input);
    }

    /**
     * Reset this matcher by another input, could be used for reusing
     *
     * @param input New input
     */
    public ReMatcher reset(CharSequence input) {
        this.input = input;
        this.from = 0;
        this.to = input.length();
        this.last = 0;
        return this;
    }

    /**
     * Execute regular expression matching operation
     *
     * @return success of not
     */
    public boolean matches() {
        this.stop = 0;
        this.hitEnd = true;
        return search(0);
    }

    /**
     * Execute once regular expression finding operation from the previous offset.
     *
     * @return success or not
     */
    public boolean find() {
        boolean succ = this.find(last);
        if (succ) {
            this.last = groupVars[1];
        }
        return succ;
    }

    /**
     * Execute once regular expression finding operation from the specefic offset.
     *
     * @param offset The starting position
     * @return success or not
     */
    public boolean find(int offset) {
        this.stop = this.to - tree.root.minInput;
        this.hitEnd = false;
        return search(offset);
    }

    /**
     * Search the next matched subsequence of input from the specified position.
     *
     * @return Success or not
     */
    private boolean search(int from) {
        for (int i = 0; i < this.localVars.length; i++) {
            this.localVars[i] = -1;
        }
        boolean success = tree.root.match(this, input, from);
        if (success && hitEnd && groupVars[1] != this.to) {
            success = false;
        }
        return success;
    }

    @Override
    public int start() {
        return this.start(0);
    }

    @Override
    public int start(int groupIndex) {
        if (node instanceof EndNode) {
            return this.groupVars[groupIndex * 2];
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public int end() {
        return this.end(0);
    }

    @Override
    public int end(int groupIndex) {
        if (node instanceof EndNode) {
            return this.groupVars[groupIndex * 2 + 1];
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public String group() {
        return group(0);
    }

    @Override
    public String group(int groupIndex) {
        if (node instanceof EndNode) {
            return input.subSequence(start(groupIndex), end(groupIndex)).toString();
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public int groupCount() {
        if (node instanceof EndNode) {
            return ((EndNode) node).getGroupCount() - 1;
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    public CharSequence group(String groupName) {
        if (node instanceof EndNode) {
            Integer groupIndex = ((EndNode) node).getNameMap().get(groupName);
            if (groupIndex == null) {
                throw new IllegalArgumentException("groupName is invalid: " + groupName);
            }
            return this.group(groupIndex);
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    public String re() {
        if (node instanceof EndNode) {
            return ((EndNode) node).getRe();
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

}
