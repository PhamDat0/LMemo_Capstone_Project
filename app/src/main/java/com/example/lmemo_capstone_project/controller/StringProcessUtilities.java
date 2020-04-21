package com.example.lmemo_capstone_project.controller;

public class StringProcessUtilities {
    private static final char[] TYPE_OF_SPACE = {' ', '　', '\n', '\t'};

    public static boolean isEmpty(String stringToTest) {
        if (stringToTest.trim().length() == 0) {
            return true;
        }
        char[] chars = stringToTest.toCharArray();
        for (char c : chars) {
            if (c != TYPE_OF_SPACE[0] && c != TYPE_OF_SPACE[1]
                    && c != TYPE_OF_SPACE[2] && c != TYPE_OF_SPACE[3]) {
                return false;
            }
        }
        return true;
    }
}
