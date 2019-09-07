package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.MixNode;
import org.junit.jupiter.api.Test;

/**
 * Perfect MixNode's test coverage.
 *
 * @author sulin
 * @since 2019-09-06 20:16:00
 */
public class MixNodeTest {

    @Test
    public void alike() {
        MixNode node1 = new MixNode(0, null);
        MixNode node2 = new MixNode(0, null);
        assert !node1.alike(node2);
    }
}