package com.example.lmemo_capstone_project.controller.dictrionary_data_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.dao.WordDAO;
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
        Word[] allWords = wordDAO.getAllWords();

        assertEquals(allWords.length, 4);
    }

    @Test
    public void testReadFileToSQLite2() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WordDAO wordDAO = LMemoDatabase.getInstance(context).wordDAO();
        Thread reader = new DictionaryFileReader(context);
        reader.start();
        Word word = wordDAO.getWords("お父さん")[0];
        assertEquals(word.getWordID(), 1002590);
        assertEquals(word.getKana(), " / おとうさん / おとっさん");
    }
}