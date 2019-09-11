package com.github.sisyphsu.retree.demo.basic;

import com.github.sisyphsu.retree.ReMatcher;
import com.github.sisyphsu.retree.ReTree;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Basic demo for Regex vs ReTree's usage and performace.
 * <p>
 * Single RE
 * Benchmark         Mode  Cnt    Score    Error  Units
 * BasicTest.regex   avgt    6  257.040 ± 12.877  ns/op
 * BasicTest.retree  avgt    6  500.925 ±  8.273  ns/op
 * <p>
 * Multiple RE
 * Benchmark         Mode  Cnt     Score    Error  Units
 * BasicTest.regex   avgt    6  1987.309 ± 19.346  ns/op
 * BasicTest.retree  avgt    6  1847.143 ± 44.174  ns/op
 *
 * @author sulin
 * @since 2019-09-07 10:29:28
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class BasicTest {

    public static final String TEXT = "You have new emails from @Sisyphsu <sisyphsu@gmail.com> & @Sulin <sulin@xxx.com> at 2019-09-07.";
    public static final String[] RES = {"\\W@(\\w+)", "<(\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?)>", "(\\d{4}-\\d{2}-\\d{2})"};

    public static final ReMatcher MATCHER = new ReMatcher(new ReTree(RES), TEXT);
    public static final java.util.regex.Matcher[] MATCHERS = new java.util.regex.Matcher[RES.length];

    static {
        for (int i = 0; i < RES.length; i++) {
            MATCHERS[i] = Pattern.compile(RES[i]).matcher(TEXT);
        }
    }

    final List<String> parseByRegex() {
        List<String> results = new ArrayList<>();

        for (java.util.regex.Matcher matcher : MATCHERS) {
            matcher.reset(TEXT);
//            matcher.matches();
            while (matcher.find()) {
                results.add(matcher.group(1));
            }
        }

        return results;
    }

    final List<String> parseByReTree() {
        List<String> results = new ArrayList<>();

        MATCHER.reset(TEXT);
//        MATCHER.matches();
        while (MATCHER.find()) {
            results.add(MATCHER.group(1));
        }

        return results;
    }

    @Test
    public void test() {
        parseByRegex();
        List<String> regResult = parseByRegex();
        assert regResult.size() == 5;
        assert regResult.contains("Sisyphsu");
        assert regResult.contains("sisyphsu@gmail.com");
        assert regResult.contains("Sulin");
        assert regResult.contains("sulin@xxx.com");
        assert regResult.contains("2019-09-07");

        parseByReTree();
        List<String> retResult = parseByReTree();
        assert retResult.size() == 5;
        assert retResult.contains("Sisyphsu");
        assert retResult.contains("sisyphsu@gmail.com");
        assert retResult.contains("Sulin");
        assert retResult.contains("sulin@xxx.com");
        assert retResult.contains("2019-09-07");
    }

    @Benchmark
    public void regex() {
        this.parseByRegex();
    }

    @Benchmark
    public void retree() {
        this.parseByReTree();
    }

}
