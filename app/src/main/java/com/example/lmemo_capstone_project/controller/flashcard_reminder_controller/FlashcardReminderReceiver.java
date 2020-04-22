package com.example.lmemo_capstone_project.controller.flashcard_reminder_controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.SharedPreferencesController;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;

public class FlashcardReminderReceiver extends BroadcastReceiver {
    private String contentText;
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel1");
        contentText = "It's time to study baby";
        Intent myIntent = new Intent(context, HomeActivity.class);
        myIntent.putExtra("flashcardReminderNotification", true);
        if (intent.getIntExtra("mode",HomeActivity.NOTI_FOR_WORD)==HomeActivity.NOTI_FOR_FC){
            myIntent.putExtra("mode", HomeActivity.NOTI_FOR_FC);
        }

        myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                HomeActivity.NOTI_FOR_FC,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.mainic1)
                .setContentTitle("Flashcard Reminder")
                .setContentIntent(pendingIntent)
                .setOngoing(SharedPreferencesController.dismissReminderIsOn(context))
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }
}
