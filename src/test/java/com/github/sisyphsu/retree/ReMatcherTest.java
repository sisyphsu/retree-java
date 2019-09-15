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
        assert Objects.equals(matcher.re(), res[0]);

        matcher.reset("2019-09-03");
        assert !matcher.matches();

        assert matcher.matches("sisyphsu@gmail.com");
        assert Objects.equals(matcher.re(), res[1]);
    }

    @Test
    public void testFind() {
        String[] res = {"(\\d{4}-\\d{1,2}-\\d{1,2})", "<b>(?<name>.*)</b>", "(\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?)"};
        String input = "Today is 2019-09-05, from <b>sulin</b> (sisyphsu@gmail.com).";

        ReMatcher matcher = new ReMatcher(new ReTree(res), input);

        assert matcher.find();
        assert "2019-09-05".contentEquals(matcher.group());

        assert matcher.find();
        assert "<b>sulin</b>".contentEquals(matcher.group());
        assert "sulin".contentEquals(matcher.group(1));
        assert "sulin".contentEquals(matcher.group("name"));
        assert "name".equals(matcher.groupName(1));
        assert null == matcher.groupName(0);

        assert matcher.find();
        assert "sisyphsu@gmail.com".contentEquals(matcher.group());

        assert matcher.groupName(0) == null;

        assert !matcher.reset("Today is 2019-09-05").find(0);
        assert matcher.reset("Today is 2019-09-05").find(9);
    }

    @Test
    public void testGroupName() {
        ReMatcher matcher = new ReMatcher("(?<id>\\d+)(?<name>[a-z]+)");
        matcher.reset("123abc");
        assert matcher.matches();
        assert Objects.equals(matcher.groupName(1), "id");
        assert Objects.equals(matcher.groupName(2), "name");
    }

    @Test
    public void testMore() {
        assert !new ReMatcher(new ReTree("^\\d+$"), "s119").find();

        assert !new ReMatcher(new ReTree("^\\d{4}"), "119").find();

        assert new ReMatcher(" ?(?<zoneOffset>[-+]\\d{2}:?(?>\\d{2})?)").matches("-00:00");
    }

}