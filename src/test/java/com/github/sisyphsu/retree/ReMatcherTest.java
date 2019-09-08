package com.github.sisyphsu.retree;

import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * Functional test of multiple regular expression.
 *
 * @author sulin
 * @since 2019-09-03 20:24:39
 */
public class ReMatcherTest {

    @Test
    public void simple1() {
        ReTree tree = new ReTree("hello", "world");
        assert new ReMatcher(tree, "world").matches();
        assert new ReMatcher(tree, "hello").matches();
        assert !new ReMatcher(tree, "hell").matches();
        assert !new ReMatcher(tree, "worldd").matches();
    }

    @Test
    public void simple2() {
        ReTree tree = new ReTree("^\\d{4}年\\d{1,2}月\\d{1,2}日$", "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?");

        assert new ReMatcher(tree, "2019年09月3日").matches();
        assert new ReMatcher(tree, "sisyphsu@gmail.com").matches();
        assert !new ReMatcher(tree, "2019-09-03").matches();
    }

    @Test
    public void testReset() {
        String[] res = {"^\\d{4}年\\d{1,2}月\\d{1,2}日$", "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?"};

        ReMatcher matcher = new ReMatcher(new ReTree(res), "");
        assert !matcher.matches();

        assert matcher.reset("2019年09月3日").matches();
        Result result = matcher.getResult();
        assert result != null;
        assert Objects.equals(result.re(), res[0]);

        matcher.reset("2019-09-03");
        assert !matcher.matches();

        assert matcher.reset("sisyphsu@gmail.com").matches();
        assert Objects.equals(matcher.getResult().re(), res[1]);
    }

    @Test
    public void testFind() {
        String[] res = {"(\\d{4}-\\d{1,2}-\\d{1,2})", "<b>(?<name>.*)</b>", "(\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?)"};
        String input = "Today is 2019-09-05, from <b>sulin</b> (sisyphsu@gmail.com).";

        ReMatcher matcher = new ReMatcher(new ReTree(res), input);

        assert matcher.find();
        assert "2019-09-05".contentEquals(matcher.getResult().group());

        assert matcher.find();
        assert "<b>sulin</b>".contentEquals(matcher.getResult().group());
        assert "sulin".contentEquals(matcher.getResult().group(1));
        assert "sulin".contentEquals(matcher.getResult().group("name"));

        assert matcher.find();
        assert "sisyphsu@gmail.com".contentEquals(matcher.getResult().group());
    }

    @Test
    public void testMultiFind() {
        String[] res = {"^(\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?)", "^(\\w+)@"};
        String input = "sisyphsu@gmail.com";

        ReMatcher matcher = new ReMatcher(new ReTree(res), input);
        matcher.find();

        assert matcher.getAllResult().size() == 2;

        for (Result result : matcher.getAllResult()) {
            switch (result.re()) {
                case "(\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?)":
                    assert input.contentEquals(result.group());
                    break;
                case "(\\w+)@":
                    assert "sisyphsu@".contentEquals(result.group());
                    assert "sisyphsu".contentEquals(result.group(1));
                    break;
            }
        }
    }

    @Test
    public void testMore() {
        assert !new ReMatcher(new ReTree("^\\d+$"), "s119").find();

        assert !new ReMatcher(new ReTree("^\\d{4}"), "119").find();
    }

}