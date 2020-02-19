package com.example.lmemo_capstone_project.controller.database_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.assertSame;

public class LMemoDatabaseTest {

    @Test
    public void testSingleton() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LMemoDatabase lMemoDatabase = LMemoDatabase.getInstance(appContext);
        assertSame(lMemoDatabase, LMemoDatabase.getInstance());
    }

}