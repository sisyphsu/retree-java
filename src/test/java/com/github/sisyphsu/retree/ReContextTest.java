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
            matcher.groupName(0);
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

        matcher.matches();
        assert "abc".contentEquals(matcher.group(1));
        assert matcher.start() == 0;
        assert matcher.end() == 3;

        try {
            matcher.group(2);
            assert false;
        } catch (Exception e) {
            assert e instanceof IndexOutOfBoundsException;
        }

        try {
            matcher.group("ss");
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalArgumentException;
        }
    }

}