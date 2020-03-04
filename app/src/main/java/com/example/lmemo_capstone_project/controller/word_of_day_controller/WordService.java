package com.example.lmemo_capstone_project.controller.word_of_day_controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.Calendar;

public class WordService extends Service {
    public WordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        startAlarm(true,true);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }
    private void startAlarm(boolean isNotification, boolean isRepeat) {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        //THIS IS WHERE YOU SET NOTIFICATION TIME FOR CASES WHEN THE NOTIFICATION NEEDS TO BE RESCHEDULED
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE,00);

        myIntent = new Intent(this,WordOfDayReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);


        if(!isRepeat)
            manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+3000,pendingIntent);
        else
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
    }
}
