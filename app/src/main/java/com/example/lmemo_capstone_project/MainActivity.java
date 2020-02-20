package com.example.lmemo_capstone_project;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lmemo_capstone_project.controller.SharedPreferencesController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.dictrionary_data_controller.DictionaryFileReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LMemoDatabase.getInstance(getApplicationContext());
//        SharedPreferencesController.setDictionaryDataState(getApplicationContext(),false);
        Log.i("SHARE_PRE", SharedPreferencesController.hasDictionaryData(getApplicationContext()) + "");
        if (!SharedPreferencesController.hasDictionaryData(getApplicationContext())) {
            loadDictionaryDatabase();
        }
//        TextView textView =(TextView)findViewById(R.id.textview);
//        textView.setText(LMemoDatabase.getInstance(getApplicationContext()).wordDAO().getWords("おとうさん")[0].getKanjiWriting());
    }

    private void loadDictionaryDatabase() {
        //Start read file to SQLite
        Thread reader = new DictionaryFileReader(getApplicationContext());
        reader.start();
//        SharedPreferencesController.setDictionaryDataState(getApplicationContext(), true);
        //At the same time display a loading bar
        //After done change the Preference
    }


}
