package com.github.sisyphsu.retree;

import java.util.Map;

/**
 * This node supports regular expressions's end, all node-chain should have it at the last.
 *
 * @author sulin
 * @since 2019-08-26 10:38:45
 */
public final class EndNode extends Node {

    private final String re;
    private final Map<String, Integer> nameMap;

    private int localCount;
    private int groupCount;

    public EndNode(String re, Map<String, Integer> nameMap) {
        this.re = re;
        this.nameMap = nameMap;
    }

    @Override
    public void study() {
        this.minInput = 0;
    }

    @Override
    public int match(ReContext cxt) {
        cxt.groupVars[1] = cxt.cursor;
        return DONE;
    }

    @Override
    public boolean alike(Node node) {
        return false;
    }

    public String getRe() {
        return re;
    }

    public void setLocalCount(int localCount) {
        this.localCount = localCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }

    public int getLocalCount() {
        return localCount;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public Map<String, Integer> getNameMap() {
        return nameMap;
    }
}
