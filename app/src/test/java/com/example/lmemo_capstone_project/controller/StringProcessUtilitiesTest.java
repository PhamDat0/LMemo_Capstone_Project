package com.example.lmemo_capstone_project.controller;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringProcessUtilitiesTest {
    private final String NOT_EMPTY = "  nbia　　w 　 nb";
    private final String REAL_EMPTY = "";
    private final String TEST_FULL_NORMAL_SPACE1 = "  ";
    private final String TEST_FULL_NORMAL_SPACE2 = "     ";
    private final String TEST_FULL_JP_SPACE1 = "　　";
    private final String TEST_FULL_JP_SPACE2 = "　　　　　";
    private final String TEST_FULL_NEWLINE1 = "\n\n";
    private final String TEST_FULL_NEWLINE2 = "\n\n\n\n\n";
    private final String TEST_FULL_TAB1 = "\t\t";
    private final String TEST_FULL_TAB2 = "\t\t\t\t\t";
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
    //JP and normal and newline
    private final String TEST_JP_NORMAL_NL1 = " 　\n";
    private final String TEST_JP_NORMAL_NL2 = " 　\n 　\n\n　 ";

    @Test
    public void isEmpty() {
        assertFalse(StringProcessUtilities.isEmpty(NOT_EMPTY));
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
        assertTrue(StringProcessUtilities.isEmpty(TEST_FULL_NEWLINE1));
        assertTrue(StringProcessUtilities.isEmpty(TEST_FULL_NEWLINE2));
        assertTrue(StringProcessUtilities.isEmpty(TEST_FULL_TAB1));
        assertTrue(StringProcessUtilities.isEmpty(TEST_FULL_TAB2));
        assertTrue(StringProcessUtilities.isEmpty(TEST_JP_NORMAL_NL1));
        assertTrue(StringProcessUtilities.isEmpty(TEST_JP_NORMAL_NL2));
    }
}