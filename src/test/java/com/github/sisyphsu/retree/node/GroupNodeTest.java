package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.CharSingleNode;
import com.github.sisyphsu.retree.GroupNode;
import com.github.sisyphsu.retree.ReMatcher;
import org.junit.jupiter.api.Test;

/**
 * Perfect GroupNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:23:07
 */
public class GroupNodeTest {

    @Test
    public void alike() {
        GroupNode node1 = new GroupNode(1);
        GroupNode node2 = new GroupNode(1);
        assert node1.alike(node2);

        assert !node1.alike(new CharSingleNode(1));

        node1 = new GroupNode(2);
        assert !node1.alike(node2);
    }

    @Test
    public void test() {
        ReMatcher matcher = new ReMatcher("(\\d+)x+", "(\\d+)z+");
        assert matcher.reset("123xxx").matches();
        assert matcher.reset("123zzz").matches();
    }

}