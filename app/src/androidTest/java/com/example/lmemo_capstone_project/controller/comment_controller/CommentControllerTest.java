package com.example.lmemo_capstone_project.controller.comment_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;

import org.junit.Before;
import org.junit.Test;

public class CommentControllerTest {
    private final String NOTE_ONLINE_ID1 = "0YE0S8UQKlfJlcL5y6Fn";
    private final String NOTE_ONLINE_ID2 = "3Ldm0viUGfCIEu8dkHFo";

    private CommentController commentController;
    private String content;
    private Note note;
    private User currentUser;

    @Before
    public void initTestController() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        commentController = new CommentController(appContext);
        currentUser = LMemoDatabase.getInstance(appContext).userDAO().getLocalUser()[0];
        content = "This is for testing adding";
        note = new Note();
        note.setOnlineID(NOTE_ONLINE_ID1);
    }

    @Test
    public void addComment() {
        commentController.addComment(note, currentUser, content);
    }

    @Test
    public void editComment() {
    }

    @Test
    public void upvoteComment() {
    }

    @Test
    public void downvoteComment() {
    }
}