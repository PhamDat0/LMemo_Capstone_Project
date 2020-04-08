package com.example.lmemo_capstone_project.controller.comment_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;

import org.junit.Before;
import org.junit.Test;

public class CommentControllerTest {
    private static final String NOTE_CONTENT_2 = "This is for testing editing";
    private final String NOTE_ONLINE_ID1 = "0YE0S8UQKlfJlcL5y6Fn";
    private final String NOTE_CONTENT_1 = "This is for testing adding";

    private CommentController commentController;
    private String content;
    private Note note;
    private Comment comment;
    private User currentUser;

    @Before
    public void initTestController() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        commentController = new CommentController(appContext);
        currentUser = LMemoDatabase.getInstance(appContext).userDAO().getLocalUser()[0];
        content = NOTE_CONTENT_1;
        note = new Note();
        note.setOnlineID(NOTE_ONLINE_ID1);
        comment = new Comment();

    }

    @Test
    public void addComment() {
        commentController.addComment(note, currentUser, content);
    }

    @Test
    public void editComment() {
        commentController.editComment(comment, NOTE_CONTENT_2);
    }

    @Test
    public void upvoteComment() {
    }

    @Test
    public void downvoteComment() {
    }

    @Test
    public void testAddComment() {
    }

    @Test
    public void testEditComment() {
    }

    @Test
    public void deleteCommentFromFB() {
    }

    @Test
    public void testUpvoteComment() {
    }

    @Test
    public void testDownvoteComment() {
    }
}