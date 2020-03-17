package com.example.lmemo_capstone_project.controller.flashcard_controller;

import android.app.Activity;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class FlashcardControllerTest extends TestCase {

    private ArrayList<Word> listFlashcard;
    private FlashcardController flashcardController;
    private FlashcardDAO flashcardDAO;
    private WordDAO wordDAO;

    @Before
    public void initTestController() {
////        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        wordDAO = LMemoDatabase.getInstance(appContext).wordDAO();
//        flashcardDAO = LMemoDatabase.getInstance(appContext).flashcardDAO();
//        flashcardController = new FlashcardController(, listFlashcard);

    }

    @Test
    public void delete() {
    }

    @Test
    public void flashcardInfo() {
    }
}