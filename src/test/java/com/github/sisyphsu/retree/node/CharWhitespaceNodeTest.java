package com.github.sisyphsu.retree.node;

import org.junit.jupiter.api.Test;

/**
 * Perfect CharWhitespaceNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:04:48
 */
public class CharWhitespaceNodeTest {

    @Test
    public void alike() {
        CharWhitespaceNode node1 = new CharWhitespaceNode(true, false);
        CharWhitespaceNode node2 = new CharWhitespaceNode(true, false);
        assert node1.alike(node2);

        node1 = new CharWhitespaceNode(false, false);
        node2 = new CharWhitespaceNode(true, false);
        assert !node1.alike(node2);

        node1 = new CharWhitespaceNode(true, true);
        node2 = new CharWhitespaceNode(true, false);
        assert !node1.alike(node2);

        assert !node1.alike(new CharSingleNode(0));
    }

}