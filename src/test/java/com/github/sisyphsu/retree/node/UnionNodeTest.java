package com.github.sisyphsu.retree.node;

import org.junit.jupiter.api.Test;

/**
 * Perfect UnionNode's test coverage.
 *
 * @author sulin
 * @since 2019-09-06 20:16:00
 */
public class UnionNodeTest {

    @Test
    public void alike() {
        UnionNode node1 = new UnionNode(0, null);
        UnionNode node2 = new UnionNode(0, null);
        assert !node1.alike(node2);
    }
}