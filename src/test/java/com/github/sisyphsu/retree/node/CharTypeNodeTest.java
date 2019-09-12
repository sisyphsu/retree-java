package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.CharSingleNode;
import com.github.sisyphsu.retree.CharTypeNode;
import org.junit.jupiter.api.Test;

/**
 * @author sulin
 * @since 2019-09-12 11:33:01
 */
public class CharTypeNodeTest {

    @Test
    public void alike() {
        CharTypeNode node1 = new CharTypeNode(CharTypeNode.DIGIT, true);
        CharTypeNode node2 = new CharTypeNode(CharTypeNode.DIGIT, true);
        assert node1.alike(node2);

        assert !node1.alike(new CharSingleNode(0));

        node1 = new CharTypeNode(CharTypeNode.DIGIT, false);
        assert !node1.alike(node2);

        node1 = new CharTypeNode(CharTypeNode.SPACE, true);
        assert !node1.alike(node2);
    }

}