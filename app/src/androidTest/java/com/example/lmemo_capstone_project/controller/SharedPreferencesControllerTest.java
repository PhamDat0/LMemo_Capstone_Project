package com.example.lmemo_capstone_project.controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SharedPreferencesControllerTest {

    @Test
    public void hasDictionaryData() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertFalse(SharedPreferencesController.hasDictionaryData(context));
    }

    @Test
    public void setDictionaryDataStateToTrue() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferencesController.setDictionaryDataState(context, true);
        assertTrue(SharedPreferencesController.hasDictionaryData(context));
    }

    @Test
    public void setDictionaryDataStateToFalse() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferencesController.setDictionaryDataState(context, false);
        assertFalse(SharedPreferencesController.hasDictionaryData(context));
    }
}