package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.CharSingleNode;
import com.github.sisyphsu.retree.CharSliceNode;
import com.github.sisyphsu.retree.ReMatcher;
import org.junit.jupiter.api.Test;

/**
 * @author sulin
 * @since 2019-09-12 10:25:12
 */
public class CharSliceNodeTest {

    @Test
    public void testStudy() {
        CharSliceNode node = new CharSliceNode(new int[]{1, 23});
        node.study();
        assert node.getMinInput() == 2;

        node.study();
        assert node.getMinInput() == 2;
    }

    @Test
    public void alike() {
        CharSliceNode node1 = new CharSliceNode(new int[]{1, 23});
        CharSliceNode node2 = new CharSliceNode(new int[]{1, 23});
        assert node1.alike(node2);

        node1 = new CharSliceNode(new int[]{2, 23});
        assert !node1.alike(node2);

        assert !node1.alike(new CharSingleNode(0));
    }

    @Test
    public void testMatch() {
        ReMatcher matcher = new ReMatcher("(?:abc)+");
        assert matcher.reset("abcabc").matches();
    }

}