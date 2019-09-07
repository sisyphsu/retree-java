package com.github.sisyphsu.retree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import static com.github.sisyphsu.retree.Util.*;

/**
 * Copied from java.util.regex.Pattern, and changes a lot.
 * <p>
 * 1. didn't support (?d), (?i), (?x), (?m), (?s), (?U)
 * 2. didn't support \p{ASCII}, \P{Uppercase} and others like this.
 * 3. didn't support [[abc]&&[def]] or [abc&&def].
 * 4. didn't support lookaround, lookahead, lookbehead.
 *
 * @author sulin
 * @since 2019-08-26 13:53:50
 */
final class Pattern {

    private String pattern;
    private int patternLength;

    Node ret;
    Node matchRoot;
    EndNode endNode;

    private int[] ptnChars;
    private Map<String, Integer> namedGroups;

    private int groupCount;
    private int localCount;

    private int cursor;

    public static Pattern compile(String regex) {
        return new Pattern(regex);
    }

    private Pattern(String re) {
        pattern = re;
        groupCount = 1;
        localCount = 1;
        namedGroups = new HashMap<>(2);

        endNode = new EndNode(re, namedGroups);
        if (pattern.length() > 0) {
            compile();
        } else {
            matchRoot = endNode;
        }
        endNode.setLocalCount(localCount);
        endNode.setGroupCount(groupCount);
        matchRoot = new BeginNode(matchRoot);
    }

    private void compile() {
        patternLength = pattern.length();

        ptnChars = new int[patternLength + 2];
        int chCount = 0;
        for (int off = 0; off < patternLength; off++) {
            ptnChars[chCount++] = pattern.charAt(off);
        }
        patternLength = chCount;

        removeQEQuoting();

        matchRoot = parseExpress(endNode);

        // if didn't hit end, throw error
        if (cursor != patternLength) {
            throw error(peek() == ')' ? "Unmatched closing ')'" : "Unexpected internal error");
        }

        // release memory
        ptnChars = null;
        patternLength = 0;
    }

    private void removeQEQuoting() {
        int off = 0;
        for (; off < patternLength - 1; off++) {
            if (ptnChars[off] == '\\' && ptnChars[off + 1] == 'Q') {
                break; // hit '\Q'
            }
        }
        if (off >= patternLength - 1) {
            return; // didn't hit '\Q'
        }
        boolean inQuote = true;
        int[] newTemp = new int[patternLength * 2];
        System.arraycopy(ptnChars, 0, newTemp, 0, off);
        int newOff = off;

        off += 2;
        for (; off < patternLength; ) {
            int ch = ptnChars[off++];
            if (!Util.isAscii(ch) || Util.isAlpha(ch) || Util.isDigit(ch)) {
                newTemp[newOff++] = ch;
                continue;
            }
            if (ch != '\\') {
                if (inQuote) {
                    newTemp[newOff++] = '\\';
                }
                newTemp[newOff++] = ch;
                continue;
            }
            if (inQuote) {
                if (ptnChars[off] == 'E') {
                    off++;
                    inQuote = false;
                } else {
                    newTemp[newOff++] = '\\';
                    newTemp[newOff++] = ch;
                }
                continue;
            }
            if (ptnChars[off] == 'Q') {
                off++;
                inQuote = true;
                continue;
            }
            newTemp[newOff++] = ch;
            if (off < patternLength) {
                newTemp[newOff++] = ptnChars[off++];
            }
        }
        patternLength = newOff;
        ptnChars = Arrays.copyOf(newTemp, newOff + 2);
    }

    /**
     * Peek the next character, and do not advance the cursor.
     */
    private int peek() {
        return ptnChars[cursor];
    }

    /**
     * Read the next character, and advance the cursor by one.
     */
    private int read() {
        return ptnChars[cursor++];
    }

    /**
     * Advance the cursor by one, and peek the next character.
     */
    private int next() {
        return ptnChars[++cursor];
    }

    /**
     * Skip the current one, and do read().
     */
    private int skipAndRead() {
        ++cursor;
        return read();
    }

