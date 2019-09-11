package com.github.sisyphsu.retree;

import java.util.ArrayList;
import java.util.List;

/**
 * Regular Expression Tree, which could represents multiple regular expression.
 * <p>
 * Every regular expression will be parsed as Node-Chain, after that merge those Node-Chain into Node-Tree.
 *
 * @author sulin
 * @since 2019-09-03 16:33:35
 */
public final class ReTree {

    final int localVarCount;
    final int groupVarCount;

    final Node root;

    /**
     * Initialize ReTree.
     *
     * @param exps All regular expression
     */
    public ReTree(String... exps) {
        // compile regular expressions
        List<Node> nodes = new ArrayList<>(exps.length);
        for (String exp : exps) {
            Node node = ReCompiler.compile(exp).root;
            node = this.optimizeLoop(node);
            nodes.add(node);
        }
        // calculate the count of localVar, groupVar, crossVar
        int loopVarCount = 0;
        int groupVarCount = 0;
        for (Node node : nodes) {
            EndNode endNode = findEndNode(node);
            loopVarCount = Math.max(loopVarCount, endNode.getLocalCount());
            groupVarCount = Math.max(groupVarCount, endNode.getGroupCount());
        }
        // build tree
        Node treeRoot = this.buildTree(nodes);
        treeRoot = this.optimizeCharSlice(treeRoot);
        if (treeRoot != null) {
            treeRoot.study();
        }
        this.root = treeRoot;
        this.localVarCount = loopVarCount;
        this.groupVarCount = groupVarCount;
    }

    /**
     * Merges all re-node-chain into re-tree.
     *
     * @param nodes All Nodes which represent regular expression.
     * @return The final ReTree
     */
    private Node buildTree(List<Node> nodes) {
        final List<Node> branches = new ArrayList<>();
        final List<Node> nexts = new ArrayList<>();
        while (nodes.size() > 0) {
            final Node curr = nodes.get(0);
            nodes.removeIf(node -> {
                if (node == curr || node.alike(curr)) {
                    if (node.next != null) {
                        nexts.add(node.next);
                    }
                    return true;
                }
                return false;
            });
            if (nexts.size() > 0) {
                curr.next = this.buildTree(nexts);
            }
            branches.add(curr);
            nexts.clear();
        }
        if (branches.size() == 0) {
            throw new IllegalArgumentException("node can't be empty or null");
        }
        if (branches.size() == 1) {
            return branches.get(0);
        }
        return new BranchNode(branches);
    }

    /**
     * Optimize the LoopNode, if it isn't complex, use CurlyNode replace.
     *
     * @param node Node
     */
    private Node optimizeLoop(Node node) {
        if (node == null) {
            return null;
        }
        if ((node.flag & 1) > 0) {
            return node;
        } else {
            node.flag |= 1;
        }
        if (node instanceof BranchNode) {
            BranchNode branchNode = (BranchNode) node;
            for (int i = 0; i < branchNode.branches.size(); i++) {
                branchNode.branches.set(i, optimizeLoop(branchNode.branches.get(i)));
            }
            branchNode.next = this.optimizeLoop(branchNode.next);
        } else if (node instanceof LoopNode) {
            LoopNode loop = (LoopNode) node;
            loop.next = this.optimizeLoop(loop.next);
            loop.body = this.optimizeLoop(loop.body);
            // try to optimize as CurlyNode
            Node body = loop.body;
            while (body != null) {
                if (body instanceof BranchNode || body instanceof LoopNode || body instanceof CurlyNode) {
                    break; // can't optimize complex loop
                }
                if (body.next == loop) {
                    node = new CurlyNode(loop.type, loop.minTimes, loop.maxTimes, loop.body, loop.next);
                    body.next = null;
                    break;
                }
                body = body.next;
            }
        } else {
            node.next = this.optimizeLoop(node.next);
        }

        return node;
    }

    private Node optimizeCharSlice(Node node) {
        if (node == null) {
            return null;
        }
        if ((node.flag & 2) > 0) {
            return node;
        } else {
            node.flag |= 2;
        }
        if (node instanceof BranchNode) {
            BranchNode branchNode = (BranchNode) node;
            for (int i = 0; i < branchNode.branches.size(); i++) {
                branchNode.branches.set(i, optimizeCharSlice(branchNode.branches.get(i)));
            }
        } else if (node instanceof LoopNode) {
            LoopNode loopNode = (LoopNode) node;
            loopNode.body = this.optimizeCharSlice(loopNode.body);
        } else if (node instanceof CurlyNode) {
            CurlyNode curlyNode = (CurlyNode) node;
            curlyNode.body = this.optimizeCharSlice(curlyNode.body);
        } else if (node instanceof CharSingleNode) {
            Node next = node;
            List<Integer> chars = new ArrayList<>();
            while (next instanceof CharSingleNode) {
                chars.add(((CharSingleNode) next).ch);
                next = next.next;
            }
            if (chars.size() > 1) {
                node = new CharSliceNode(chars.stream().mapToInt(i -> i).toArray());
                node.next = next;
            }
        }
        node.next = this.optimizeCharSlice(node.next);

        return node;
    }

    /**
     * Find the EndNode from the specified node in recusion
     *
     * @param node Any node
     * @return The final EndNode's instance
     */
    private EndNode findEndNode(Node node) {
        if (node instanceof EndNode) {
            return (EndNode) node;
        }
        return findEndNode(node.next);
    }

}