package com.example.lmemo_capstone_project.controller.search_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.KanjiDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Kanji;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
    private final int ID_OF_EXISTED = 1282440;
    private final int ID_OF_NOT_EXISTED = 2455780;
    private final int ID_OF_EXISTED_WITH_LAST_STATE_OF_99 = 1012280;
    private final double DEFAULT_STATS_ACCURACY = 0;
    private final double DEFAULT_STATS_SPEED = 10;
    private final int DEFAULT_STATS_STATE = 1;

    private SearchController searchControllerForWord;
    private SearchController searchControllerForKanji;
    private WordDAO wordDAO;
    private KanjiDAO kanjiDAO;
    private FlashcardDAO flashcardDAO;

    @Before
    public void initTestController() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LMemoDatabase instance = LMemoDatabase.getInstance(appContext);
        wordDAO = instance.wordDAO();
        kanjiDAO = instance.kanjiDAO();
        flashcardDAO = instance.flashcardDAO();
        searchControllerForWord = new SearchController(wordDAO, flashcardDAO);
        searchControllerForKanji = new SearchController(kanjiDAO);
        /*
        Pre-condition: This word is not existed in db as a flashcard
        <entry xml:id="a1282440">
            <form type="k_ele">
              <orth>購入</orth>
              <usg type="pri">ichi1</usg>
              <usg type="pri">news1</usg>
              <usg type="pri">nf02</usg>
            </form>
            <form type="r_ele">
              <orth>こうにゅう</orth>
              <usg type="re_pri">ichi1</usg>
              <usg type="re_pri">news1</usg>
              <usg type="re_pri">nf02</usg>
            </form>
            <sense>
              <note type="pos">noun (common) (futsuumeishi)</note>
              <note type="pos">noun or participle which takes the aux. verb suru</note>
              <cit type="trans">
                <quote>purchase</quote>
              </cit>
              <cit type="trans">
                <quote>buy</quote>
              </cit>
            </sense>
        </entry>
        */
        EXISTED_IN_DB = wordDAO.getWordWithID(ID_OF_EXISTED)[0];
        /*
         * Pre-condition: This word is not existed in db as a flashcard
         * <entry xml:id="a2455780">
         *   <form type="r_ele">
         *       <orth>アストロラーベ</orth>
         *   </form>
         *   <sense>
         *       <note type="pos">noun (common) (futsuumeishi)</note>
         *       <cit type="trans">
         *           <quote>astrolabe</quote>
         *       </cit>
         *   </sense>
         * </entry>
         */
        NOT_EXISTED_IN_DB = wordDAO.getWordWithID(ID_OF_NOT_EXISTED)[0];
        /*
        Pre-condition: this word has last-state of 99
        <entry xml:id="a1012280">
            <form type="k_ele">
              <orth>見す見す</orth>
              <usg type="pri">ichi1</usg>
            </form>
            <form type="r_ele">
              <orth>みすみす</orth>
              <usg type="re_pri">ichi1</usg>
            </form>
            <sense>
              <note type="pos">adverb (fukushi)</note>
              <note>word usually written using kana alone</note>
              <cit type="trans">
                <quote>before one's own eyes</quote>
              </cit>
              <cit type="trans">
                <quote>from under one's very nose</quote>
              </cit>
            </sense>
        </entry>
        */
        EXISTED_IN_DB_WITH_LAST_STATE_OF_99 = wordDAO.getWordWithID(ID_OF_EXISTED_WITH_LAST_STATE_OF_99)[0];

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
        //For existed, nothing changes
        Flashcard[] before1 = flashcardDAO.getFlashCardByID(ID_OF_EXISTED);
        searchControllerForWord.addWordToFlashcard(EXISTED_IN_DB);
        Flashcard[] after1 = flashcardDAO.getFlashCardByID(ID_OF_EXISTED);
        assertBeforeAndAfterEquals(before1, after1);

        //For not existed, add to flashcard with default stats
        flashcardDAO.deleteUsingID(ID_OF_NOT_EXISTED);
        Flashcard[] before2 = flashcardDAO.getFlashCardByID(ID_OF_NOT_EXISTED);
        //if this assert failed, consider to delete it from DB
        assertEquals(before2.length, 0);
        searchControllerForWord.addWordToFlashcard(NOT_EXISTED_IN_DB);
        Flashcard[] after2 = flashcardDAO.getFlashCardByID(ID_OF_NOT_EXISTED);
        assertDefaultFlashcard(after2);

        //For existed, state 99, change state to 1
        flashcardDAO.changeLastStateTo99(ID_OF_EXISTED_WITH_LAST_STATE_OF_99);
        Flashcard[] before3 = flashcardDAO.getFlashCardByID(ID_OF_EXISTED_WITH_LAST_STATE_OF_99);
        searchControllerForWord.addWordToFlashcard(EXISTED_IN_DB_WITH_LAST_STATE_OF_99);
        Flashcard[] after3 = flashcardDAO.getFlashCardByID(ID_OF_EXISTED_WITH_LAST_STATE_OF_99);
        assertLastStateTo1(before3, after3);
    }

    private void assertDefaultFlashcard(Flashcard[] after2) {
        Flashcard[] expected = new Flashcard[1];
        try {
            expected[0] = new Flashcard();
            expected[0].setFlashcardID(after2[0].getFlashcardID());
            expected[0].setKanaLength("アストロラーベ".length());
            expected[0].setSpeedPerCharacter(DEFAULT_STATS_SPEED);
            expected[0].setAccuracy(DEFAULT_STATS_ACCURACY);
            expected[0].setLastState(DEFAULT_STATS_STATE);
        } catch (Exception e) {
            fail();
        }
        assertBeforeAndAfterEquals(after2, expected);
    }

    private void assertBeforeAndAfterEquals(Flashcard[] before, Flashcard[] after) {
        assertBeforeAndAfterWithoutLaststate(before, after);
        Flashcard bf = before[0];
        Flashcard at = after[0];
        assertEquals(bf.getLastState(), at.getLastState());
    }

    private void assertLastStateTo1(Flashcard[] before, Flashcard[] after) {
        assertBeforeAndAfterWithoutLaststate(before, after);
        Flashcard bf = before[0];
        Flashcard at = after[0];
        //if this assertion failed, consider to change the last state to 99
        assertEquals(99, bf.getLastState());
        assertEquals(1, at.getLastState());
    }

    private void assertBeforeAndAfterWithoutLaststate(Flashcard[] before, Flashcard[] after) {
        //if this assert failed, consider add the word with ID: ID_OF_EXISTED to flashcard
        assertEquals(before.length, 1);
        assertEquals(after.length, 1);
        Flashcard bf = before[0];
        Flashcard at = after[0];
        assertEquals(bf.getFlashcardID(), at.getFlashcardID());
        assertEquals(bf.getKanaLength(), at.getKanaLength());
        assertEquals(bf.getSpeedPerCharacter(), at.getSpeedPerCharacter(), 0);
        assertEquals(bf.getAccuracy(), at.getAccuracy(), 0);
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