    /**
     * Unread one next character, and retreat cursor by one.
     */
    private void unread() {
        cursor--;
    }

    private PatternSyntaxException error(String s) {
        return new PatternSyntaxException(s, pattern, cursor - 1);
    }

    /**
     * Parse expression, like '\w+|\d+|(?\d+|\w+)'
     */
    private Node parseExpress(final Node end) {
        Node result = sequence(end);
        Node resultTail = ret;

        BranchNode branch = null;
        for (; peek() == '|'; ) {
            next();
            Node node = sequence(end);
            Node nodeTail = ret;
            if (node == end) {
                node = null;
            } else {
                nodeTail.setNext(end);
            }

            if (branch != null) {
                branch.add(node);
                continue;
            }

            // initialize BranchNode
            if (result == end) {
                result = null;
            } else {
                resultTail.setNext(end);
            }
            branch = new BranchNode(end, result, node);
        }

        return branch == null ? result : branch;
    }

    /**
     * Parse sub expression between '|'
     */
    private Node sequence(Node end) {
        Node head = null;
        Node tail = null;
        LOOP:
        for (; ; ) {
            int ch = peek();
            if (ch == '(') {
                Node groupHead = this.parseGroup();
                if (groupHead != null) {
                    if (head == null) {
                        head = groupHead;
                    } else {
                        tail.setNext(groupHead);
                    }
                    tail = ret;
                }
                continue;
            }
            Node node;
            switch (ch) {
                case '|':
                case ')':
                    break LOOP;
                case '[':
                    node = this.parseClass();
                    break;
                case '^':
                    next();
                    node = new AnchorStartNode();
                    break;
                case '$':
                    next();
                    node = new AnchorEndNode(false);
                    break;
                case '.':
                    next();
                    node = new CharAnyNode();
                    break;
                case '?':
                case '*':
                case '+':
                case '{':
                    next();
                    throw error("Dangling meta character '" + ((char) ch) + "'");
                case '\\':
                    ch = this.parseEscape();
                    if (ch < 0) {
                        node = ret; // hit excape, return it directly
                    } else {
                        node = new CharSingleNode(ch);
                    }
                    break;
                case 0:
                    if (cursor >= patternLength) {
                        break LOOP;
                    }
                default:
                    next();
                    node = new CharSingleNode(ch);
                    break;
            }

            node = parseRepetition(node, node);

            if (head == null) {
                head = node;
            } else {
                tail.setNext(node);
            }
            tail = node;
        }
        if (head == null) {
            return end;
        }
        tail.setNext(end);
        ret = tail;
        return head;
    }

    /**
     * Parse excape expression, like \d.
     */
    private int parseEscape() {
        int ch = skipAndRead();
        switch (ch) {
            case 'C':
            case 'l':
            case 'm':
            case 'o':
            case 'p':
            case 'q':
            case 'i':
            case 'j':
            case 'g':
            case 'y':
            case 'E':
            case 'F':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'T':
            case 'U':
            case 'X':
            case 'Y':
                break;
            case '0':
                return parseOctalEscape();
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                ret = backReferenceEscape(ch - '0');
                return -1;
            case 'b':
                ret = new AnchorBoundNode(AnchorBoundNode.WORD);
                return -1;
            case 'B':
                ret = new AnchorBoundNode(AnchorBoundNode.NON_WORD);
                return -1;
            case 'd':
                ret = new CharTypeNode(Util.DIGIT, true);
                return -1;
            case 'D':
                ret = new CharTypeNode(Util.DIGIT, false);
                return -1;
            case 'h':
                ret = new CharWhitespaceNode(true, true);
                return -1;
            case 'H':
                ret = new CharWhitespaceNode(true, false);
                return -1;
            case 's':
                ret = new CharTypeNode(Util.SPACE, true);
                return -1;
            case 'S':
                ret = new CharTypeNode(Util.SPACE, false);
                return -1;
            case 'v':
                ret = new CharWhitespaceNode(false, true);
                return -1;
            case 'V':
                ret = new CharWhitespaceNode(false, false);
                return -1;
            case 'w':
                ret = new CharTypeNode(Util.WORD, true);
                return -1;
            case 'W':
                ret = new CharTypeNode(Util.WORD, false);
                return -1;
            case 'A':
                ret = new AnchorStartNode();
                return -1;
            case 'z':
                ret = new AnchorEndNode(true);
                return -1;
            case 'Z':
                ret = new AnchorEndNode(false);
                return -1;
            case 'c':
                return parseControlEscape();
            case 'u':
                return parseUxxxx();
            case 'x':
                return parseHexadecimalEscape();
            case 'k':
                if (read() != '<') {
                    throw error("\\k is not followed by '<' for named capturing group");
                }
                String name = groupname();
                if (!namedGroups.containsKey(name)) {
                    throw error("(named capturing group <" + name + "> does not exit");
                }
                ret = new CharRefNode(namedGroups.get(name));
                return -1;
            case 'a':
                return '\007';
            case 'e':
                return '\033';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            default:
                return ch;
        }
        throw error("Illegal/unsupported escape sequence");
    }

