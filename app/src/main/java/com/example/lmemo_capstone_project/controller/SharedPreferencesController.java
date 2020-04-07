package com.example.lmemo_capstone_project.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lmemo_capstone_project.R;

public class SharedPreferencesController {

    public static boolean hasDictionaryData(Context context) {
        return getSharedPreferences(context).getBoolean(
                context.getString(R.string.dictionary_state), false);
    }

    public static boolean hasKanjiData(Context context) {
        return getSharedPreferences(context).getBoolean(
                context.getString(R.string.kanji_state), false);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public static void setDictionaryDataState(Context context, boolean hasData) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(context.getString(R.string.dictionary_state), hasData);
        editor.commit();
    }

    public static void setKanjiDataState(Context context, boolean hasData) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(context.getString(R.string.kanji_state), hasData);
        editor.commit();
        Log.i("TEST", hasKanjiData(context) + "");
    }


    public static boolean dailyWordIsOn(Context context){
            return getSharedPreferences(context).getBoolean(
                    context.getString(R.string.daily_word_state), false);
    }
    public static void setDailyWordStatus(Context context, boolean isOn){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(context.getString(R.string.daily_word_state), isOn);
        editor.commit();

    }
    public static boolean reminderIsOn(Context context){
        return getSharedPreferences(context).getBoolean(
                context.getString(R.string.reminder_state), false);
    }
    public static void setReminderStatus(Context context, boolean isOn){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(context.getString(R.string.reminder_state), isOn);
        editor.commit();

    }
    public static void setDailyWordTime(Context context, long time){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(context.getString(R.string.daily_word_time), time);
        editor.commit();
    }
    public static long getDailyWordTime(Context context){
        return getSharedPreferences(context).getLong(
                context.getString(R.string.daily_word_time), 0);
    }
    public static void setReminderTime(Context context, long time){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(context.getString(R.string.reminder_time), time);
        editor.commit();
    }
    public static long getReminderTime(Context context){
        return getSharedPreferences(context).getLong(
                context.getString(R.string.reminder_time), 0);
    }
    public static boolean dismissDailyWordIsOn(Context context){
        return getSharedPreferences(context).getBoolean(
                context.getString(R.string.daily_word_dismiss_status), false);
    }
    public static void setDailyWordDismissStatus(Context context, boolean isOn){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(context.getString(R.string.daily_word_dismiss_status), isOn);
        editor.commit();
    }
    public static boolean dismissReminderIsOn(Context context){
        return getSharedPreferences(context).getBoolean(
                context.getString(R.string.reminder_dismiss_status), false);
    }
    public static void setReminderDismissStatus(Context context, boolean isOn){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(context.getString(R.string.reminder_dismiss_status), isOn);
        editor.commit();
    }
}
