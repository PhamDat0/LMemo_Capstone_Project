package com.example.lmemo_capstone_project.controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SharedPreferencesControllerTest {

    @Test
    public void hasDictionaryData() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferencesController.setContext(context);
        assertEquals(SharedPreferencesController.hasDictionaryData(), false);
    }

    @Test
    public void setDictionaryDataState() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferencesController.setContext(context);
        SharedPreferencesController.setDictionaryDataState(true);
        assertEquals(SharedPreferencesController.hasDictionaryData(), true);
    }
}