package com.github.sisyphsu.retree;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark                      Mode  Cnt     Score    Error  Units
 * BaseBenchmark.newMatchContext  avgt    9    34.537 ±  0.132  ns/op
 * BaseBenchmark.newMatcher       avgt    9    13.996 ±  0.056  ns/op
 * BaseBenchmark.newTree          avgt    9  2333.791 ± 37.309  ns/op
 *
 * @author sulin
 * @since 2019-09-07 11:32:24
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class BaseBenchmark {

    public static final String[] RES = {"\\W@(\\w+)", "<(\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?)>", "(\\d{4}-\\d{2}-\\d{2})"};
    public static final ReTree TREE = new ReTree(RES);
    public static final ReMatcher MATCHER = new ReMatcher(TREE, "");

    @Benchmark
    public void newTree() {
        new ReTree(RES);
    }

    @Benchmark
    public void newMatcher() {
        new ReMatcher(TREE, "");
    }

    @Benchmark
    public void newMatchContext() {
        new ReContext(MATCHER, TREE);
    }

}
