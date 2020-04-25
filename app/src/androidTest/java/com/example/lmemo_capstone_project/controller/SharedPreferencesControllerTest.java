package com.example.lmemo_capstone_project.controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SharedPreferencesControllerTest {

    private final long TIME_EXPECTED1 = Long.MAX_VALUE;
    private final long TIME_EXPECTED2 = Long.MIN_VALUE;
    private final long TIME_EXPECTED3 = 5862369;
    private Context appContext;

    @Before
    public void initTestController() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void setAndGetDictionaryDataStateToFalse() {
        SharedPreferencesController.setDictionaryDataState(appContext, false);
        assertFalse(SharedPreferencesController.hasDictionaryData(appContext));
    }

    @Test
    public void setAndGetDictionaryDataStateToTrue() {
        SharedPreferencesController.setDictionaryDataState(appContext, true);
        assertTrue(SharedPreferencesController.hasDictionaryData(appContext));
    }

    @Test
    public void setAndGetKanjiDataStateToFalse() {
        SharedPreferencesController.setKanjiDataState(appContext, false);
        assertFalse(SharedPreferencesController.hasKanjiData(appContext));
    }

    @Test
    public void setAndGetKanjiDataStateToTrue() {
        SharedPreferencesController.setKanjiDataState(appContext, true);
        assertTrue(SharedPreferencesController.hasKanjiData(appContext));
    }

    @Test
    public void setAndGetDailyToOff() {
        SharedPreferencesController.setDailyWordStatus(appContext, false);
        assertFalse(SharedPreferencesController.dailyWordIsOn(appContext));
    }

    @Test
    public void setAndGetDailyToOn() {
        SharedPreferencesController.setDailyWordStatus(appContext, true);
        assertTrue(SharedPreferencesController.dailyWordIsOn(appContext));
    }

    @Test
    public void setAndGetDailyDismissibleToOff() {
        SharedPreferencesController.setDailyWordDismissStatus(appContext, false);
        assertFalse(SharedPreferencesController.dismissDailyWordIsOn(appContext));
    }

    @Test
    public void setAndGetDailyDismissibleToOn() {
        SharedPreferencesController.setDailyWordDismissStatus(appContext, true);
        assertTrue(SharedPreferencesController.dismissDailyWordIsOn(appContext));
    }

    @Test
    public void setAndGetReminderToOff() {
        SharedPreferencesController.setReminderStatus(appContext, false);
        assertFalse(SharedPreferencesController.reminderIsOn(appContext));
    }

    @Test
    public void setAndGetReminderToOn() {
        SharedPreferencesController.setReminderStatus(appContext, true);
        assertTrue(SharedPreferencesController.reminderIsOn(appContext));
    }

    @Test
    public void setAndGetReminderDismissibleToOff() {
        SharedPreferencesController.setReminderDismissStatus(appContext, false);
        assertFalse(SharedPreferencesController.dismissReminderIsOn(appContext));
    }

    @Test
    public void setAndGetReminderDismissibleToOn() {
        SharedPreferencesController.setReminderDismissStatus(appContext, true);
        assertTrue(SharedPreferencesController.dismissReminderIsOn(appContext));
    }

    @Test
    public void setAndGetDailyTime() {
        SharedPreferencesController.setDailyWordTime(appContext, TIME_EXPECTED1);
        assertEquals(TIME_EXPECTED1, SharedPreferencesController.getDailyWordTime(appContext));
        SharedPreferencesController.setDailyWordTime(appContext, TIME_EXPECTED2);
        assertEquals(TIME_EXPECTED2, SharedPreferencesController.getDailyWordTime(appContext));
        SharedPreferencesController.setDailyWordTime(appContext, TIME_EXPECTED3);
        assertEquals(TIME_EXPECTED3, SharedPreferencesController.getDailyWordTime(appContext));
    }

    @Test
    public void setAndGetReminderTime() {
        SharedPreferencesController.setReminderTime(appContext, TIME_EXPECTED1);
        assertEquals(TIME_EXPECTED1, SharedPreferencesController.getReminderTime(appContext));
        SharedPreferencesController.setReminderTime(appContext, TIME_EXPECTED2);
        assertEquals(TIME_EXPECTED2, SharedPreferencesController.getReminderTime(appContext));
        SharedPreferencesController.setReminderTime(appContext, TIME_EXPECTED3);
        assertEquals(TIME_EXPECTED3, SharedPreferencesController.getReminderTime(appContext));
    }
}