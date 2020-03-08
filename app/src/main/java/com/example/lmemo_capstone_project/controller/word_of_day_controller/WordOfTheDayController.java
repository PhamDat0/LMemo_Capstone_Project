package com.example.lmemo_capstone_project.controller.word_of_day_controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import java.util.Calendar;

public class WordOfTheDayController {
    private String CHANNEL_ID = "channel1";
    private Activity activity;

    public WordOfTheDayController() {

    }

    public void createNotificationChannel(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is channel 1");
            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startAlarm(boolean isNotification, boolean isRepeat, Activity activity) {

        Intent myIntent;
        PendingIntent pendingIntent;

        // SET TIME HERE
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 00);
        myIntent = new Intent(activity.getApplicationContext(), WordOfDayReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) activity.getSystemService(activity.getApplicationContext().ALARM_SERVICE);
        if (!isRepeat)
            manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
        else
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


}
