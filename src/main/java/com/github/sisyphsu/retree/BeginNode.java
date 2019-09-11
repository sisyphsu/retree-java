package com.github.sisyphsu.retree;

/**
 * Normal beginning node, should be the first node of all node-chain.
 *
 * @author sulin
 * @since 2019-09-02 19:51:09
 */
public final class BeginNode extends Node {

    public BeginNode(Node next) {
        this.next = next;
    }

    @Override
    public boolean match(ReMatcher matcher, CharSequence input, int cursor) {
        for (int i = cursor, stop = matcher.stop; i <= stop; i++) {
            matcher.groupVars[0] = i;
            if (next.match(matcher, input, i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean alike(Node node) {
        return node instanceof BeginNode;
    }

}
