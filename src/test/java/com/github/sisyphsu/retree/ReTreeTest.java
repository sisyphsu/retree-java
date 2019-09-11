package com.github.sisyphsu.retree;

import org.junit.jupiter.api.Test;

/**
 * Functional test of ReTree
 *
 * @author sulin
 * @since 2019-09-03 20:32:03
 */
public class ReTreeTest {

    @Test
    public void testStudy() {
        String[] res = {"\\w{5,7}", "\\d{4,6}", "123\\w+"};
        ReTree tree = new ReTree(res);
        assert tree.root.minInput == 4;
    }

    @Test
    public void testEmpty() {
        try {
            new ReTree();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalArgumentException;
        }
    }

    @Test
    public void testLoopOptimized() {
        ReTree tree = new ReTree("\\d+");
        assert tree.root.next instanceof CurlyNode;

        tree = new ReTree("\\d++");
        assert tree.root.next instanceof CurlyNode;

        tree = new ReTree("(\\d{2}|\\d){3,}\\d");
        assert tree.root.next instanceof LoopNode;
        LoopNode loopNode = (LoopNode) tree.root.next;
        assert loopNode.body instanceof GroupNode;
        BranchNode branchNode = (BranchNode) loopNode.body.next;
        assert branchNode.branches.get(0) instanceof CurlyNode;
    }

    @Test
    public void testCharSliceOptimized() {
        ReTree tree = new ReTree("abcde\\d");
        assert tree.root.next instanceof CharSliceNode;
    }

    @Test
    public void testUnion() {
        String[] res = {"abc\\d{5,}", "abc\\w{10,}"};
        ReTree tree = new ReTree(res);

        ReMatcher matcher = new ReMatcher(tree, "abc123");

        assert !matcher.matches();

        matcher.reset("abc123456");
        assert matcher.matches();

        assert res[0].contentEquals(matcher.re());
    }

}