    /**
     * Parse bake reference escape, like \1
     */
    private Node backReferenceEscape(int refNum) {
        while (true) {
            int ch = peek();
            if (ch < '0' || ch > '9') {
                break;
            }
            int newRefNum = (refNum * 10) + (ch - '0');
            if (groupCount - 1 < newRefNum) {
                break;
            }
            refNum = newRefNum;
            read();
        }
        if (groupCount - 1 < refNum) {
            return new CharSingleNode(refNum);
        }
        return new CharRefNode(refNum);
    }

    /**
     * Parse char class, like [abcdef]
     */
    private CharNode parseClass() {
        boolean include = true;
        int ch = next();
        if (ch == '^') {
            ch = next();
            include = false;
        }
        CharNode prev = null;
        CharSetNode bits = new CharSetNode();
        for (; ; ) {
            if (ch == 0 && cursor >= patternLength) {
                throw error("Unclosed character class");
            }
            if (ch == ']' && prev != null) {
                next();
                return include ? prev : prev.complement();
            }
            final CharNode node = clazzRange(bits);
            if (prev == null) {
                prev = node;
            } else if (prev != node) {
                prev = new CharUnionNode(prev, node);
            }
            ch = peek();
        }
    }

    /**
     * Parse char range, like [a-z]
     */
    private CharNode clazzRange(CharSetNode bits) {
        int ch = peek();
        if (ch == '\\') {
            ch = clazzEscape(true, ptnChars[cursor + 2] == '-');
            if (ch == -1) {
                return (CharNode) ret;
            }
        } else {
            next();
        }
        if (peek() == '-') {
            int endRange = ptnChars[cursor + 1];
            if (endRange == '[') {
                throw error("Character range is out of order");
            }
            if (endRange != ']') {
                next();
                int m = peek();
                if (m == '\\') {
                    m = clazzEscape(false, true);
                } else {
                    next();
                }
                if (m < ch) {
                    throw error("Illegal character range");
                }
                return new CharRangeNode(ch, m);
            }
        }
        return ch < 256 ? bits.add(ch) : new CharSingleNode(ch);
    }

    /**
     * Parse excape in char class, like [\1]
     */
    private int clazzEscape(boolean create, boolean isRange) {
        int ch = skipAndRead();
        if (ch >= '1' && ch <= '9') {
            return ch - '0';
        }
        switch (ch) {
            case 'l':
            case 'o':
            case 'p':
            case 'E':
            case 'L':
            case 'N':
            case 'P':
            case 'U':
                break;
            case '0':
                return parseOctalEscape();
            case 'b':
                return 8;
            case 'd':
                if (create) ret = new CharTypeNode(Util.DIGIT, true);
                return -1;
            case 'D':
                if (create) ret = new CharTypeNode(Util.DIGIT, false);
                return -1;
            case 'h':
                if (create) ret = new CharWhitespaceNode(true, true);
                return -1;
            case 'H':
                if (create) ret = new CharWhitespaceNode(true, false);
                return -1;
            case 's':
                if (create) ret = new CharTypeNode(Util.SPACE, true);
                return -1;
            case 'S':
                if (create) ret = new CharTypeNode(Util.SPACE, false);
                return -1;
            case 'v':
                if (isRange) return '\013';
                if (create) ret = new CharWhitespaceNode(false, true);
                return -1;
            case 'V':
                if (create) ret = new CharWhitespaceNode(false, false);
                return -1;
            case 'w':
                if (create) ret = new CharTypeNode(Util.WORD, true);
                return -1;
            case 'W':
                if (create) ret = new CharTypeNode(Util.WORD, false);
                return -1;
            case 'a':
                return '\007';
            case 'e':
                return '\033';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'c':
                return parseControlEscape();
            case 'u':
                return parseUxxxx();
            case 'x':
                return parseHexadecimalEscape();
            default:
                return ch;
        }
        throw error("Illegal/unsupported escape sequence");
    }

