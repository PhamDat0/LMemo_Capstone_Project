package com.example.lmemo_capstone_project.controller.search_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.KanjiDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Kanji;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SearchControllerTest {

    private final String EMPTY = "";
    private final String DOES_NOT_IN_DICT = "寂しくなかったんだけど";
    private final String SEARCH_BY_KANJI = "購入";
    private final String SEARCH_BY_HIRA = "こうにゅう";
    private final String SEARCH_BY_MEANING = "purchase";
    private final String SEARCH_BY_KATA = "アンケート";
    private final int ID_FOR_PURCHASE_WORD = 1282440;
    private final int ID_FOR_ANKETO = 1019940;
    private final String EMPTY_MESSAGE = "You didn't enter anything.";
    private final String NOT_FOUND_MESSAGE = "That word does not exist.";

    private final String SEARCH_DUPLICATED_KANJI1 = "購購";
    private final String SEARCH_DUPLICATED_KANJI2 = "購入購";
    private final String EXPECTED_KANJI1 = "購";
    private final String EXPECTED_KANJI2 = "入";
    private final String EXPECTED_KANJI3 = "寂";

    private Word EXISTED_IN_DB;
    private Word NOT_EXISTED_IN_DB;
    private Word EXISTED_IN_DB_WITH_LAST_STATE_OF_99;

    private SearchController searchControllerForWord;
    private SearchController searchControllerForKanji;

    @Before
    public void initTestController() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LMemoDatabase instance = LMemoDatabase.getInstance(appContext);
        WordDAO wordDAO = instance.wordDAO();
        KanjiDAO kanjiDAO = instance.kanjiDAO();
        FlashcardDAO flashcardDAO = instance.flashcardDAO();
        searchControllerForWord = new SearchController(wordDAO, flashcardDAO);
        searchControllerForKanji = new SearchController(kanjiDAO);

    }

    @Test
    public void performSearch() {
        try {
            searchControllerForWord.performSearch(EMPTY);
            assertEquals(1, 2);
        } catch (Exception e) {
            assertEquals(e.getMessage(), EMPTY_MESSAGE);
        }
        try {
            searchControllerForWord.performSearch(DOES_NOT_IN_DICT);
            assertEquals(1, 2);
        } catch (Exception e) {
            assertEquals(e.getMessage(), NOT_FOUND_MESSAGE);
        }
        try {
            assertEquals(searchControllerForWord.performSearch(SEARCH_BY_KANJI).getWordID(), ID_FOR_PURCHASE_WORD);
            assertEquals(searchControllerForWord.performSearch(SEARCH_BY_HIRA).getWordID(), ID_FOR_PURCHASE_WORD);
            assertEquals(searchControllerForWord.performSearch(SEARCH_BY_MEANING).getWordID(), ID_FOR_PURCHASE_WORD);
            assertEquals(searchControllerForWord.performSearch(SEARCH_BY_KATA).getWordID(), ID_FOR_ANKETO);
        } catch (Exception e) {
            assertEquals(1, 2);
        }
    }

    @Test
    public void addWordToFlashcard() {

    }

    @Test
    public void searchForKanji() {
        List<Kanji> hasResult1 = searchControllerForKanji.searchForKanji(SEARCH_BY_KANJI);
        assertEquals(hasResult1.get(0).getKanji(), EXPECTED_KANJI1);
        assertEquals(hasResult1.get(1).getKanji(), EXPECTED_KANJI2);
        List<Kanji> hasResult2 = searchControllerForKanji.searchForKanji(DOES_NOT_IN_DICT);
        assertEquals(hasResult2.get(0).getKanji(), EXPECTED_KANJI3);
        List<Kanji> hasResult3 = searchControllerForKanji.searchForKanji(SEARCH_DUPLICATED_KANJI1);
        assertEquals(hasResult3.size(), 1);
        assertEquals(hasResult3.get(0).getKanji(), EXPECTED_KANJI1);
        List<Kanji> hasResult4 = searchControllerForKanji.searchForKanji(SEARCH_DUPLICATED_KANJI2);
        assertEquals(hasResult4.size(), 2);
        assertEquals(hasResult4.get(0).getKanji(), EXPECTED_KANJI1);
        assertEquals(hasResult4.get(1).getKanji(), EXPECTED_KANJI2);
        List<Kanji> noResult1 = searchControllerForKanji.searchForKanji(EMPTY);
        assertEquals(noResult1.size(), 0);
        List<Kanji> noResult2 = searchControllerForKanji.searchForKanji(SEARCH_BY_HIRA);
        assertEquals(noResult2.size(), 0);
        List<Kanji> noResult3 = searchControllerForKanji.searchForKanji(SEARCH_BY_KATA);
        assertEquals(noResult3.size(), 0);
        List<Kanji> noResult4 = searchControllerForKanji.searchForKanji(SEARCH_BY_MEANING);
        assertEquals(noResult4.size(), 0);
    }
}