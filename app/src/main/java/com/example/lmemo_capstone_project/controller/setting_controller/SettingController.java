package com.example.lmemo_capstone_project.controller.setting_controller;

import android.content.Context;
import android.util.Log;

import com.example.lmemo_capstone_project.controller.SharedPreferencesController;

import java.util.Calendar;

public class SettingController {

    public SettingController() {
    }

    private void setupDailyWordTime(String hourString, String minString, Context context) {
        //get data from UI and set daily time
        int hour = Integer.parseInt(hourString);
        int min = Integer.parseInt(minString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        Log.w("Setting Controller", calendar.toString());
        long dailyWordTime = calendar.getTimeInMillis();
        SharedPreferencesController.setDailyWordTime(context, dailyWordTime);
    }


    private void setupReminderTime(String hourString, String minString, Context context) {
        //get data from UI and set reminder time
        int hour = Integer.parseInt(hourString);
        int min = Integer.parseInt(minString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        long reminderTime = calendar.getTimeInMillis();
        SharedPreferencesController.setReminderTime(context, reminderTime);
    }

    public void saveSettingToSharePreferences(Context context, boolean isDailyWordOn, boolean isDailyWordDismissible, String dailyWordHour, String dailyWordMin,
                                              boolean isReminderOn, boolean isReminderDismissible, String reminderHour, String reminderMin) {
        // control on click event of save button
        SharedPreferencesController.setDailyWordStatus(context, isDailyWordOn);
        setupDailyWordTime(dailyWordHour, dailyWordMin, context);
        SharedPreferencesController.setDailyWordDismissStatus(context, isDailyWordDismissible);
        SharedPreferencesController.setReminderStatus(context, isReminderOn);
        setupReminderTime(reminderHour, reminderMin, context);
        SharedPreferencesController.setReminderDismissStatus(context, isReminderDismissible);
//        if (isDailyWordOn) {
//            SharedPreferencesController.setDailyWordStatus(context, true);
//            setupDailyWordTime(dailyWordHour, dailyWordMin, context);
//            if (isDailyWordDismissible) {
//                SharedPreferencesController.setDailyWordDismissStatus(context, true);
//            } else {
//                SharedPreferencesController.setDailyWordDismissStatus(context, false);
//            }
//        } else {
//            SharedPreferencesController.setDailyWordStatus(context, false);
//        }
//        if (isReminderOn) {
//            SharedPreferencesController.setReminderStatus(context, true);
//            setupReminderTime(reminderHour, reminderMin, context);
//            if (isReminderDismissible) {
//                SharedPreferencesController.setReminderDismissStatus(context, true);
//            } else {
//                SharedPreferencesController.setReminderDismissStatus(context, false);
//            }
//        } else {
//            SharedPreferencesController.setReminderStatus(context, false);
//        }
    }
}
