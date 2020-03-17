package com.example.lmemo_capstone_project.controller.flashcard_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FlashcardControllerTest {

    private List<Word> listFlashcard;
    private FlashcardController flashcardController;
    private FlashcardDAO flashcardDAO;
    private WordDAO wordDAO;
    private int testPosition1, testPosition2, testPosition3;

    @Before
    public void initTestController() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        wordDAO = LMemoDatabase.getInstance(appContext).wordDAO();
        flashcardDAO = LMemoDatabase.getInstance(appContext).flashcardDAO();
        listFlashcard = new ArrayList<>();
        Flashcard[] allVisibleFlashcard = flashcardDAO.getAllVisibleFlashcard();
        for (Flashcard flashcard : allVisibleFlashcard) {
            listFlashcard.add(wordDAO.getOneWord(flashcard.getFlashcardID()));
        }
        flashcardController = new FlashcardController(appContext, listFlashcard);
        testPosition1 = 0;
        testPosition3 = listFlashcard.size() - 1;
        testPosition2 = (testPosition1 + testPosition3) / 2;
    }

    @Test
    public void delete1() {
        //Test case 1, with position 0
        Flashcard expectedFlashcard1 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition1).getWordID())[0];
        expectedFlashcard1.setLastState(99);
        flashcardController.delete(testPosition1);
        Flashcard actualFlashcard1 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition1).getWordID())[0];
        assertEqualsFlashcard(expectedFlashcard1, actualFlashcard1);
    }

    private void assertEqualsFlashcard(Flashcard expectedFlashcard1, Flashcard actualFlashcard1) {
        assertEquals(expectedFlashcard1.getFlashcardID(), actualFlashcard1.getFlashcardID());
        assertEquals(expectedFlashcard1.getAccuracy(), actualFlashcard1.getAccuracy(), 0.001);
        assertEquals(expectedFlashcard1.getKanaLength(), actualFlashcard1.getKanaLength());
        assertEquals(expectedFlashcard1.getSpeedPerCharacter(), actualFlashcard1.getSpeedPerCharacter(), 0.001);
        assertEquals(expectedFlashcard1.getLastState(), actualFlashcard1.getLastState());
    }


    @Test
    public void delete2() {
        //Test case 2, (with position1 + position3) /2
        Flashcard expectedFlashcard2 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition2).getWordID())[0];
        expectedFlashcard2.setLastState(99);
        flashcardController.delete(testPosition2);
        Flashcard actualFlashcard2 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition2).getWordID())[0];
        assertEqualsFlashcard(expectedFlashcard2, actualFlashcard2);
    }

    @Test
    public void delete3() {
        //Test case 1, with position 0
        Flashcard expectedFlashcard3 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition3).getWordID())[0];
        expectedFlashcard3.setLastState(99);
        flashcardController.delete(testPosition3);
        Flashcard actualFlashcard3 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition3).getWordID())[0];
        assertEqualsFlashcard(expectedFlashcard3, actualFlashcard3);
    }

    @Test
    public void flashcardInfo() {
    }
}