package com.example.lmemo_capstone_project.controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;

import org.junit.Test;

public class DictionaryFileReaderTest {

    @Test
    public void testDeleteAllRemainWords() {
        // Context of the app under test.
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LMemoDatabase.getInstance(context);

    }
}