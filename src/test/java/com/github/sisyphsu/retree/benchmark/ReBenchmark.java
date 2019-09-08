package com.github.sisyphsu.retree.benchmark;

import com.github.sisyphsu.retree.ReMatcher;
import com.github.sisyphsu.retree.ReTree;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic demo for Regex vs ReTree's usage and performace.
 * <p>
 * Matches
 * Benchmark           Mode  Cnt   Score   Error  Units
 * ReBenchmark.regex   avgt    6  12.674 ± 2.923  ns/op
 * ReBenchmark.retree  avgt    6  66.145 ± 1.126  ns/op
 * <p>
 * Find:
 * Benchmark           Mode  Cnt     Score     Error  Units
 * ReBenchmark.regex   avgt    6   219.558 ±   9.406  ns/op
 * ReBenchmark.retree  avgt    6  7183.744 ± 283.702  ns/op
 *
 * @author sulin
 * @since 2019-09-07 10:29:28
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ReBenchmark {

    public static final String TEXT = "abcdefghij";
    public static final String[] RES = {"\\w"};

    public static final ReMatcher MATCHER = new ReMatcher(new ReTree(RES), TEXT);
    public static final Matcher[] MATCHERS = new Matcher[RES.length];

    static {
        for (int i = 0; i < RES.length; i++) {
            MATCHERS[i] = Pattern.compile(RES[i]).matcher(TEXT);
        }
    }

    final void parseByRegex() {
        for (Matcher matcher : MATCHERS) {
            matcher.reset(TEXT);
            matcher.matches();
//            matcher.find(0);
//            for (int i = 0; i < 1; i++) {
//                matcher.find(i);
//            }
//            while (matcher.find()) {
//            }
        }
    }

    final void parseByReTree() {
        MATCHER.reset(TEXT);
        MATCHER.matches();
//        MATCHER.find(0);
//        while (MATCHER.find() != null) {
//        }
//        for (int i = 0; i < 1; i++) {
//            MATCHER.find(i);
//        }
    }

    @Benchmark
    public void regex() {
        this.parseByRegex();
    }

    @Benchmark
    public void retree() {
        this.parseByReTree();
    }

    @Test
    public void test() {
        this.parseByRegex();
        this.parseByReTree();
    }
}
