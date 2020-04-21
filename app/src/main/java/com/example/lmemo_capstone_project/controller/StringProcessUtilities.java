package com.example.lmemo_capstone_project.controller;

public class StringProcessUtilities {
    private static final char[] TYPE_OF_SPACE = {' ', '　'};

    public static boolean isEmpty(String stringToTest) {
        if (stringToTest.length() == 0) {
            return true;
        }
        char[] chars = stringToTest.toCharArray();
        for (char c : chars) {
            if (c != TYPE_OF_SPACE[0] && c != TYPE_OF_SPACE[1]) {
                return false;
            }
        }
        return true;
    }
}
