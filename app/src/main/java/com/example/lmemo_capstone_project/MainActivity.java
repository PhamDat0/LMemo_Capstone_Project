package com.example.lmemo_capstone_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lmemo_capstone_project.controller.SharedPreferencesController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LMemoDatabase.getInstance(getApplicationContext());
        SharedPreferencesController.setContext(getApplicationContext());
        if (!SharedPreferencesController.hasDictionaryData()) {
            loadDictionaryDatabase();
        }
    }

    private void loadDictionaryDatabase() {
        //Start read file to SQLite
        //At the same time display a loading bar
        //After done change the Preference
    }
}
