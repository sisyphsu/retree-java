package com.github.sisyphsu.retree.node;

import org.junit.jupiter.api.Test;

/**
 * Perfect AnchorBoundNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:07:23
 */
public class AnchorBoundNodeTest {

    @Test
    public void alike() {
        AnchorBoundNode node1 = new AnchorBoundNode(AnchorBoundNode.WORD);
        AnchorBoundNode node2 = new AnchorBoundNode(AnchorBoundNode.WORD);
        assert node1.alike(node2);

        node1 = new AnchorBoundNode(AnchorBoundNode.WORD);
        node2 = new AnchorBoundNode(AnchorBoundNode.NON_WORD);
        assert !node1.alike(node2);

        assert !node1.alike(new CharSingleNode(1));
    }

}