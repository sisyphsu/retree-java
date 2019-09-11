package com.github.sisyphsu.retree;

import org.junit.jupiter.api.Test;

/**
 * MatchContext's test, basically for 100% test coverage.
 *
 * @author sulin
 * @since 2019-09-06 14:21:36
 */
public class ReContextTest {

    @Test
    public void testError() {
        String[] res = {"(?<key>.*)"};
        ReTree tree = new ReTree(res);
        ReMatcher matcher = new ReMatcher(tree, "abc");

        try {
            matcher.re();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        try {
            matcher.start();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        try {
            matcher.end();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        try {
            matcher.group();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        try {
            matcher.groupCount();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        try {
            matcher.group("name");
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

//        matcher.matches();
//        result = matcher.getResult();
//        assert result != null;
//        assert "abc".contentEquals(result.group(1));
//        assert result.start() == 0;
//        assert result.end() == 3;
//
//        try {
//            result.group(2);
//            assert false;
//        } catch (Exception e) {
//            assert e instanceof IndexOutOfBoundsException;
//        }
//
//        try {
//            result.group("ss");
//            assert false;
//        } catch (Exception e) {
//            assert e instanceof IllegalArgumentException;
//        }
    }

}