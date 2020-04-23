package com.example.lmemo_capstone_project.controller.internet_checking_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InternetCheckingControllerTest {

    //Pre-condition: The test device is online
    @Test
    public void isOnline() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertTrue(InternetCheckingController.isOnline(appContext));
    }

    //Pre-condition: The test device is offline
    @Test
    public void isOffline() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertFalse(InternetCheckingController.isOnline(appContext));
    }
}