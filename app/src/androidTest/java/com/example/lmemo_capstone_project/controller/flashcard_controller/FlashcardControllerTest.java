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
        //テストケース：１、ポジション：０
        Flashcard expectedFlashcard1 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition1).getWordID())[0];
        expectedFlashcard1.setLastState(99);
        flashcardController.delete(testPosition1);
        Flashcard actualFlashcard1 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition1).getWordID())[0];
        assertEqualsFlashcard(expectedFlashcard1, actualFlashcard1);
    }

    @Test
    public void delete2() {
        //Test case 2, (with position1 + position3) /2
        //テストケース：２、ポジション＝（ポジション１＋ポジション３）／２
        Flashcard expectedFlashcard2 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition2).getWordID())[0];
        expectedFlashcard2.setLastState(99);
        flashcardController.delete(testPosition2);
        Flashcard actualFlashcard2 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition2).getWordID())[0];
        assertEqualsFlashcard(expectedFlashcard2, actualFlashcard2);
    }

    @Test
    public void delete3() {
        //Test case 3, with last position
        //テストケース：１、ポジション：最後のポジション
        Flashcard expectedFlashcard3 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition3).getWordID())[0];
        expectedFlashcard3.setLastState(99);
        flashcardController.delete(testPosition3);
        Flashcard actualFlashcard3 = flashcardDAO.getFlashCardByID(listFlashcard.get(testPosition3).getWordID())[0];
        assertEqualsFlashcard(expectedFlashcard3, actualFlashcard3);
    }

    /**
     * @param expectedFlashcard これはフラッシュカードを予想されます。
     * @param actualFlashcard　これは実際のフラッシュカードです。
     * 二つのフラッシュカードを比較します。
     */
    private void assertEqualsFlashcard(Flashcard expectedFlashcard, Flashcard actualFlashcard) {
        assertEquals(expectedFlashcard.getFlashcardID(), actualFlashcard.getFlashcardID());
        assertEquals(expectedFlashcard.getAccuracy(), actualFlashcard.getAccuracy(), 0.001);
        assertEquals(expectedFlashcard.getKanaLength(), actualFlashcard.getKanaLength());
        assertEquals(expectedFlashcard.getSpeedPerCharacter(), actualFlashcard.getSpeedPerCharacter(), 0.001);
        assertEquals(expectedFlashcard.getLastState(), actualFlashcard.getLastState());
    }

    @Test
    public void flashcardInfo1() {
        //テストケース：１、ポジション：０
        Word expectedFlashcardDetail = wordDAO.getAWords(listFlashcard.get(testPosition1).getWordID())[0];
        Word actualFlashcardDetail = listFlashcard.get(testPosition1);
        assertEqualsFlashcardDetail(expectedFlashcardDetail,actualFlashcardDetail);
    }

    @Test
    public void flashcardInfo2() {
        //テストケース：２、ポジション＝（ポジション１＋ポジション３）／２
        Word expectedFlashcardDetail = wordDAO.getAWords(listFlashcard.get(testPosition2).getWordID())[0];
        Word actualFlashcardDetail = listFlashcard.get(testPosition2);
        assertEqualsFlashcardDetail(expectedFlashcardDetail,actualFlashcardDetail);
    }

    @Test
    public void flashcardInfo3() {
        //テストケース：１、ポジション：最後のポジション
        Word expectedFlashcardDetail = wordDAO.getAWords(listFlashcard.get(testPosition3).getWordID())[0];
        Word actualFlashcardDetail = listFlashcard.get(testPosition3);
        assertEqualsFlashcardDetail(expectedFlashcardDetail,actualFlashcardDetail);
    }


    /**
     * @param expectedFlashcardDetail これはフラッシュカードのインフォメーションを予想されます。
     * @param actualFlashcardDetail　これは実際のフラッシュカードのインフォメーションです。
     * 二つのフラッシュカードのンフォメーションを比較します。
     */
    private void assertEqualsFlashcardDetail(Word expectedFlashcardDetail, Word actualFlashcardDetail) {
        assertEquals(expectedFlashcardDetail.getKana(), actualFlashcardDetail.getKana());
        assertEquals(expectedFlashcardDetail.getKanjiWriting(), actualFlashcardDetail.getKanjiWriting());
        assertEquals(expectedFlashcardDetail.getMeaning(), actualFlashcardDetail.getMeaning());
        assertEquals(expectedFlashcardDetail.getPartOfSpeech(), actualFlashcardDetail.getPartOfSpeech());
    }
}