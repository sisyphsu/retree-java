package com.github.sisyphsu.retree;

import java.util.Arrays;
import java.util.regex.MatchResult;

/**
 * The Context of Matcher and MultiMatcher
 *
 * @author sulin
 * @since 2019-09-03 15:06:49
 */
public final class ReMatcher implements MatchResult {

    final int[] groupVars;
    final long[] loopVars;

    int[] backs = new int[4];

    int from;
    int to;
    CharSequence input;

    int stop;
    int last;
    private boolean hitEnd;
    private final ReTree tree;

    EndNode endNode;

    /**
     * Construction for convenience.
     *
     * @param res All regular expression
     */
    public ReMatcher(String... res) {
        this(new ReTree(res), "");
    }

    /**
     * Initialize Matcher by the specified regular expression tree and input.
     *
     * @param tree  ReTree represents multiple regular expression.
     * @param input The default input, which could be changed by {{@link #reset(CharSequence)}}
     */
    public ReMatcher(ReTree tree, CharSequence input) {
        this.tree = tree;
        this.loopVars = new long[tree.localVarCount];
        this.groupVars = new int[tree.groupVarCount * 2];
        this.reset(input);
        Arrays.fill(this.loopVars, -1);
    }

    /**
     * Reset this matcher by another input, could be used for reusing
     *
     * @param input New input
     * @return This
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
     * @return success or not
     */
    public boolean matches() {
        this.stop = 0;
        this.hitEnd = true;
        return search(0);
    }

    /**
     * Reset the current matcher's input, then execute matches.
     *
     * @param input New input
     * @return success or not
     */
    public boolean matches(CharSequence input) {
        this.reset(input);
        return this.matches();
    }

    /**
     * Execute once regular expression finding operation from the previous offset.
     *
     * @return success or not
     */
    public boolean find() {
        this.stop = this.to - tree.root.minInput;
        this.hitEnd = false;
        boolean succ = this.search(last);
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
        this.stop = offset;
        this.hitEnd = false;
        return search(offset);
    }

    /**
     * Search the next matched subsequence of input from the specified position.
     *
     * @return Success or not
     */
    private boolean search(int from) {
        for (int i = 0; i < groupVars.length; i++) {
            groupVars[i] = -1;
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
        if (endNode != null) {
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
        if (endNode != null) {
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
        if (endNode != null) {
            return input.subSequence(start(groupIndex), end(groupIndex)).toString();
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public int groupCount() {
        if (endNode != null) {
            return endNode.groupCount - 1;
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    /**
     * Fetch captured group by its name.
     *
     * @param groupName Group's name
     * @return Captured input
     */
    public CharSequence group(String groupName) {
        if (endNode != null) {
            Integer groupIndex = endNode.groupNameMap.get(groupName);
            if (groupIndex == null) {
                throw new IllegalArgumentException("groupName is invalid: " + groupName);
            }
            return this.group(groupIndex);
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    /**
     * Fetch the matched regular expression as string.
     * Throw exception if didn't match any regular expression.
     *
     * @return The regular expression has matched.
     */
    public String re() {
        if (endNode != null) {
            return endNode.re;
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    /**
     * Fetch the specified capture-group's name by its index.
     * Return null if this group hasn't name.
     * Throw exception if the index is invalid.
     * Throw exception if didn't match any regular expression.
     *
     * @param groupIndex group's index
     * @return return null if the group hasn't name.
     */
    public String groupName(int groupIndex) {
        if (endNode != null) {
            return endNode.groupNames.get(groupIndex);
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

}
