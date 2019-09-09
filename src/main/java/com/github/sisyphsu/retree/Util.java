package com.github.sisyphsu.retree;

/**
 * This class is mainly copied from java.util.regex.ASCII, and other place.
 *
 * @author sulin
 * @since 2019-08-11 12:34:31
 */
final class Util {

    public static final int GREEDY = 0;
    public static final int LAZY = 1;
    public static final int POSSESSIVE = 2;

    public static final int UPPER = 0x00000100;
    public static final int LOWER = 0x00000200;
    public static final int DIGIT = 0x00000400;
    public static final int SPACE = 0x00000800;
    public static final int UNDER = 0x00010000;
    public static final int WORD = (UPPER | LOWER | UNDER | DIGIT);

    public static boolean isAscii(int ch) {
        return (ch & 0xFFFFFF80) == 0;
    }

    public static boolean isAlpha(int ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    public static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isHexDigit(int ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }

    public static boolean isLower(int ch) {
        return ch >= 'a' && ch <= 'z';
    }

    public static boolean isUpper(int ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    public static int toDigit(int ch) {
        if (isLower(ch)) {
            return ch - 'a' + 10;
        }
        if (isUpper(ch)) {
            return ch - 'A' + 10;
        }
        return ch - '0';
    }

    private Util() {
    }
}
