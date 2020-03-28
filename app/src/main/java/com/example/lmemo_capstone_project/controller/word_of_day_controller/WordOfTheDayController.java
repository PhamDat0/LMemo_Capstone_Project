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

    public static void startAlarm(boolean isNotification, boolean isRepeat, Activity activity, long dailyTime) {

        Intent myIntent;
        PendingIntent pendingIntent;

            // SET TIME HERE
            myIntent = new Intent(activity.getApplicationContext(), WordOfDayReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (isNotification){
            AlarmManager manager = (AlarmManager) activity.getApplicationContext().getSystemService(activity.getApplicationContext().ALARM_SERVICE);
            if (!isRepeat)
                manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
            else {
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, dailyTime, AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }


    }


}
