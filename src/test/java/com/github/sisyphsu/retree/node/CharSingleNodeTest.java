package com.github.sisyphsu.retree.node;

import org.junit.jupiter.api.Test;

/**
 * Perfect CharSingleNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:26:51
 */
public class CharSingleNodeTest {

    @Test
    public void alike() {
        CharSingleNode node1 = new CharSingleNode(1);
        CharSingleNode node2 = new CharSingleNode(1);
        assert node1.alike(node2);

        node1 = new CharSingleNode(10);
        assert !node1.alike(node2);

        node1 = new CharSingleNode('a');
        assert node1.toString().equals("a");
    }

}