package com.github.sisyphsu.retree;

import org.junit.jupiter.api.Test;

import java.util.regex.PatternSyntaxException;

/**
 * @author sulin
 * @since 2019-09-06 14:31:02
 */
public class PatternTest {

    @Test
    public void testCompile() {
        Pattern.compile("");

        try {
            Pattern.compile("(abc");
            assert false;
        } catch (Exception e) {
            assert e instanceof PatternSyntaxException;
        }

        try {
            Pattern.compile("(abc))");
            assert false;
        } catch (Exception e) {
            assert e instanceof PatternSyntaxException;
        }

        try {
            Pattern.compile("(abc)???");
            assert false;
        } catch (Exception e) {
            assert e instanceof PatternSyntaxException;
        }
    }

    @Test
    public void testQE() {
        assert match("\\Q\\w\\E\\d+\\Q[1-9]\\E", "\\w123[1-9]");
    }

    @Test
    public void testEmptyBranch() {
        assert match("\\d|", "");
        assert match("\\d|", "1");
        assert match("\\d|a|b|c|\\w", "a");
        assert match("\\d||b|c|\\w", "");
    }

    @Test
    public void testOther() {
        assert match("\\077", "?");
        try {
            match("\\C", "C");
            assert false;
        } catch (Exception e) {
            assert e instanceof PatternSyntaxException;
        }
    }

    @Test
    public void testBound() {
        assert find("\\w\\B", "abc");
    }

    public boolean match(String re, String input) {
        return new Matcher(new ReTree(re), input).matches() != null;
    }

    public boolean find(String re, String input) {
        return new Matcher(new ReTree(re), input).find() != null;
    }

}