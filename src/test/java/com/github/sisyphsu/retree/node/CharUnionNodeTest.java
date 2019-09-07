package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.CharSingleNode;
import com.github.sisyphsu.retree.CharUnionNode;
import org.junit.jupiter.api.Test;

/**
 * Perfect CharUnionNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 19:43:26
 */
public class CharUnionNodeTest {

    @Test
    public void alike() {
        CharUnionNode node1, node2;

        node1 = new CharUnionNode(new CharSingleNode(1), new CharSingleNode(2));
        node2 = new CharUnionNode(new CharSingleNode(1), new CharSingleNode(2));
        assert node1.alike(node2);

        assert !node1.alike(new CharSingleNode(1));

        node1 = new CharUnionNode(new CharSingleNode(1), new CharSingleNode(2));
        node2 = new CharUnionNode(new CharSingleNode(1), new CharSingleNode(3));
        assert !node1.alike(node2);

        node1 = new CharUnionNode(new CharSingleNode(1), new CharSingleNode(2));
        node2 = new CharUnionNode(new CharSingleNode(1), new CharSingleNode(2).complement());
        assert !node1.alike(node2);
    }

}