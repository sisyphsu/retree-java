package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.AnchorStartNode;
import com.github.sisyphsu.retree.CharSingleNode;
import org.junit.jupiter.api.Test;

/**
 * @author sulin
 * @since 2019-09-12 11:40:31
 */
public class AnchorStartNodeTest {

    @Test
    public void match() {
    }

    @Test
    public void alike() {
        AnchorStartNode node1 = new AnchorStartNode();
        assert !node1.alike(new CharSingleNode(1));
    }

}