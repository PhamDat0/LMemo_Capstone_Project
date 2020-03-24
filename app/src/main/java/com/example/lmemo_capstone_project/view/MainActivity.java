package com.example.lmemo_capstone_project.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.SharedPreferencesController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.dictrionary_data_controller.DictionaryFileReader;
import com.example.lmemo_capstone_project.controller.dictrionary_data_controller.KanjiFileReader;
import com.example.lmemo_capstone_project.controller.word_of_day_controller.WordOfTheDayController;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;

public class MainActivity extends AppCompatActivity {
    WordOfTheDayController wordOfTheDayController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LMemoDatabase.getInstance(getApplicationContext());
//        SharedPreferencesController.setDictionaryDataState(getApplicationContext(), false);
//        SharedPreferencesController.setDictionaryDataState(getApplicationContext(), false);
        Log.i("SHARE_PRE", SharedPreferencesController.hasDictionaryData(getApplicationContext()) + "");

        loadDictionaryDatabase();
        wordOfTheDayController = new WordOfTheDayController();
        wordOfTheDayController.createNotificationChannel(this);
    }

    @Override
    public void onStart() {

        super.onStart();
        wordOfTheDayController.startAlarm(true, false, this);
    }

    /**
     * この関数はプログレスバーの進度を1パーセント足します。
     */
    private void updateProgressBar() {
        ProgressBar pb = findViewById(R.id.LOAD_DATA_PROGRESS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pb.setProgress(pb.getProgress() + 1, true);
        } else {
            pb.setProgress(pb.getProgress() + 1);
        }
    }

    /**
     * この関数は漢字と言葉のファイルを読むことをスレッドに入れ、そのスレッドを始め、プログレスバーも
     * 進度を表します。
     */
    private void loadDictionaryDatabase() {
        final Thread dictionaryFileReader = new DictionaryFileReader(getApplicationContext());
        dictionaryFileReader.start();
        final Thread kanjiReader = new KanjiFileReader(getApplicationContext());
        kanjiReader.start();
        Thread progressBarUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                //漢字と言葉のファイルを読み終わってからでなければ、プログレスバーを更新し続けます。
                //終わったら、HomeActivityに転送します。
                while (kanjiReader.isAlive() || dictionaryFileReader.isAlive()) {
                    try {
                        Thread.sleep(3000);
                        updateProgressBar();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        progressBarUpdate.start();
    }


}
