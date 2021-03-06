package com.github.sisyphsu.retree.node;

import com.github.sisyphsu.retree.AnchorEndNode;
import com.github.sisyphsu.retree.CharSingleNode;
import com.github.sisyphsu.retree.ReMatcher;
import org.junit.jupiter.api.Test;

/**
 * Perfect AnchorEndNode's test coverage
 *
 * @author sulin
 * @since 2019-09-06 20:11:17
 */
public class AnchorEndNodeTest {

    @Test
    public void alike() {
        AnchorEndNode node1 = new AnchorEndNode(true);
        AnchorEndNode node2 = new AnchorEndNode(true);
        assert node1.alike(node2);

        node1 = new AnchorEndNode(false);
        assert !node1.alike(node2);

        assert !node1.alike(new CharSingleNode(0));
    }

    @Test
    public void testEnd() {
        ReMatcher matcher = new ReMatcher("^\\d+\\r$");
        assert matcher.reset("12345\r\n").matches();

        assert !matcher.reset("12345\r\t").matches();
    }

}