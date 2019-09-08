package com.github.sisyphsu.retree;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark                      Mode  Cnt     Score     Error  Units
 * BaseBenchmark.cxtReset         avgt    3    38.837 ±   3.052  ns/op
 * BaseBenchmark.newMatchContext  avgt    3    31.429 ±   3.820  ns/op
 * BaseBenchmark.newMatcher       avgt    3    44.503 ±   1.168  ns/op
 * BaseBenchmark.newTree          avgt    3  2349.035 ± 253.398  ns/op
 *
 * @author sulin
 * @since 2019-09-07 11:32:24
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Measurement(iterations = 3, time = 3)
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

    @Benchmark
    public void cxtReset() {
        ReContext cxt = new ReContext(MATCHER, TREE);
        cxt.reset(TREE.root, 1);
    }

}
