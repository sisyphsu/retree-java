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

    public static final String TEXT = "You have an new email from @Sisyphsu <sisyphsu@gmail.com> and @Sulin <sulin@xxx.com> at 2019-09-07.";
    public static final String[] RES = {"(\\d{4}-\\d{2}-\\d{2})"};

    public static final ReMatcher MATCHER = new ReMatcher(new ReTree(RES), TEXT);
    public static final Matcher[] MATCHERS = new Matcher[RES.length];

    static {
        for (int i = 0; i < RES.length; i++) {
            MATCHERS[i] = Pattern.compile(RES[i]).matcher(TEXT);
        }
    }

    final int parseByRegex() {
        int count = 0;
        for (Matcher matcher : MATCHERS) {
//            matcher.reset(TEXT);
//            matcher.matches();
            while (matcher.find()) {
                count++;
            }
//            for (int i = 0; i < TEXT.length(); i++) {
//                if (matcher.find(i)) {
//                    count++;
//                }
//            }
        }
        return count;
    }

    final int parseByReTree() {
        int count = 0;
//        MATCHER.reset(TEXT);
//        MATCHER.matches();
        while (MATCHER.find()) {
            count++;
        }
//        for (int i = 0; i < TEXT.length(); i++) {
//            if (MATCHER.find(i)) {
//                count++;
//            }
//        }
        return count;
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
        System.out.println(this.parseByRegex());
        System.out.println(this.parseByReTree());
        assert this.parseByRegex() == this.parseByReTree();
    }
}
