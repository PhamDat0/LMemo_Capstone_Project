package com.example.lmemo_capstone_project.controller.setting_controller;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.lmemo_capstone_project.controller.SharedPreferencesController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SettingController {
    private List<String> listHour;
    private List<String> listMinute;

    public SettingController() {
        listHour = new ArrayList<>();
        listMinute = new ArrayList<>();
    }

    public void setDailyWordDismissSwitch(Context context, Switch dailyWordDismissSwitch) {
        //set daily word switch dismiss is checked or not when load UI
        if (SharedPreferencesController.dismissDailyWordIsOn(context)) {
            dailyWordDismissSwitch.setChecked(true);
        } else {
            dailyWordDismissSwitch.setChecked(false);
        }
    }

    public void setReminderDismissSwitch(Context context, Switch reminderDismissSwitch) {
        // set reminder switch  dismiss is checked or not when loading UI
        if (SharedPreferencesController.dismissReminderIsOn(context)) {
            reminderDismissSwitch.setChecked(true);
        } else {
            reminderDismissSwitch.setChecked(false);
        }
    }

    public void setDailyWordSwitch(Context context, Switch dailyWordSwitch) {
        //set daily word switch is checked or not when load UI
        if (SharedPreferencesController.dailyWordIsOn(context)) {
            dailyWordSwitch.setChecked(true);
        } else {
            dailyWordSwitch.setChecked(false);
        }
    }

    public void setReminderSwitch(Context context, Switch reminderSwitch) {
        // set reminder switch is checked or not when loading UI
        if (SharedPreferencesController.reminderIsOn(context)) {
            reminderSwitch.setChecked(true);
        } else {
            reminderSwitch.setChecked(false);
        }
    }

    public void setDailyWordTimeSpinner(Context context, Spinner dailyWordHour, Spinner dailyWordMin) {
        // set time of daily word time spinner when loading UI
        long time = SharedPreferencesController.getDailyWordTime(context);
        Date date = new Date(time);
        int hour = date.getHours();
        int minute = date.getMinutes();
        dailyWordHour.setSelection(((ArrayAdapter<String>) dailyWordHour.getAdapter()).getPosition("" + hour));
        dailyWordMin.setSelection(((ArrayAdapter<String>) dailyWordMin.getAdapter()).getPosition("" + minute));
    }

    public void setReminderTimeSpinner(Context context, Spinner reminderHour, Spinner reminderMin) {
        //set time of reminder time spinner when loading UI
        long time = SharedPreferencesController.getReminderTime(context);
        Date date = new Date(time);
        int hour = date.getHours();
        int minute = date.getMinutes();
        reminderHour.setSelection(((ArrayAdapter<String>) reminderHour.getAdapter()).getPosition("" + hour));
        reminderMin.setSelection(((ArrayAdapter<String>) reminderMin.getAdapter()).getPosition("" + minute));
    }

    public void addValueToTimeSpinner(Spinner hourSpinner, Spinner minuteSpinner, Context context) {
        //add value to time spinner
        if (listHour.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                listHour.add("" + i);
            }
        }
        if (listMinute.isEmpty()) {
            for (int i = 0; i < 60; i++) {
                listMinute.add("" + i);
            }
        }
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, listHour);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, listMinute);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteSpinner.setAdapter(minuteAdapter);
    }

    private void setupDailyWordTime(Spinner hourSpinner, Spinner minSpinner, Context context) {
        //get data from UI and set daily word time
        int hour = Integer.parseInt(hourSpinner.getSelectedItem().toString());
        int min = Integer.parseInt(minSpinner.getSelectedItem().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        Log.w("Setting Controller", calendar.toString());
        long dailyWordTime = calendar.getTimeInMillis();
        SharedPreferencesController.setDailyWordTime(context, dailyWordTime);
    }

    private void setupReminderTime(Spinner hourSpinner, Spinner minSpinner, Context context) {
        //get data from UI and set reminder time
        int hour = Integer.parseInt(hourSpinner.getSelectedItem().toString());
        int min = Integer.parseInt(minSpinner.getSelectedItem().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        long reminderTime = calendar.getTimeInMillis();
        SharedPreferencesController.setReminderTime(context, reminderTime);
    }

    public void controlOnClickEvent(Context context, Switch dailyWordSwtich, Switch dailyWordDismissSwitch, Spinner dailyWordHour, Spinner dailyWordMin,
                                    Switch reminderSwtich, Switch reminderDismissSwitch, Spinner reminderHour, Spinner reminderMin) {
        // control on click event of save button
        if (dailyWordSwtich.isChecked()) {
            SharedPreferencesController.setDailyWordStatus(context, true);
            setupDailyWordTime(dailyWordHour, dailyWordMin, context);
            if (dailyWordDismissSwitch.isChecked()) {
                SharedPreferencesController.setDailyWordDismissStatus(context, true);
            } else {
                SharedPreferencesController.setDailyWordDismissStatus(context, false);
            }
        } else {
            SharedPreferencesController.setDailyWordStatus(context, false);
        }
        if (reminderSwtich.isChecked()) {
            SharedPreferencesController.setReminderStatus(context, true);
            setupReminderTime(reminderHour, reminderMin, context);
            if (reminderDismissSwitch.isChecked()) {
                SharedPreferencesController.setReminderDismissStatus(context, true);
            } else {
                SharedPreferencesController.setReminderDismissStatus(context, false);
            }
        } else if (!reminderSwtich.isChecked()) {
            SharedPreferencesController.setReminderStatus(context, false);
        }
        Toast.makeText(context, "Save Setting Successful!", Toast.LENGTH_LONG).show();
    }
}
