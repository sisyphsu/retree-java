package com.github.sisyphsu.retree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This node supports regular expressions's end, all node-chain should have it at the last.
 *
 * @author sulin
 * @since 2019-08-26 10:38:45
 */
public final class EndNode extends Node {

    final String re;
    final List<String> groupNames = new ArrayList<>();
    final Map<String, Integer> groupNameMap = new HashMap<>();

    int localCount;
    int groupCount;

    public EndNode(String re) {
        this.re = re;
    }

    public void init(int localCount, int groupCount, Map<String, Integer> nameMap) {
        this.groupNames.clear();
        this.groupNameMap.clear();

        this.localCount = localCount;
        this.groupCount = groupCount;
        this.groupNameMap.putAll(nameMap);

        for (int i = 0; i < groupCount; i++) {
            this.groupNames.add(null); // null as unnamed
        }
        for (Map.Entry<String, Integer> entry : nameMap.entrySet()) {
            this.groupNames.set(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public void study() {
        this.minInput = 0;
    }

    @Override
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        matcher.endNode = this;
        matcher.groupVars[1] = cursor;
        return true;
    }

    @Override
    public boolean alike(Node node) {
        return false;
    }

}
