package com.github.sisyphsu.retree;

/**
 * The Context of Matcher and MultiMatcher
 *
 * @author sulin
 * @since 2019-09-03 15:06:49
 */
public final class ReContext implements Result {

    private final ReMatcher matcher;
    final int[] localVars;
    final int[] groupVars;
    final int[] crossVars;

    int[] backs = new int[4];

    int from;
    int to;
    CharSequence input;

    int stackDeep;
    Point[] stack;

    int cursor;
    Node node;

    protected ReContext(ReMatcher matcher, ReTree tree) {
        this.matcher = matcher;
        this.input = matcher.input;
        this.from = matcher.from;
        this.to = matcher.to;
        this.localVars = new int[tree.localVarCount];
        this.groupVars = new int[tree.groupVarCount * 2];
        this.crossVars = new int[tree.crossVarCount];
        this.stackDeep = 0;
        this.stack = new Point[4];
        this.reset();
    }

    @SuppressWarnings("CopyConstructorMissesField")
    protected ReContext(ReContext cxt) {
        this.matcher = cxt.matcher;
        this.localVars = new int[cxt.localVars.length];
        this.groupVars = new int[cxt.groupVars.length];
        this.crossVars = new int[cxt.crossVars.length];
        this.stackDeep = 0;
        this.stack = new Point[Math.min(4, cxt.stackDeep)];
    }

    /**
     * Reset context
     */
    public void reset() {
        for (int i = 0; i < this.localVars.length; i++)
            this.localVars[i] = -1;
        for (int i = 0; i < this.crossVars.length; i++)
            this.crossVars[i] = -1;
        this.stackDeep = 0;
    }

    /**
     * Split and clone an new MatchContext from current instance
     *
     * @return new MatchContext instance
     */
    public ReContext split() {
        ReContext result = matcher.allocContext();
        // insure stack is enough
        if (result.stack.length < stack.length) {
            result.stack = new Point[stack.length];
        }
        // copy context's data
        result.from = this.from;
        result.to = this.to;
        result.input = this.input;
        result.cursor = this.cursor;
        result.stackDeep = this.stackDeep;
        System.arraycopy(this.stack, 0, result.stack, 0, this.stackDeep);
        System.arraycopy(this.localVars, 0, result.localVars, 0, this.localVars.length);
        System.arraycopy(this.groupVars, 0, result.groupVars, 0, this.groupVars.length);
        System.arraycopy(this.crossVars, 0, result.crossVars, 0, this.crossVars.length);

        return result;
    }

    public void addBackPoint(Node node, int offset, long data) {
        if (stackDeep >= this.stack.length) {
            Point[] newStack = new Point[stackDeep * 2];
            System.arraycopy(stack, 0, newStack, 0, stackDeep);
            this.stack = newStack;
        }
        this.stack[stackDeep++] = new Point(node, offset, data);
    }

    @Override
    public String re() {
        if (node instanceof EndNode) {
            return ((EndNode) node).getRe();
        }
        throw new IllegalStateException("Invalid MatchResult");
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

    @Override
    public int groupCount() {
        if (node instanceof EndNode) {
            return ((EndNode) node).getGroupCount() - 1;
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
