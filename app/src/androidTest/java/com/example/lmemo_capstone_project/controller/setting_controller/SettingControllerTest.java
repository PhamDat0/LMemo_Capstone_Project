package com.example.lmemo_capstone_project.controller.setting_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.SharedPreferencesController;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class SettingControllerTest {

    private final boolean EXPECTED_DAILY_1 = true;
    private final boolean EXPECTED_DAILY_2 = false;
    private final boolean EXPECTED_REMINDER_1 = true;
    private final boolean EXPECTED_REMINDER_2 = false;
    private final boolean EXPECTED_DAILY_DISMISSIBLE_1 = true;
    private final boolean EXPECTED_DAILY_DISMISSIBLE_2 = false;
    private final boolean EXPECTED_REMINDER_DISMISSIBLE_1 = true;
    private final boolean EXPECTED_REMINDER_DISMISSIBLE_2 = false;
    private final String EXPECTED_HOURS_1 = "0";
    private final String EXPECTED_HOURS_2 = "23";
    private final String EXPECTED_HOURS_3 = "10";
    private final String EXPECTED_MINUTE_1 = "0";
    private final String EXPECTED_MINUTE_2 = "59";
    private final String EXPECTED_MINUTE_3 = "20";
    private Context appContext;
    private SettingController settingController;

    @Before
    public void initTestController() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        settingController = new SettingController();
    }

    @Test
    public void saveSettingToSharePreferences() {
        //test 1:
        settingController.saveSettingToSharePreferences(appContext, EXPECTED_DAILY_1,
                EXPECTED_DAILY_DISMISSIBLE_1, Integer.parseInt(EXPECTED_HOURS_1), Integer.parseInt(EXPECTED_MINUTE_1), EXPECTED_REMINDER_1,
                EXPECTED_REMINDER_DISMISSIBLE_1, Integer.parseInt(EXPECTED_HOURS_1), Integer.parseInt(EXPECTED_MINUTE_1));
        assertEquals(SharedPreferencesController.dailyWordIsOn(appContext), EXPECTED_DAILY_1);
        assertEquals(SharedPreferencesController.dismissDailyWordIsOn(appContext), EXPECTED_DAILY_1);
        assertForHourAndMinuteForDaily(EXPECTED_HOURS_1, EXPECTED_MINUTE_1);
        assertEquals(SharedPreferencesController.reminderIsOn(appContext), EXPECTED_REMINDER_1);
        assertEquals(SharedPreferencesController.dismissReminderIsOn(appContext), EXPECTED_REMINDER_DISMISSIBLE_1);
        assertForHourAndMinuteForReminder(EXPECTED_HOURS_1, EXPECTED_MINUTE_1);
        //test 2:
        settingController.saveSettingToSharePreferences(appContext, EXPECTED_DAILY_2,
                EXPECTED_DAILY_DISMISSIBLE_2, Integer.parseInt(EXPECTED_HOURS_2), Integer.parseInt(EXPECTED_MINUTE_2), EXPECTED_REMINDER_2,
                EXPECTED_REMINDER_DISMISSIBLE_2, Integer.parseInt(EXPECTED_HOURS_2), Integer.parseInt(EXPECTED_MINUTE_2));
        assertEquals(SharedPreferencesController.dailyWordIsOn(appContext), EXPECTED_DAILY_2);
        assertEquals(SharedPreferencesController.dismissDailyWordIsOn(appContext), EXPECTED_DAILY_2);
        assertForHourAndMinuteForDaily(EXPECTED_HOURS_2, EXPECTED_MINUTE_2);
        assertEquals(SharedPreferencesController.reminderIsOn(appContext), EXPECTED_REMINDER_2);
        assertEquals(SharedPreferencesController.dismissReminderIsOn(appContext), EXPECTED_REMINDER_DISMISSIBLE_2);
        assertForHourAndMinuteForReminder(EXPECTED_HOURS_2, EXPECTED_MINUTE_2);
        //test 3:
        settingController.saveSettingToSharePreferences(appContext, EXPECTED_DAILY_1,
                EXPECTED_DAILY_DISMISSIBLE_1, Integer.parseInt(EXPECTED_HOURS_3), Integer.parseInt(EXPECTED_MINUTE_3), EXPECTED_REMINDER_1,
                EXPECTED_REMINDER_DISMISSIBLE_1, Integer.parseInt(EXPECTED_HOURS_3), Integer.parseInt(EXPECTED_MINUTE_3));
        assertEquals(SharedPreferencesController.dailyWordIsOn(appContext), EXPECTED_DAILY_1);
        assertEquals(SharedPreferencesController.dismissDailyWordIsOn(appContext), EXPECTED_DAILY_1);
        assertForHourAndMinuteForDaily(EXPECTED_HOURS_3, EXPECTED_MINUTE_3);
        assertEquals(SharedPreferencesController.reminderIsOn(appContext), EXPECTED_REMINDER_1);
        assertEquals(SharedPreferencesController.dismissReminderIsOn(appContext), EXPECTED_REMINDER_DISMISSIBLE_1);
        assertForHourAndMinuteForReminder(EXPECTED_HOURS_3, EXPECTED_MINUTE_3);
        //test 4
        settingController.saveSettingToSharePreferences(appContext, EXPECTED_DAILY_1,
                EXPECTED_DAILY_DISMISSIBLE_2, Integer.parseInt(EXPECTED_HOURS_1), Integer.parseInt(EXPECTED_MINUTE_1), EXPECTED_REMINDER_2,
                EXPECTED_REMINDER_DISMISSIBLE_1, Integer.parseInt(EXPECTED_HOURS_3), Integer.parseInt(EXPECTED_MINUTE_3));
        assertEquals(SharedPreferencesController.dailyWordIsOn(appContext), EXPECTED_DAILY_1);
        assertEquals(SharedPreferencesController.dismissDailyWordIsOn(appContext), EXPECTED_DAILY_2);
        assertForHourAndMinuteForDaily(EXPECTED_HOURS_1, EXPECTED_MINUTE_1);
        assertEquals(SharedPreferencesController.reminderIsOn(appContext), EXPECTED_REMINDER_2);
        assertEquals(SharedPreferencesController.dismissReminderIsOn(appContext), EXPECTED_REMINDER_DISMISSIBLE_1);
        assertForHourAndMinuteForReminder(EXPECTED_HOURS_3, EXPECTED_MINUTE_3);
    }

    private void assertForHourAndMinuteForReminder(String expected_hours_1, String expected_minute_1) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(SharedPreferencesController.getReminderTime(appContext));
        assertEquals(c.get(Calendar.HOUR_OF_DAY) + "", expected_hours_1);
        assertEquals(c.get(Calendar.MINUTE) + "", expected_minute_1);
    }

    private void assertForHourAndMinuteForDaily(String expected_hours_1, String expected_minute_1) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(SharedPreferencesController.getDailyWordTime(appContext));
        assertEquals(c.get(Calendar.HOUR_OF_DAY) + "", expected_hours_1);
        assertEquals(c.get(Calendar.MINUTE) + "", expected_minute_1);
    }
}