package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.BranchNode;
import com.github.sisyphsu.retree.CharSingleNode;
import com.github.sisyphsu.retree.EndNode;
import org.junit.jupiter.api.Test;

/**
 * Perfect BranchNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 19:36:22
 */
public class BranchNodeTest {

    private static final EndNode endNode = new EndNode("", null);

    @Test
    public void alike() {
        BranchNode node1, node2;

        node1 = new BranchNode(endNode, null, new CharSingleNode(1));
        node2 = new BranchNode(endNode, null, new CharSingleNode(2));
        assert !node1.alike(node2);

        node1 = new BranchNode(endNode, new CharSingleNode(2), new CharSingleNode(1));
        node2 = new BranchNode(endNode, null, new CharSingleNode(1));
        assert !node1.alike(node2);

        node1 = new BranchNode(endNode, null, new CharSingleNode(1));
        node2 = new BranchNode(endNode, new CharSingleNode(2), new CharSingleNode(1));
        assert !node1.alike(node2);

        node1 = new BranchNode(endNode, null, new CharSingleNode(1));
        node2 = new BranchNode(endNode, null, new CharSingleNode(1));
        assert node1.alike(node2);

        node2.add(new CharSingleNode(1));
        assert !node1.alike(node2);

        assert !node1.alike(endNode);
    }

}