    /**
     * Parse the following group, and return GroupNode, if it's repeated then return LoopNode.
     */
    private Node parseGroup() {
        int ch = next();
        Node head;
        Node tail;
        ret = null;
        if (ch == '?') {
            ch = skipAndRead();
            switch (ch) {
                case ':':
                    head = this.createGroup(true, null);
                    tail = ret;
                    head.setNext(parseExpress(tail));
                    break;
                case '>':
                    head = this.createGroup(true, null);
                    tail = ret;
                    head.setNext(parseExpress(tail));
                    head = tail = new LoopNode(head, tail, 1, 1, POSSESSIVE, localCount += 3);
                    break;
                case '<':
                    String name = this.groupname();
                    if (namedGroups.containsKey(name)) {
                        throw error("Named capturing group <" + name + "> is already defined");
                    }
                    head = this.createGroup(false, name);
                    tail = ret;
                    namedGroups.put(name, groupCount - 1);
                    head.setNext(parseExpress(tail));
                    break;
                default:
                    throw error("Unknown group type");
            }
        } else {
            head = this.createGroup(false, null);
            tail = ret;
            head.setNext(parseExpress(tail));
        }

        if (')' != read()) {
            throw error("Unclosed group");
        }

        Node node = parseRepetition(head, tail);
        if (node == head) {
            ret = tail;
        } else {
            ret = node;
        }
        return node;
    }

    /**
     * parse the following group name, like the 'name' in '(?<name>\w+)'
     */
    private String groupname() {
        int ch;
        StringBuilder sb = new StringBuilder();
        while (Util.isLower(ch = read()) || Util.isUpper(ch) || Util.isDigit(ch)) {
            sb.append(Character.toChars(ch));
        }
        if (sb.length() == 0)
            throw error("named capturing group has 0 length name");
        if (ch != '>')
            throw error("named capturing group is missing trailing '>'");
        return sb.toString();
    }

    /**
     * create group
     */
    private GroupNode createGroup(boolean anonymous, String groupName) {
        int groupIndex = anonymous ? 0 : groupCount++;
        GroupNode head = new GroupNode(groupIndex, groupName);
        ret = head.getTailNode();
        return head;
    }

