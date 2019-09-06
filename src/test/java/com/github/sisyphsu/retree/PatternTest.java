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
        assert match("|\\d|b|c|\\w", "");
    }

    @Test
    public void testChar() {
        // assert match("\\ca", String.valueOf((char) 1));
        assert match("\\c0", String.valueOf((char) 112));

        try {
            assert match("\\c", "J");
            assert false;
        } catch (Exception e) {
            assert e instanceof PatternSyntaxException;
        }

        assert match("\\07", String.valueOf((char) 7));
        assert match("\\077", "?");
        assert match("\\0112", "J");
        assert match("\\x30", "0");
        assert match("[\\u4e00-\\u9fa5]", "苏");

        try {
            assert match("\\08", "J");
            assert false;
        } catch (Exception e) {
            assert e instanceof PatternSyntaxException;
        }

        try {
            match("\\C", "C");
            assert false;
        } catch (Exception e) {
            assert e instanceof PatternSyntaxException;
        }

        try {
            assert match("[\\u4eU0-\\u9fa5]", "苏");
        } catch (Exception e) {
            assert e instanceof PatternSyntaxException;
        }
    }

    @Test
    public void testSem() {
        assert match("\\d+]", "12]");
        try {
            assert match("\\d+++", "12+");
            assert false;
        } catch (Exception e) {
            assert e instanceof PatternSyntaxException;
        }
    }

    @Test
    public void testBound() {
        // TODO
//        assert find("\\w\\B", "abc");
    }

    public boolean match(String re, String input) {
        return new Matcher(new ReTree(re), input).matches() != null;
    }

    public boolean find(String re, String input) {
        return new Matcher(new ReTree(re), input).find() != null;
    }

}