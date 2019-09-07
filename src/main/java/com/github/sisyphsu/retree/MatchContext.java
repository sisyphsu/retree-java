package com.github.sisyphsu.retree;

import com.github.sisyphsu.retree.node.EndNode;
import com.github.sisyphsu.retree.node.Node;

import java.util.Arrays;

/**
 * The Context of Matcher and MultiMatcher
 *
 * @author sulin
 * @since 2019-09-03 15:06:49
 */
public final class MatchContext implements MatchResult {

    private final Matcher matcher;
    private final int[] localVars;
    private final int[] groupVars;
    private final int[] crossVars;

    private int from;
    private int to;
    private CharSequence input;

    private int stackDeep;
    private Point[] stack;

    private int cursor;
    private Node activedNode;

    protected MatchContext(Matcher matcher, ReTree tree) {
        this.matcher = matcher;
        this.localVars = new int[tree.localVarCount + 1];
        this.groupVars = new int[tree.groupVarCount * 2];
        this.crossVars = new int[tree.crossVarCount];
        this.stackDeep = 0;
        this.stack = new Point[4];
    }

    @SuppressWarnings("CopyConstructorMissesField")
    protected MatchContext(MatchContext cxt) {
        this.matcher = cxt.matcher;
        this.localVars = new int[cxt.localVars.length];
        this.groupVars = new int[cxt.groupVars.length];
        this.crossVars = new int[cxt.crossVars.length];
        this.stackDeep = 0;
        this.stack = new Point[Math.min(4, cxt.stackDeep)];
    }

    /**
     * Reset context
     *
     * @param node   new node that is actived
     * @param cursor new cursor after reset
     */
    public void reset(Node node, CharSequence input, int from, int to, int cursor) {
        Arrays.fill(this.localVars, -1);
        Arrays.fill(this.crossVars, -1);

        this.input = input;
        this.from = from;
        this.to = to;
        this.activedNode = node;
        this.cursor = cursor;
        this.stackDeep = 0;
    }

    /**
     * Split and clone an new MatchContext from current instance
     *
     * @return new MatchContext instance
     */
    public MatchContext split() {
        MatchContext result = matcher.allocContext();
        // insure stack is enough
        if (result.stack.length < this.stack.length) {
            result.stack = new Point[this.stack.length];
        }
        // copy context's data
        result.from = this.from;
        result.to = this.to;
        result.input = this.input;
        result.cursor = this.cursor;
        result.activedNode = this.activedNode;
        result.stackDeep = this.stackDeep;
        System.arraycopy(this.stack, 0, result.stack, 0, this.stackDeep);
        System.arraycopy(this.localVars, 0, result.localVars, 0, this.localVars.length);
        System.arraycopy(this.groupVars, 0, result.groupVars, 0, this.groupVars.length);
        System.arraycopy(this.crossVars, 0, result.crossVars, 0, this.crossVars.length);

        return result;
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public Node getActivedNode() {
        return activedNode;
    }

    public void setActivedNode(Node activedNode) {
        this.activedNode = activedNode;
    }

    public int getStackDeep() {
        return this.stackDeep;
    }

    public void setStackDeep(int deep) {
        this.stackDeep = deep;
    }

    public Point popStack() {
        return stackDeep == 0 ? null : stack[--stackDeep];
    }

    public void addBackPoint(Node node, int offset, long data) {
        if (stackDeep >= this.stack.length) {
            Point[] newStack = new Point[stackDeep * 2];
            System.arraycopy(stack, 0, newStack, 0, stackDeep);
            this.stack = newStack;
        }
        this.stack[stackDeep++] = new Point(node, offset, data);
    }

    public int getLoopVar(int index) {
        return this.localVars[index + 1];
    }

    public void setLoopVar(int index, int val) {
        this.localVars[index + 1] = val;
    }

    public int getTempVar() {
        return this.localVars[0];
    }

    public void setTempVar(int val) {
        this.localVars[0] = val;
    }

    public int getCrossVar(int index) {
        return this.crossVars[index];
    }

    public void setCrossVar(int index, int val) {
        this.crossVars[index] = val;
    }

    public int getGroupOffset(int index) {
        return this.groupVars[index];
    }

    public void setGroupOffset(int index, int start) {
        this.groupVars[index] = start;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public CharSequence getInput() {
        return input;
    }

    @Override
    public String re() {
        if (activedNode instanceof EndNode) {
            return ((EndNode) activedNode).getRe();
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public int start() {
        return this.start(0);
    }

    @Override
    public int start(int groupIndex) {
        if (activedNode instanceof EndNode) {
            return this.getGroupOffset(groupIndex * 2);
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public int end() {
        return this.end(0);
    }

    @Override
    public int end(int groupIndex) {
        if (activedNode instanceof EndNode) {
            return this.getGroupOffset(groupIndex * 2 + 1);
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public String group() {
        return group(0);
    }

    @Override
    public String group(int groupIndex) {
        if (activedNode instanceof EndNode) {
            return getInput().subSequence(start(groupIndex), end(groupIndex)).toString();
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public CharSequence group(String groupName) {
        if (activedNode instanceof EndNode) {
            Integer groupIndex = ((EndNode) activedNode).getNameMap().get(groupName);
            if (groupIndex == null) {
                throw new IllegalArgumentException("groupName is invalid: " + groupName);
            }
            return this.group(groupIndex);
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public int groupCount() {
        if (activedNode instanceof EndNode) {
            return ((EndNode) activedNode).getGroupCount() - 1;
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    /**
     * One point in backtracking stack.
     */
    protected static final class Point {

        final Node node;
        final int offset;
        final long data;

        public Point(Node node, int offset, long data) {
            this.node = node;
            this.offset = offset;
            this.data = data;
        }
    }

}
