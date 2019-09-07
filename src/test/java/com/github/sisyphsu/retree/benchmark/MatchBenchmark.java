package com.github.sisyphsu.retree.benchmark;

import com.github.sisyphsu.retree.ReMatcher;
import com.github.sisyphsu.retree.ReTree;
import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Benchmark for single regular expression's matching operation.
 * <p>
 * # Round 1.
 * # Compare to the java.util.regex, retree needs more optimization.
 * SingleMatchBenchmark.regex   avgt    9  12181.759 ±  96.131  ns/op
 * SingleMatchBenchmark.retree  avgt    9  43354.836 ± 553.402  ns/op
 * SingleMatchBenchmark.sregex  avgt    9  35580.709 ± 663.439  ns/op
 * <p>
 * # Round 2.
 * # Compare to normal node-chain, retree has about 15% performance loss.
 * Benchmark                    Mode  Cnt      Score     Error  Units
 * SingleMatchBenchmark.retree  avgt    9  41086.601 ± 420.338  ns/op
 * SingleMatchBenchmark.sregex  avgt    9  35445.534 ± 386.090  ns/op
 * <p>
 * # Round 3.
 * # Add fast-fail feature by study, retree has a huge performance improvement.
 * # Compare to regex, it has only 20%~30% performance loss.
 * Benchmark                    Mode  Cnt      Score     Error  Units
 * SingleMatchBenchmark.regex   avgt    9  12395.474 ± 158.027  ns/op
 * SingleMatchBenchmark.retree  avgt    9  15765.203 ± 236.455  ns/op
 * <p>
 * # Round 4.
 * # Complete FF optimization.
 * Benchmark                    Mode  Cnt      Score     Error  Units
 * SingleMatchBenchmark.regex   avgt    9  12610.212 ± 285.918  ns/op
 * SingleMatchBenchmark.retree  avgt    9  15167.808 ± 377.977  ns/op
 * <p>
 * # Round 5.
 * Benchmark                    Mode  Cnt      Score     Error  Units
 * SingleMatchBenchmark.regex   avgt    9  12596.052 ± 241.812  ns/op
 * SingleMatchBenchmark.retree  avgt    9  15346.489 ± 247.418  ns/op
 * <p>
 * # Rount 6.
 * # Cache the compiled regular expression~~~ We Win!!!
 * # ReTree is 40% faster than regex.
 * Benchmark              Mode  Cnt     Score     Error  Units
 * MatchBenchmark.regex   avgt    9  7978.058 ± 253.454  ns/op
 * MatchBenchmark.retree  avgt    9  5658.872 ± 120.930  ns/op
 *
 * @author sulin
 * @since 2019-09-04 10:11:05
 */
@SuppressWarnings("ALL")
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(3)
@Measurement(iterations = 3, time = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MatchBenchmark {

    private static final Map<String, Pattern> ptnMap = new HashMap<>();
    private static final Map<String, ReTree> treeMap = new HashMap<>();

    @Benchmark
    public void retree() {
        retreeMatch("(\\d{2}|\\d){10,}\\d", "012345678912");
        retreeMatch("[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))", "192.168.1.1");
        retreeMatch("(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?", "http://www.csdn.net:80");
        retreeMatch("[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}", "1992-09-03");
        retreeMatch("\\-?[1-9]\\d+(\\.\\d+)?", "123.54324");
        retreeMatch("\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?", "zhangsan@xxx.com.cn");
        retreeMatch("([1-9]|1[012]):[0-5][0-9]", "3:59");
        retreeMatch("[1-9][1-9]-[1-9][1-9]", "38-99");
    }

    @Benchmark
    public void regex() {
        regexMatch("(\\d{2}|\\d){10,}\\d", "012345678912");
        regexMatch("[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))", "192.168.1.1");
        regexMatch("(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?", "http://www.csdn.net:80");
        regexMatch("[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}", "1992-09-03");
        regexMatch("\\-?[1-9]\\d+(\\.\\d+)?", "123.54324");
        regexMatch("\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?", "zhangsan@xxx.com.cn");
        regexMatch("([1-9]|1[012]):[0-5][0-9]", "3:59");
        regexMatch("[1-9][1-9]-[1-9][1-9]", "38-99");
    }


    private boolean retreeMatch(String ptn, String input) {
        ReTree tree = treeMap.computeIfAbsent(ptn, ReTree::new);
        return new ReMatcher(tree, input).matches() != null;
    }

    private boolean regexMatch(String ptn, String input) {
        Pattern pattern = ptnMap.computeIfAbsent(ptn, Pattern::compile);
        return pattern.matcher(input).matches();
    }

//    private boolean retreeMatch(String ptn, String input) {
//        return new Matcher(new ReTree(ptn), input).matches() != null;
//    }
//
//    private boolean regexMatch(String ptn, String input) {
//        return Pattern.matches(ptn, input);
//    }

}
