package com.example.lmemo_capstone_project.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lmemo_capstone_project.R;

public class SharedPreferencesController {

    public static boolean hasDictionaryData(Context context) {
        return getSharedPreferences(context).getBoolean(
                context.getString(R.string.dictionary_state), false);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public static void setDictionaryDataState(Context context, boolean hasData) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(context.getString(R.string.dictionary_state), hasData);
        editor.apply();
    }
}
