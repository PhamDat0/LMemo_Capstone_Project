package com.example.lmemo_capstone_project.controller.test_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.review_activity.MultipleChoiceTestActivity;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestControllerTest {

    private static final int NUMBER_OF_TEST_NORMAL_CASE = 9;
    private static final int NUMBER_OF_TEST_OUT_OF_FLASHCARD = 9999999;
    private static final int NUMBER_OF_TEST_SMALLER_THAN_0 = -5;
    private TestController testControllerForMultipleChoice;
    private TestController testControllerForWriting;
    private FlashcardDAO flashcardDAO;
    private WordDAO wordDAO;

    @Before
    public void initTestController() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        wordDAO = LMemoDatabase.getInstance(appContext).wordDAO();
        flashcardDAO = LMemoDatabase.getInstance(appContext).flashcardDAO();
        testControllerForMultipleChoice = new TestController(wordDAO, flashcardDAO, TestController.MULTIPLE_CHOICE_MODE);
        testControllerForWriting = new TestController(wordDAO, flashcardDAO, TestController.WRITING_MODE);
    }

    @Test
    public void prepareTestNormalCaseMultipleChoiceTestFor() {
        List<Word> words1 = testControllerForMultipleChoice.prepareTest(NUMBER_OF_TEST_NORMAL_CASE);
        //NUMBER_OF_TEST_NORMAL_CASEのフラッシュカードがあるかどうか確認します。
        assertEquals(words1.size(), NUMBER_OF_TEST_NORMAL_CASE);
        Set<Integer> wordIDs = new HashSet<>();
        //同じフラッシュカードがないかどうか確認します。
        for (Word word : words1) {
            assertFalse(wordIDs.contains(word.getWordID()));
            wordIDs.add(word.getWordID());
        }
    }

    @Test
    public void prepareTestOutOfFlashcardCaseMultipleChoiceFor() {
        List<Word> words1 = testControllerForMultipleChoice.prepareTest(NUMBER_OF_TEST_OUT_OF_FLASHCARD);
        //すべてのフラッシュカードがあるかどうか確認します。
        assertEquals(words1.size(), flashcardDAO.getNumberOfVisibleFlashcards());
        Set<Integer> wordIDs = new HashSet<>();
        //同じフラッシュカードがないかどうか確認します。
        for (Word word : words1) {
            assertFalse(wordIDs.contains(word.getWordID()));
            wordIDs.add(word.getWordID());
        }
    }

    @Test
    public void prepareTestSmallerThanZeroCaseMultipleChoiceFor() {
        List<Word> words1 = testControllerForMultipleChoice.prepareTest(NUMBER_OF_TEST_SMALLER_THAN_0);
        //１つの質問があるかどうか確認します。
        assertEquals(words1.size(), 1);
    }

    @Test
    public void prepareTestNormalCaseWriting() {
        List<Word> words1 = testControllerForWriting.prepareTest(NUMBER_OF_TEST_NORMAL_CASE);
        //NUMBER_OF_TEST_NORMAL_CASEのフラッシュカードがあるかどうか確認します。
        assertEquals(words1.size(), NUMBER_OF_TEST_NORMAL_CASE);
        Set<Integer> wordIDs = new HashSet<>();
        //同じフラッシュカードがないかどうか確認します。
        for (Word word : words1) {
            assertFalse(wordIDs.contains(word.getWordID()));
            wordIDs.add(word.getWordID());
        }
    }

    @Test
    public void prepareTestOutOfFlashcardCaseWriting() {
        List<Word> words1 = testControllerForWriting.prepareTest(NUMBER_OF_TEST_OUT_OF_FLASHCARD);
        //すべてのフラッシュカードがあるかどうか確認します。
        assertEquals(words1.size(), flashcardDAO.getNumberOfVisibleFlashcards());
        Set<Integer> wordIDs = new HashSet<>();
        //同じフラッシュカードがないかどうか確認します。
        for (Word word : words1) {
            assertFalse(wordIDs.contains(word.getWordID()));
            wordIDs.add(word.getWordID());
        }
    }

    @Test
    public void prepareTestSmallerThanZeroCaseWriting() {
        List<Word> words1 = testControllerForWriting.prepareTest(NUMBER_OF_TEST_SMALLER_THAN_0);
        //１つの質問があるかどうか確認します。
        assertEquals(words1.size(), 1);
    }

    @Test
    public void getWordsForMultipleChoiceSelection() {
        Flashcard flashcard = flashcardDAO.getAllVisibleFlashcard()[0];
        Word[] wordsForMultipleChoiceSelection = testControllerForMultipleChoice.getWordsForMultipleChoiceSelection(flashcard.getFlashcardID());
        //４つの答えがあるかどうか確認します。
        assertEquals(wordsForMultipleChoiceSelection.length, 4);
        Set<Integer> wordIDs = new HashSet<>();
        //同じ答えがないかどうか確認します。
        for (Word word : wordsForMultipleChoiceSelection) {
            assertFalse(wordIDs.contains(word.getWordID()));
            wordIDs.add(word.getWordID());
        }
        //正解があるかどうか確認します。
        assertTrue(wordIDs.contains(flashcard.getFlashcardID()));
    }

    @Test
    public void getRandomModeForWordHasKanji() {
        //１から６までできます。
        Word word1 = wordDAO.getWords("%お父さん%")[0];
        int mode1 = testControllerForMultipleChoice.getRandomMode(word1);
        Set<Integer> modes = new HashSet<>();
        modes.add(MultipleChoiceTestActivity.KANJI_KANA);
        modes.add(MultipleChoiceTestActivity.KANJI_MEANING);
        modes.add(MultipleChoiceTestActivity.KANA_KANJI);
        modes.add(MultipleChoiceTestActivity.KANA_MEANING);
        modes.add(MultipleChoiceTestActivity.MEANING_KANA);
        modes.add(MultipleChoiceTestActivity.MEANING_KANJI);
        assertTrue(modes.contains(mode1));
    }

    @Test
    public void getRandomModeForWordDoesNotHaveKanji() {
        //漢字がないので、KANA_MEANINGとMEANING_KANAしかできません。
        Word word2 = wordDAO.getWords("%スケジュール%")[0];
        int mode2 = testControllerForMultipleChoice.getRandomMode(word2);
        Set<Integer> modes2 = new HashSet<>();
        modes2.add(MultipleChoiceTestActivity.KANA_MEANING);
        modes2.add(MultipleChoiceTestActivity.MEANING_KANA);
        assertTrue(modes2.contains(mode2));
    }

}