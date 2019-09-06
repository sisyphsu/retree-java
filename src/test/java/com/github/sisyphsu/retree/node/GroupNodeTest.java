package com.github.sisyphsu.retree.node;

import org.junit.jupiter.api.Test;

/**
 * Perfect GroupNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:23:07
 */
public class GroupNodeTest {

    @Test
    public void alike() {
        GroupNode node1 = new GroupNode(1, null);
        GroupNode node2 = new GroupNode(1, null);
        assert node1.alike(node2);

        assert !node1.alike(new CharSingleNode(1));
    }

}