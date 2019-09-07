package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.CharRangeNode;
import org.junit.jupiter.api.Test;

/**
 * Perfect CharRangeNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:02:43
 */
public class CharRangeNodeTest {

    @Test
    public void alike() {
        CharRangeNode node1 = new CharRangeNode(1, 100);
        CharRangeNode node2 = new CharRangeNode(1, 100);

        assert node1.alike(node2);

        node1.complement();
        assert !node1.alike(node2);

        node1 = new CharRangeNode(1, 10);
        node2 = new CharRangeNode(1, 11);
        assert !node1.alike(node2);
    }

}