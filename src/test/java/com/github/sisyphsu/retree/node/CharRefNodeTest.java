package com.github.sisyphsu.retree.node;

import org.junit.jupiter.api.Test;

/**
 * Perfect CharRefNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:14:26
 */
public class CharRefNodeTest {

    @Test
    public void alike() {
        CharRefNode node1 = new CharRefNode(1);
        CharRefNode node2 = new CharRefNode(1);
        assert node1.alike(node2);

        node1 = new CharRefNode(2);
        assert !node1.alike(node2);

        assert !node1.alike(new CharSingleNode(0));
    }

}