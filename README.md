# retree-java

[![Travis CI](https://travis-ci.org/sisyphsu/retree-java.svg?branch=master)](https://travis-ci.org/sisyphsu/retree-java)
[![codecov](https://codecov.io/gh/sisyphsu/retree-java/branch/master/graph/badge.svg)](https://codecov.io/gh/sisyphsu/retree-java)

# Introduce

`retree` could parse and combine lots of regular expressions as an `regular-expression-tree`, 
which is very similar to `trie`.

First, `retree` would parse `regex` as a **node-chain**, for example:
+ `\d+a\W` will be parsed as `CurlyNode(\d+) -> CharNode(a) -> CharNode(\W)`,
+ `\d+\s\W` will be parsed as `CurlyNode(\d+) -> CharNode(\s) -> CharNode(\W)`.
 
After that, `retree` will merge those two **node-chain** as one **node-tree** 
(there should have an image to explain how it looks)

When performing multiple regex matching, we could use `retree` to reduce useless scan and loop, and avoid lots of backtracking.

# Maven Dependency

Add maven dependency:

```xml
<dependency>
    <groupId>com.github.sisyphsu</groupId>
    <artifactId>retree</artifactId>
    <version>1.0.4</version>
</dependency>
```

# Usage

This example shows how `retree` works, it's very similar to `java.util.regex`:

```java
String[] res = {"(\\d{4}-\\d{1,2}-\\d{1,2})", "<b>(?<name>.*)</b>", "(\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?)"};
String input = "Today is 2019-09-05, from <b>sulin</b> (sisyphsu@gmail.com).";
ReMatcher matcher = new ReMatcher(new ReTree(res), input);

assert matcher.find();
assert "2019-09-05".contentEquals(matcher.group());

assert matcher.find();
assert "<b>sulin</b>".contentEquals(matcher.group());
assert "sulin".contentEquals(matcher.group("name"));

assert matcher.find();
assert "sisyphsu@gmail.com".contentEquals(matcher.group());
```

In this example, we only need to scan `input` once to complete three different regular expressions' matching: 
+ `(\d{4}-\d{1,2}-\d{1,2})`
+ `<b>(?<name>.*)</b>`
+ `(\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?)` 

# Showcase

[**dateparser**](https://github.com/sisyphsu/dateparser)

`dateparser` is a smart and high-performance date parser library, 
it supports hundreds of different format, nearly all format that we used.

`dateparser` use `retree` to perform the matching operation of lots of different date patterns. 

Even if `dateparser` have thundreds of predefined regular expressions, 
it still can parse date very fast(1000~1500ns).

# Performance & Benchmark

TODO

# Multi-Language Support

I will transplant this library to `golang` and `javascript` in nearly future.

# License

Apache-2.0