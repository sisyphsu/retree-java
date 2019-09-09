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

    private final int pos;
    private final List<Node> branches = new ArrayList<>(2);

    public BranchNode(Node next, Node first, Node second, int pos) {
        this.next = next;
        this.pos = pos;
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
    public boolean match(ReContext cxt) {
        int rest = cxt.to - cxt.cursor;
        // pick the next branch
        int cursor = cxt.cursor;
        int branchIdx = 0;
        Node node;
        for (; branchIdx < branches.size(); branchIdx++) {
            node = branches.get(branchIdx);
            if (node == null) {
                node = next;
            }
            if (node.minInput <= rest && node.match(cxt)) {
                return true;
            }
            cxt.cursor = cursor; // recover the old cursor for backtracking
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
