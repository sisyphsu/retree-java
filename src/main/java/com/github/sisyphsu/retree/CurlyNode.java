package com.github.sisyphsu.retree;

import java.util.Arrays;

import static com.github.sisyphsu.retree.Util.*;

/**
 * This node support repeat rules that isn't complex, like \d+.
 *
 * @author sulin
 * @since 2019-09-11 15:39:19
 */
public class CurlyNode extends Node {

    private final int type;
    private final int minTimes;
    private final int maxTimes;
    Node body;

    public CurlyNode(int type, int minTimes, int maxTimes, Node body, Node next) {
        this.type = type;
        this.minTimes = minTimes;
        this.maxTimes = maxTimes;
        this.body = body;
        this.next = next;
    }

    @Override
    public void study() {
        if (minInput < 0) {
            this.minInput = 0;
            body.study();
            next.study();
            this.minInput = next.minInput + body.minInput * minTimes;
        }
    }

    @Override
    public boolean alike(Node node) {
        if (node instanceof CurlyNode) {
            if (type != ((CurlyNode) node).type)
                return false;
            if (minTimes != ((CurlyNode) node).minTimes)
                return false;
            if (maxTimes != ((CurlyNode) node).maxTimes)
                return false;
            return body.alike(((CurlyNode) node).body);
        }
        return false;
    }

    @Override
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        int times = 0;

        for (; times < minTimes; times++) {
            if (matcher.to - cursor < body.minInput || !body.match(matcher, input, cursor)) {
                return false;
            }
            cursor = matcher.last; // body should update last.
        }

        int backCount = 0;
        switch (type) {
            case LAZY:
                for (; ; times++) {
                    if (matcher.to - cursor >= next.minInput && next.match(matcher, input, cursor)) {
                        return true;
                    }
                    if (times >= maxTimes) {
                        return false;
                    }
                    if (matcher.to - cursor < body.minInput || !body.match(matcher, input, cursor)) {
                        return false;
                    }
                    cursor = matcher.last; // body should update last.
                }

            case POSSESSIVE:
                for (; times < maxTimes; times++) {
                    if (matcher.to - cursor < body.minInput || !body.match(matcher, input, cursor)) {
                        break;
                    }
                    cursor = matcher.last; // body should update last.
                }
                break;

            default:
                for (; times < maxTimes; times++) {
                    if (matcher.to - cursor < body.minInput || !body.match(matcher, input, cursor)) {
                        break;
                    }
                    if (matcher.backs.length <= backCount) {
                        matcher.backs = Arrays.copyOf(matcher.backs, matcher.backs.length * 2);
                    }
                    matcher.backs[backCount++] = cursor;

                    cursor = matcher.last; // body should update last.
                }
                break;
        }

        boolean result;
        for (; ; ) {
            result = (matcher.to - cursor >= next.minInput) && next.match(matcher, input, cursor);
            if (result || backCount == 0) {
                break;
            }
            cursor = matcher.backs[--backCount]; // backtracking
        }
        return result;
    }

}
