package com.github.sisyphsu.retree;

import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * Functional test of multiple regular expression.
 *
 * @author sulin
 * @since 2019-09-03 20:24:39
 */
public class MatcherTest {

    @Test
    public void simple1() {
        ReTree tree = new ReTree("hello", "world");
        assert new Matcher(tree, "world").matches() != null;
        assert new Matcher(tree, "hello").matches() != null;
        assert new Matcher(tree, "hell").matches() == null;
        assert new Matcher(tree, "worldd").matches() == null;
    }

    @Test
    public void simple2() {
        ReTree tree = new ReTree("^\\d{4}年\\d{1,2}月\\d{1,2}日$", "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?");

        assert new Matcher(tree, "2019年09月3日").matches() != null;
        assert new Matcher(tree, "sisyphsu@gmail.com").matches() != null;
        assert new Matcher(tree, "2019-09-03").matches() == null;
    }

    @Test
    public void testReset() {
        String[] res = {"^\\d{4}年\\d{1,2}月\\d{1,2}日$", "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?"};

        Matcher matcher = new Matcher(new ReTree(res), "");
        assert matcher.matches() == null;

        MatchResult result = matcher.reset("2019年09月3日").matches();
        assert result != null;
        assert Objects.equals(result.re(), res[0]);

        matcher.reset("2019-09-03");
        assert matcher.matches() == null;

        result = matcher.reset("sisyphsu@gmail.com").matches();
        assert result != null;
        assert Objects.equals(result.re(), res[1]);
    }

    @Test
    public void testFind() {
        String[] res = {"(\\d{4}-\\d{1,2}-\\d{1,2})", "<b>(?<name>.*)</b>", "(\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?)"};
        String input = "Today is 2019-09-05, from <b>sulin</b> (sisyphsu@gmail.com).";

        Matcher matcher = new Matcher(new ReTree(res), input);

        MatchResult result = matcher.find();
        assert result != null;
        assert "2019-09-05".contentEquals(result.group());

        result = matcher.find();
        assert result != null;
        assert "<b>sulin</b>".contentEquals(result.group());
        assert "sulin".contentEquals(result.group(1));
        assert "sulin".contentEquals(result.group("name"));

        result = matcher.find();
        assert result != null;
        assert "sisyphsu@gmail.com".contentEquals(result.group());
    }

    @Test
    public void testMultiFind() {
        String[] res = {"(\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?)", "(\\w+)@"};
        String input = "sisyphsu@gmail.com";

        Matcher matcher = new Matcher(new ReTree(res), input);
        matcher.find();

        assert matcher.getResults().size() == 2;

        for (MatchResult result : matcher.getResults()) {
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

}