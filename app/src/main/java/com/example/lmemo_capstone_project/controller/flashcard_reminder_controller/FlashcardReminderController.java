package com.example.lmemo_capstone_project.controller.flashcard_reminder_controller;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import java.util.Calendar;

public class FlashcardReminderController {
    private String CHANNEL_ID = "channel2";

    public FlashcardReminderController() {

    }

    public void createNotificationChannel(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel 2", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is channel 1");
            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void startAlarm(boolean isNotification, boolean isRepeat, Activity activity, long dailyTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        calendar.setTimeInMillis(dailyTime);
        calendar.set(Calendar.DATE, date);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        if (!timeHasNotCome(calendar, hour, minute)) {

            calendar.add(Calendar.DATE, 1);
        }

        Intent myIntent;
        PendingIntent pendingIntent;


        myIntent = new Intent(activity.getApplicationContext(), FlashcardReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (isNotification) {
            AlarmManager manager = (AlarmManager) activity.getApplicationContext().getSystemService(activity.getApplicationContext().ALARM_SERVICE);
            if (!isRepeat)
                manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
            else {
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//                manager.setRepeating(AlarmManager.RTC_WAKEUP, dailyTime, AlarmManager.INTERVAL_DAY,pendingIntent);
            }
        }
        else{
            AlarmManager manager = (AlarmManager) activity.getApplicationContext().getSystemService(activity.getApplicationContext().ALARM_SERVICE);
            manager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private static boolean timeHasNotCome(Calendar calendar, int hour, int minute) {
        return calendar.get(Calendar.HOUR_OF_DAY) > hour || (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) > minute);
    }
}
