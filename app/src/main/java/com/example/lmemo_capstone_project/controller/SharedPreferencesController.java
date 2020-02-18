package com.example.lmemo_capstone_project.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lmemo_capstone_project.R;

public class SharedPreferencesController {
    private static Context context;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void setContext(Context context) {
        SharedPreferencesController.context = context;
        SharedPreferencesController.sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferencesController.editor = sharedPreferences.edit();
    }

    public static boolean hasDictionaryData() {
        return sharedPreferences.getBoolean(
                context.getString(R.string.dictionary_state), false);
    }

    public static void setDictionaryDataState(boolean hasData) {
        editor.putBoolean(context.getString(R.string.dictionary_state), hasData);
    }
}
