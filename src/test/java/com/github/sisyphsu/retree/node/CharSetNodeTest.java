package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.CharSetNode;
import org.junit.jupiter.api.Test;

/**
 * Perfect CharSetNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 19:59:15
 */
public class CharSetNodeTest {

    @Test
    public void alike() {
        CharSetNode node1 = new CharSetNode();
        CharSetNode node2 = new CharSetNode();

        node1.add(12);
        assert !node1.alike(node2);

        node2.add(12);
        assert node1.alike(node2);

        node2.complement();
        assert !node1.alike(node2);
    }

}