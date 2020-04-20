package com.example.lmemo_capstone_project.controller;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StringProcessUtilitiesTest {
    private final String REAL_EMPTY = "";
    private final String TEST_FULL_NORMAL_SPACE1 = "  ";
    private final String TEST_FULL_NORMAL_SPACE2 = "     ";
    private final String TEST_FULL_JP_SPACE1 = "　　";
    private final String TEST_FULL_JP_SPACE2 = "　　　　　";
    //Normal JP
    private final String TEST_JP_SP_AND_NORMAL_SP1 = " 　";
    //JP Normal
    private final String TEST_JP_SP_AND_NORMAL_SP2 = "　 ";
    //Normal JP normal JP normal
    private final String TEST_JP_SP_AND_NORMAL_SP3 = " 　 　 ";
    //JP normal JP normal JP
    private final String TEST_JP_SP_AND_NORMAL_SP4 = "　 　 　";
    //5 normal and 5JP
    private final String TEST_JP_SP_AND_NORMAL_SP5 = "     　　　　　";
    //5 JP and 5 normal
    private final String TEST_JP_SP_AND_NORMAL_SP6 = "　　　　　     ";

    @Test
    public void isFullOfSpace() {
        assertTrue(StringProcessUtilities.isEmpty(REAL_EMPTY));
        assertTrue(StringProcessUtilities.isEmpty(TEST_FULL_NORMAL_SPACE1));
        assertTrue(StringProcessUtilities.isEmpty(TEST_FULL_NORMAL_SPACE2));
        assertTrue(StringProcessUtilities.isEmpty(TEST_FULL_JP_SPACE1));
        assertTrue(StringProcessUtilities.isEmpty(TEST_FULL_JP_SPACE2));
        assertTrue(StringProcessUtilities.isEmpty(TEST_JP_SP_AND_NORMAL_SP1));
        assertTrue(StringProcessUtilities.isEmpty(TEST_JP_SP_AND_NORMAL_SP2));
        assertTrue(StringProcessUtilities.isEmpty(TEST_JP_SP_AND_NORMAL_SP3));
        assertTrue(StringProcessUtilities.isEmpty(TEST_JP_SP_AND_NORMAL_SP4));
        assertTrue(StringProcessUtilities.isEmpty(TEST_JP_SP_AND_NORMAL_SP5));
        assertTrue(StringProcessUtilities.isEmpty(TEST_JP_SP_AND_NORMAL_SP6));
    }
}