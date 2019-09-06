package com.github.sisyphsu.retree.node;

import org.junit.jupiter.api.Test;

/**
 * Perfect AnchorEndNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:11:17
 */
public class AnchorEndNodeTest {

    @Test
    public void alike() {
        AnchorEndNode node1 = new AnchorEndNode(true);
        AnchorEndNode node2 = new AnchorEndNode(true);
        assert node1.alike(node2);

        node1 = new AnchorEndNode(false);
        assert !node1.alike(node2);

        assert !node1.alike(new CharSingleNode(0));
    }

}