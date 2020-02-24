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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LMemoDatabase.getInstance(getApplicationContext());
        SharedPreferencesController.setDictionaryDataState(getApplicationContext(), false);
        SharedPreferencesController.setDictionaryDataState(getApplicationContext(), false);
        Log.i("SHARE_PRE", SharedPreferencesController.hasDictionaryData(getApplicationContext()) + "");

        loadDictionaryDatabase();

    }

    private void updateProgressBar() {
        ProgressBar pb = findViewById(R.id.LOAD_DATA_PROGRESS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pb.setProgress(pb.getProgress() + 1, true);
        } else {
            pb.setProgress(pb.getProgress() + 1);
        }
    }

    private void loadDictionaryDatabase() {
        //Start read file to SQLite
        final Thread dictionaryFileReader = new DictionaryFileReader(getApplicationContext());
        dictionaryFileReader.start();
        final Thread kanjiReader = new KanjiFileReader(getApplicationContext());
        kanjiReader.start();
        Thread progressBarUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                while (kanjiReader.isAlive() || dictionaryFileReader.isAlive()) {
                    try {
                        Thread.sleep(3000);
                        updateProgressBar();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                Toast.makeText(getApplicationContext(), "Load dictionary data successfully.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        progressBarUpdate.start();
        //At the same time display a loading bar
        //After done change the Preference
    }


}
