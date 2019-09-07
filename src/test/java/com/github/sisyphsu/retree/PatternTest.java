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

        assertSyntaxError("(abc");
        assertSyntaxError("(abc))");
        assertSyntaxError("(abc)???");

        assertSyntaxError("[a-[a-z]");
        assertSyntaxError("[z-a");
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

        assert match("\\07", String.valueOf((char) 7));
        assert match("\\077", "?");
        assert match("\\0112", "J");
        assert match("\\x30", "0");
        assert match("[\\u4e00-\\u9fa5]", "苏");

        assertSyntaxError("\\08");
        assertSyntaxError("\\c");
        assertSyntaxError("\\C");
        assertSyntaxError("[\\u4eU0-\\u9fa5]");

        assert match("\\x{100}", "Ā");
        assertSyntaxError("\\xx");
        assertSyntaxError("\\xax");
        assertSyntaxError("\\x{x");
        assertSyntaxError("\\x{110");
        assertSyntaxError("\\x{FFFFFF}");
    }

    @Test
    public void testSem() {
        assert match("\\d+]", "12]");
        assertSyntaxError("\\d+++");

        assertSyntaxError("\\d{a");
        assertSyntaxError("\\d{1");
        assertSyntaxError("\\d{1,0}");
        assertSyntaxError("(?<name\\w+)");
        assertSyntaxError("(?<>\\w+)");
        assertSyntaxError("(?<name>\\w+)(?<name>\\w+)");
        assertSyntaxError("(?\\w+)");

        assert match("(1)(2)(3)(4)(5)(6)(7)(8)(9)(0)(a)(b)\\1\\10\\12\\19", "1234567890ab10b19");

        assertSyntaxError("[abc");

        assert match("[中国]", "国");
    }

    @Test
    public void testEscape() {
        assertSyntaxError("\\C");
        assertSyntaxError("\\l");
        assertSyntaxError("\\m");
        assertSyntaxError("\\o");
        assertSyntaxError("\\p");
        assertSyntaxError("\\q");
        assertSyntaxError("\\i");
        assertSyntaxError("\\j");
        assertSyntaxError("\\g");
        assertSyntaxError("\\y");
        assertSyntaxError("\\E");
        assertSyntaxError("\\F");
        assertSyntaxError("\\I");
        assertSyntaxError("\\J");
        assertSyntaxError("\\K");
        assertSyntaxError("\\L");
        assertSyntaxError("\\M");
        assertSyntaxError("\\N");
        assertSyntaxError("\\O");
        assertSyntaxError("\\P");
        assertSyntaxError("\\T");
        assertSyntaxError("\\X");
        assertSyntaxError("\\Y");

        assert match("\\1", String.valueOf((char) 1));
        assert match("\\9", String.valueOf((char) 9));

        assert match("\\h", " ");
        assert match("\\H", "\n");
        assert match("\\s", "\r");
        assert match("\\S", "a");
        assert match("\\v", "\n");
        assert match("\\V", "b");
        assert match("\\A1\\d\\z", "12");
        assert match("\\A1\\d\\Z", "12");
        assert match("\\u4e00", "一");

        assertSyntaxError("\\k?");
        assertSyntaxError("\\k<name>");

        assert match("\\a", String.valueOf((char) 7));
        assert match("\\e", String.valueOf((char) 27));
        assert match("\\f\\n\\r\\t", "\f\n\r\t");

        assert find("\\w\\B", "ab");
    }

    @Test
    public void testClazzEscape() {
        assert match("[\\1-\\9]", String.valueOf((char) 1));
        assert match("[\\1-\\9]", String.valueOf((char) 9));
        assert match("[\\077]", "?");
        assert match("[\\d]", "1");
        assert match("[\\D]", "a");
        assert match("[\\w]", "a");
        assert match("[\\W]", "?");

        assert match("[\\h]", " ");
        assert match("[\\H]", "\n");
        assert match("[\\s]", "\r");
        assert match("[\\S]", "a");
        assert match("[\\v]", "\n");
        assert match("[\\V]", "b");
        assert match("[\\y]", "y");
        assert match("[\\x{100}]", "Ā");
        assert match("[\\c0]", String.valueOf((char) 112));

        assert match("[\\a]", String.valueOf((char) 7));
        assert match("[\\e]", String.valueOf((char) 27));
        assert match("[\\f]", "\f");
        assert match("[\\n]", "\n");
        assert match("[\\r]", "\r");
        assert match("[\\t]", "\t");

        assert find("[\\b-z]", "a");
        assert find("[\\B-C]", "B");

        assertSyntaxError("[\\l]");
        assertSyntaxError("[\\o]");
        assertSyntaxError("[\\p]");
        assertSyntaxError("[\\E]");
        assertSyntaxError("[\\L]");
        assertSyntaxError("[\\N]");
        assertSyntaxError("[\\P]");
        assertSyntaxError("[\\U]");
    }

    @Test
    public void testOther() {
        assert !match("(\\d*){2,10}\\w", "eeeeee");
        assert !match("(\\d*)*\\w", "ee");
        assert !match("\\d*?[abc]{2}", "1c");
    }

    public boolean match(String re, String input) {
        return new ReMatcher(new ReTree(re), input).matches() != null;
    }

    public boolean find(String re, String input) {
        return new ReMatcher(new ReTree(re), input).find() != null;
    }

    public void assertSyntaxError(String re) {
        try {
            new ReTree(re);
            assert false;
        } catch (Exception e) {
            assert e instanceof PatternSyntaxException;
        }
    }

}