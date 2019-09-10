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
 * Keep using stack for 15% improvement of performance.
 * Before：
 * Benchmark           Mode  Cnt     Score    Error  Units
 * ReBenchmark.regex   avgt    3   516.437 ± 18.356  ns/op
 * ReBenchmark.retree  avgt    3  1267.261 ± 27.098  ns/op
 * After：
 * Benchmark           Mode  Cnt     Score    Error  Units
 * ReBenchmark.regex   avgt    3   505.684 ± 35.526  ns/op
 * ReBenchmark.retree  avgt    3  1109.869 ± 47.807  ns/op
 *
 * @author sulin
 * @since 2019-09-07 10:29:28
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ReBenchmark {

    public static final String TEXT = "You have an new email from @Sisyphsu <sisyphsu@gmail.com> and @Sulin <sulin@xxx.com> at 2019-09-07.";
    public static final String RE = "(\\W)";

    public static final Matcher matcher = Pattern.compile(RE).matcher(TEXT);
    public static final ReMatcher reMatcher = new ReMatcher(new ReTree(RE), TEXT);

    final int parseByRegex() {
        int count = 0;
        matcher.reset(TEXT);
//            matcher.matches();
        for (int i = 0; i < TEXT.length(); i++) {
            if (matcher.find(i)) {
                count++;
            }
        }
//        while (matcher.find()) {
//            count++;
//        }
        return count;
    }

    final int parseByReTree() {
        int count = 0;
        reMatcher.reset(TEXT);
//        reMatcher.matches();
        for (int i = 0; i < TEXT.length(); i++) {
            if (reMatcher.find(i)) {
                count++;
            }
        }
//        while (reMatcher.find()) {
//            count++;
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
