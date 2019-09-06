package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.MatchContext;

import java.util.ArrayList;
import java.util.List;

/**
 * This node supports '|' branches.
 *
 * @author sulin
 * @since 2019-08-26 11:10:27
 */
public final class BranchNode extends Node {

    private final List<Node> branches = new ArrayList<>(2);

    public BranchNode(Node conn, Node first, Node second) {
        this.setNext(conn);
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
    public int match(MatchContext cxt, CharSequence input, int offset) {
        int branchIdx = Math.max(cxt.getTempVar(), 0);
        cxt.setTempVar(-1);

        // fast fail
        int rest = cxt.getTo() - offset;
        if (rest < next.minInput) {
            return FAIL;
        }

        // pick the next branch
        Node node = null;
        for (; branchIdx < branches.size(); branchIdx++) {
            node = branches.get(branchIdx);
            if (node == null || node.minInput <= rest) {
                break;
            }
        }

        if (branchIdx >= branches.size()) {
            return FAIL;
        }

        if (branchIdx < branches.size() - 1) {
            cxt.addBackPoint(this, offset, branchIdx + 1);
        }

        cxt.setActivedNode(node == null ? next : node);

        return CONTINE;
    }

    @Override
    public boolean onBack(MatchContext cxt, long data) {
        cxt.setTempVar((int) data);
        return true;
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof BranchNode) {
            if (branches.size() != ((BranchNode) node).branches.size()) {
                return false;
            }
            for (int i = 0; i < branches.size(); i++) {
                if (!branches.get(i).alike(((BranchNode) node).branches.get(i))) {
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
