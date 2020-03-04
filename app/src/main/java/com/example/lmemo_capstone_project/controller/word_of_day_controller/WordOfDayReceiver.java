package com.example.lmemo_capstone_project.controller.word_of_day_controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;


public class WordOfDayReceiver extends BroadcastReceiver {
    private WordDAO wordDB;
    private String contentText;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel1");
        wordDB = LMemoDatabase.getInstance(context.getApplicationContext()).wordDAO();
        Word word = wordDB.getDailyWord()[0];
        contentText = word.getKana();
        int wordID = -1;
        Intent myIntent = new Intent(context, HomeActivity.class);
        wordID = word.getWordID();
        myIntent.putExtra("wordID", wordID);
        Log.w("HomeActivity", "Got Something from broadcast"+word.getWordID());
        myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.dictionicon)
                .setContentTitle("Word Of The Day")
                .setContentIntent(pendingIntent)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }
}
