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
        ReTree tree = new ReTree(ReTree.SHORTEST_SELECTOR, res);
        ReMatcher matcher = new ReMatcher(tree, "abc");

        Result result = new ReContext(matcher, tree);

        try {
            result.re();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        try {
            result.start();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        try {
            result.end();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        try {
            result.group();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        try {
            result.groupCount();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        try {
            result.group("name");
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalStateException;
        }

        result = matcher.matches();
        assert result != null;
        assert "abc".contentEquals(result.group(1));
        assert result.start() == 0;
        assert result.end() == 3;

        try {
            result.group(2);
            assert false;
        } catch (Exception e) {
            assert e instanceof IndexOutOfBoundsException;
        }

        try {
            result.group("ss");
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalArgumentException;
        }
    }

}