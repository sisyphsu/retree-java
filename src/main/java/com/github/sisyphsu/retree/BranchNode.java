package com.github.sisyphsu.retree;

import java.util.ArrayList;
import java.util.List;

/**
 * This node supports '|' branches.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class BranchNode extends Node {

    final List<Node> branches = new ArrayList<>(2);

    public BranchNode(List<Node> branches) {
        this.branches.addAll(branches);
    }

    public BranchNode(Node next, Node first, Node second) {
        this.next = next;
        this.add(first);
        this.add(second);
    }

    @Override
    public void study() {
        if (minInput < 0) {
            this.minInput = 0;
            int min = Integer.MAX_VALUE;
            for (Node branch : branches) {
                int currMin;
                if (branch == null) {
                    next.study();
                    currMin = next.minInput;
                } else {
                    branch.study();
                    currMin = branch.minInput;
                }
                min = Math.min(min, currMin);
            }
            this.minInput = min;
        }
    }

    @Override
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        int rest = matcher.to - cursor;

        Node node;
        for (Node branch : branches) {
            node = branch == null ? next : branch;
            if (rest >= node.minInput && node.match(matcher, input, cursor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof BranchNode) {
            if (branches.size() != ((BranchNode) node).branches.size()) {
                return false;
            }
            for (int i = 0; i < branches.size(); i++) {
                Node node1 = branches.get(i);
                Node node2 = ((BranchNode) node).branches.get(i);
                if (node1 == null && node2 == null) {
                    continue;
                }
                if (node1 == null || node2 == null || !node1.alike(node2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void add(Node node) {
        branches.add(node);
    }

}
