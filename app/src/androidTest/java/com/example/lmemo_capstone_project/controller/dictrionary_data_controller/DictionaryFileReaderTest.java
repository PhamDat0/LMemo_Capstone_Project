package com.example.lmemo_capstone_project.controller.dictrionary_data_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DictionaryFileReaderTest {

    @Test
    public void testReadFileToSQLite1() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordDAO wordDAO = LMemoDatabase.getInstance(context).wordDAO();
        Thread reader = new DictionaryFileReader(context);
        reader.start();
        while (reader.isAlive()) {
        }
        Word[] allWords = wordDAO.getAllWords();

        assertEquals(allWords.length, 173747);
    }

    @Test
    public void testReadFileToSQLite2() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordDAO wordDAO = LMemoDatabase.getInstance(context).wordDAO();
        Thread reader = new DictionaryFileReader(context);
        reader.start();
        while (reader.isAlive()) {
        }
        Word word = wordDAO.getWords("お父さん")[0];
        assertEquals(word.getWordID(), 1002590);
        assertEquals(word.getKana(), "おとうさん / おとっさん");
        assertEquals(word.getKanjiWriting(), "お父さん / 御父さん");
        assertEquals(word.getPartOfSpeech(), "noun (common) (futsuumeishi)");
        assertEquals(word.getMeaning(), "father / dad / papa / pa / pop / daddy / dada\n");
    }
}