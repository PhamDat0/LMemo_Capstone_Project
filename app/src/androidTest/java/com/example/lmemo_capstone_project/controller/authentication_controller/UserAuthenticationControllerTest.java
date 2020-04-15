package com.example.lmemo_capstone_project.controller.authentication_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UserAuthenticationControllerTest {

    private static final String GUEST_ID = "GUEST";
    private UserAuthenticationController userAuthenticationController;
    private UserDAO userDAO;

    @Before
    public void initTestController() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        userAuthenticationController = new UserAuthenticationController(appContext);
        userDAO = LMemoDatabase.getInstance(appContext).userDAO();
    }

    @Test
    public void useAppAsGuestTest() {
        userAuthenticationController.useAppAsGuest();
        User user = userDAO.getLocalUser()[0];
        assertTrue(user.getUserID().equalsIgnoreCase(GUEST_ID));
    }
}