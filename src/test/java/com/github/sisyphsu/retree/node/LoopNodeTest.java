package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.CharSingleNode;
import com.github.sisyphsu.retree.LoopNode;
import org.junit.jupiter.api.Test;

/**
 * Perfect LoopNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:17:59
 */
public class LoopNodeTest {

    @Test
    public void alike() {
        LoopNode node1 = new LoopNode(new CharSingleNode(1), new CharSingleNode(10), 1, 10, 1, 3);
        LoopNode node2 = new LoopNode(new CharSingleNode(1), new CharSingleNode(10), 1, 10, 1, 3);
        assert node1.alike(node2);

        node1 = new LoopNode(new CharSingleNode(1), new CharSingleNode(10), 1, 10, 1, 2);
        assert !node1.alike(node2);

        node1 = new LoopNode(new CharSingleNode(1), new CharSingleNode(10), 1, 10, 2, 3);
        assert !node1.alike(node2);

        node1 = new LoopNode(new CharSingleNode(1), new CharSingleNode(10), 2, 10, 1, 3);
        assert !node1.alike(node2);

        node1 = new LoopNode(new CharSingleNode(1), new CharSingleNode(10), 1, 100, 1, 3);
        assert !node1.alike(node2);

        assert !node1.alike(new CharSingleNode(1));
    }

}