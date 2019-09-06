package com.github.sisyphsu.retree;

import com.github.sisyphsu.retree.node.EndNode;
import com.github.sisyphsu.retree.node.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Context of Matcher and MultiMatcher
 *
 * @author sulin
 * @since 2019-09-03 15:06:49
 */
public final class MatchContext implements MatchResult {

    private final List<Point> stack;

    private final Matcher matcher;
    private final int[] localVars;
    private final int[] groupVars;
    private final int[] crossVars;

    private CharSequence input;
    private int from;
    private int to;

    private int cursor;
    private Node activedNode;

    protected MatchContext(Matcher matcher, ReTree tree) {
        this.matcher = matcher;
        this.stack = new ArrayList<>(16);
        this.localVars = new int[tree.localVarCount + 1];
        this.groupVars = new int[tree.groupVarCount * 2];
        this.crossVars = new int[tree.crossVarCount];
    }

    @SuppressWarnings("CopyConstructorMissesField")
    protected MatchContext(MatchContext cxt) {
        this.matcher = cxt.matcher;
        this.stack = new ArrayList<>(Math.min(16, cxt.stack.size()));
        this.localVars = new int[cxt.localVars.length];
        this.groupVars = new int[cxt.groupVars.length];
        this.crossVars = new int[cxt.crossVars.length];
    }

    /**
     * Reset context
     *
     * @param node   new node that is actived
     * @param cursor new cursor after reset
     */
    public void reset(Node node, CharSequence input, int from, int to, int cursor) {
        Arrays.fill(this.localVars, -1);
        Arrays.fill(this.groupVars, -1);
        Arrays.fill(this.crossVars, -1);

        this.input = input;
        this.from = from;
        this.to = to;
        this.activedNode = node;
        this.cursor = cursor;
        this.stack.clear();
    }

    /**
     * Split and clone an new MatchContext from current instance
     *
     * @return new MatchContext instance
     */
    public MatchContext split() {
        MatchContext result;
        if (matcher.contextPool.isEmpty()) {
            result = new MatchContext(this);
        } else {
            result = matcher.contextPool.remove(matcher.contextPool.size() - 1);
        }
        // copy context's data
        result.from = from;
        result.to = to;
        result.input = input;
        result.cursor = cursor;
        result.activedNode = activedNode;
        result.stack.clear();
        result.stack.addAll(stack);
        System.arraycopy(localVars, 0, result.localVars, 0, localVars.length);
        System.arraycopy(groupVars, 0, result.groupVars, 0, groupVars.length);
        System.arraycopy(crossVars, 0, result.crossVars, 0, crossVars.length);

        matcher.contexts.add(result);
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
        return this.stack.size();
    }

    public void resetStack(int deep) {
        for (int size = stack.size(); size > deep; size = stack.size()) {
            stack.remove(size - 1);
        }
    }

    public Point popStack() {
        return stack.isEmpty() ? null : stack.remove(stack.size() - 1);
    }

    public void addBackPoint(Node node, int offset, long data) {
        this.stack.add(new Point(node, offset, data));
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

    public int getGroupStart(int groupIndex) {
        return this.groupVars[groupIndex * 2];
    }

    public int getGroupEnd(int groupIndex) {
        return this.groupVars[groupIndex * 2 + 1];
    }

    public void setGroupStart(int groupIndex, int start) {
        this.groupVars[groupIndex * 2] = start;
    }

    public void setGroupEnd(int groupIndex, int end) {
        this.groupVars[groupIndex * 2 + 1] = end;
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
    public int start(int group) {
        if (activedNode instanceof EndNode) {
            return this.getGroupStart(group);
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public int end() {
        return this.end(0);
    }

    @Override
    public int end(int group) {
        if (activedNode instanceof EndNode) {
            return this.getGroupEnd(group);
        }
        throw new IllegalStateException("Invalid MatchResult");
    }

    @Override
    public String group() {
        return group(0);
    }

    @Override
    public String group(int group) {
        if (activedNode instanceof EndNode) {
            return getInput().subSequence(start(group), end(group)).toString();
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
