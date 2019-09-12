package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.EndNode;
import org.junit.jupiter.api.Test;

/**
 * @author sulin
 * @since 2019-09-12 11:47:31
 */
public class EndNodeTest {

    @Test
    public void match() {
    }

    @Test
    public void alike() {
        EndNode node1 = new EndNode("", null);
        EndNode node2 = new EndNode("", null);
        assert !node1.alike(node2);
    }

}