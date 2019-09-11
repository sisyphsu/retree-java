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
    public void test() {
//        ReTree tree1 = new ReTree("hello");
//        assert tree1.crossVarCount == 0;
//
//        ReTree tree2 = new ReTree("hello", "world");
//        assert tree2.crossVarCount == 1;
//
//        ReTree tree3 = new ReTree("hello", "world", "worxd");
//        assert tree3.crossVarCount == 2;
    }

    @Test
    public void testShortest() {
//        String[] res = {"\\w{5,7}", "\\d{4,6}", "123\\w+"};
//        ReTree tree = new ReTree(ReTree.SHORTEST_SELECTOR, res);
//
//        ReMatcher matcher = new ReMatcher(tree, "123456789");
//        assert matcher.find();
//        assert Objects.equals(matcher.getResult().re(), res[1]);
//
//        matcher = new ReMatcher(tree, "1234a");
//        assert matcher.find();
//        assert Objects.equals(matcher.getResult().re(), res[1]);
//
//        matcher = new ReMatcher(tree, "123abcdef");
//        assert matcher.find();
//        assert Objects.equals(matcher.getResult().re(), res[0]);
    }

    @Test
    public void testLongest() {
//        String[] res = {"\\w{5,7}", "\\d{4,6}", "123\\w+"};
//        ReTree tree = new ReTree(ReTree.LONGEST_SELECTOR, res);
//
//        ReMatcher matcher = new ReMatcher(tree, "12345678");
//        assert matcher.find();
//        assert Objects.equals(matcher.getResult().re(), res[2]);
//
//        matcher = new ReMatcher(tree, "23456789");
//        assert matcher.find();
//        assert Objects.equals(matcher.getResult().re(), res[0]);
//
//        matcher = new ReMatcher(tree, "123abcdef");
//        assert matcher.find();
//        assert Objects.equals(matcher.getResult().re(), res[2]);
    }

    @Test
    public void testStudy() {
//        String[] res = {"\\w{5,7}", "\\d{4,6}", "123\\w+"};
//        ReTree tree = new ReTree(ReTree.LONGEST_SELECTOR, res);
//        assert tree.root.minInput == 4;
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
    public void testUnion() {
//        String[] res = {"abc\\d{5,}", "abc\\w{10,}"};
//        ReTree tree = new ReTree(ReTree.LONGEST_SELECTOR, res);
//
//        ReMatcher matcher = new ReMatcher(tree, "abc123");
//
//        assert !matcher.matches();
//
//        matcher.reset("abc123456");
//        assert matcher.matches();
//
//        System.out.println(matcher.getResult());
//        assert res[0].contentEquals(matcher.getResult().re());
    }

}