package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.CharSingleNode;
import com.github.sisyphsu.retree.CurlyNode;
import com.github.sisyphsu.retree.EndNode;
import com.github.sisyphsu.retree.ReMatcher;
import org.junit.jupiter.api.Test;

import static com.github.sisyphsu.retree.Util.GREEDY;
import static com.github.sisyphsu.retree.Util.LAZY;

/**
 * Perfect test coverage for CurlyNode
 *
 * @author sulin
 * @since 2019-09-12 11:11:33
 */
public class CurlyNodeTest {

    private static final EndNode endNode = new EndNode("");

    @Test
    public void study() {
        CurlyNode node = new CurlyNode(LAZY, 1, 10, new CharSingleNode(0), endNode);

        node.study();
        assert node.getMinInput() == 1;

        node.study();
        assert node.getMinInput() == 1;
    }

    @Test
    public void alike() {
        CurlyNode node1 = new CurlyNode(LAZY, 1, 10, new CharSingleNode(0), endNode);
        CurlyNode node2 = new CurlyNode(LAZY, 1, 10, new CharSingleNode(0), endNode);
        assert node1.alike(node2);

        assert !node1.alike(endNode);

        node1 = new CurlyNode(GREEDY, 1, 10, new CharSingleNode(0), endNode);
        assert !node1.alike(node2);

        node1 = new CurlyNode(LAZY, 2, 10, new CharSingleNode(0), endNode);
        assert !node1.alike(node2);

        node1 = new CurlyNode(LAZY, 1, 100, new CharSingleNode(0), endNode);
        assert !node1.alike(node2);

        node1 = new CurlyNode(LAZY, 1, 10, new CharSingleNode(1), endNode);
        assert !node1.alike(node2);
    }

    @Test
    public void test() {
        ReMatcher matcher = new ReMatcher("\\d+?\\d");
        assert matcher.reset("12").matches();
        assert !matcher.reset("123").matches();

        matcher = new ReMatcher("\\d+\\d");
        assert matcher.reset("123").matches();

        matcher = new ReMatcher("\\d++\\d");
        assert !matcher.reset("123").matches();

        matcher = new ReMatcher("\\d{1,3}?\\s");
        assert !matcher.reset("1234a").matches();
    }

}