    /**
     * Parse repetition to be LoopNode, like '\d+', '[a-z]*+' etc
     */
    private Node parseRepetition(Node head, Node tail) {
        int ch = peek();
        switch (ch) {
            case '?':
                ch = next();
                if (ch == '?') {
                    next();
                    return new LoopNode(head, tail, 0, 1, LAZY, localCount += 3);
                } else if (ch == '+') {
                    next();
                    return new LoopNode(head, tail, 0, 1, POSSESSIVE, localCount += 3);
                }
                // '?*' is invalid expression
                return new LoopNode(head, tail, 0, 1, GREEDY, localCount += 3);
            case '*':
                ch = next();
                if (ch == '?') {
                    next();
                    return new LoopNode(head, tail, 0, Integer.MAX_VALUE, LAZY, localCount += 3);
                } else if (ch == '+') {
                    next();
                    return new LoopNode(head, tail, 0, Integer.MAX_VALUE, POSSESSIVE, localCount += 3);
                }
                // '**' is invalid expression
                return new LoopNode(head, tail, 0, Integer.MAX_VALUE, GREEDY, localCount += 3);
            case '+':
                ch = next();
                if (ch == '?') {
                    next();
                    return new LoopNode(head, tail, 1, Integer.MAX_VALUE, LAZY, localCount += 3);
                } else if (ch == '+') {
                    next();
                    return new LoopNode(head, tail, 1, Integer.MAX_VALUE, POSSESSIVE, localCount += 3);
                }
                // '+*' is invalid expression
                return new LoopNode(head, tail, 1, Integer.MAX_VALUE, GREEDY, localCount += 3);
            case '{':
                ch = ptnChars[cursor + 1];
                if (Util.isDigit(ch)) {
                    skipAndRead();
                    int cmin = 0;
                    do {
                        cmin = cmin * 10 + (ch - '0');
                    } while (Util.isDigit(ch = read()));
                    int cmax = cmin;
                    if (ch == ',') {
                        ch = read();
                        cmax = Integer.MAX_VALUE;
                        if (ch != '}') {
                            cmax = 0;
                            while (Util.isDigit(ch)) {
                                cmax = cmax * 10 + (ch - '0');
                                ch = read();
                            }
                        }
                    }
                    if (ch != '}')
                        throw error("Unclosed counted closureRepetition");
                    if (((cmin) | (cmax) | (cmax - cmin)) < 0)
                        throw error("Illegal repetition range");
                    LoopNode curly;
                    ch = peek();
                    if (ch == '?') {
                        next();
                        curly = new LoopNode(head, tail, cmin, cmax, LAZY, localCount += 3);
                    } else if (ch == '+') {
                        next();
                        curly = new LoopNode(head, tail, cmin, cmax, POSSESSIVE, localCount += 3);
                    } else {
                        curly = new LoopNode(head, tail, cmin, cmax, GREEDY, localCount += 3);
                    }
                    return curly;
                } else {
                    throw error("Illegal repetition");
                }
            default:
                return head;
        }
    }

    /**
     * Parse some char like '\ca' as 'CTRL+a'
     */
    private int parseControlEscape() {
        if (cursor < patternLength) {
            return read() ^ 64;
        }
        throw error("Illegal control escape sequence");
    }

    /**
     * Parse char in octal excape, like \077 could be parsed as '?'
     */
    private int parseOctalEscape() {
        int n = read();
        if (((n - '0') | ('7' - n)) >= 0) {
            int m = read();
            if (((m - '0') | ('7' - m)) >= 0) {
                int o = read();
                if ((((o - '0') | ('7' - o)) >= 0) && (((n - '0') | ('3' - n)) >= 0)) {
                    return (n - '0') * 64 + (m - '0') * 8 + (o - '0');
                }
                unread();
                return (n - '0') * 8 + (m - '0');
            }
            unread();
            return (n - '0');
        }
        throw error("Illegal octal escape sequence");
    }

    /**
     * Parse char in hexadecimal, like \x30
     */
    private int parseHexadecimalEscape() {
        int n = read();
        if (Util.isHexDigit(n)) {
            int m = read();
            if (Util.isHexDigit(m)) {
                return Util.toDigit(n) * 16 + Util.toDigit(m);
            }
        } else if (n == '{' && Util.isHexDigit(peek())) {
            int ch = 0;
            while (Util.isHexDigit(n = read())) {
                ch = (ch << 4) + Util.toDigit(n);
                if (ch > Character.MAX_CODE_POINT) {
                    throw error("Hexadecimal codepoint is too big");
                }
            }
            if (n != '}')
                throw error("Unclosed hexadecimal escape sequence");
            return ch;
        }
        throw error("Illegal hexadecimal escape sequence");
    }

    /**
     * Parse unicode char, like \uFFFF
     */
    private int parseUxxxx() {
        int n = 0;
        for (int i = 0; i < 4; i++) {
            int ch = read();
            if (!Util.isHexDigit(ch)) {
                throw error("Illegal Unicode escape sequence");
            }
            n = n * 16 + Util.toDigit(ch);
        }
        return n;
    }

}