package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.BoundNode;
import com.github.sisyphsu.retree.CharSingleNode;
import org.junit.jupiter.api.Test;

/**
 * Perfect AnchorBoundNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:07:23
 */
public class BoundNodeTest {

    @Test
    public void alike() {
        BoundNode node1 = new BoundNode(BoundNode.WORD);
        BoundNode node2 = new BoundNode(BoundNode.WORD);
        assert node1.alike(node2);

        node1 = new BoundNode(BoundNode.WORD);
        node2 = new BoundNode(BoundNode.NON_WORD);
        assert !node1.alike(node2);

        assert !node1.alike(new CharSingleNode(1));
    }

}