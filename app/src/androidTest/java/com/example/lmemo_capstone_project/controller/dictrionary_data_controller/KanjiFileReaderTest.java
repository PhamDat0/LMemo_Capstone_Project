package com.example.lmemo_capstone_project.controller.dictrionary_data_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.dao.KanjiDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Kanji;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KanjiFileReaderTest {

    @Test
    public void testReadFileToSQLite1() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        KanjiDAO kanjiDAO = LMemoDatabase.getInstance(context).kanjiDAO();
        Thread reader = new KanjiFileReader(context);
        reader.start();
        while (reader.isAlive()) {
        }
        Kanji[] allWords = kanjiDAO.getKanji("%");

        assertEquals(allWords.length, 13108);
    }

    @Test
    public void testReadFileToSQLite2() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        KanjiDAO kanjiDAO = LMemoDatabase.getInstance(context).kanjiDAO();
        Thread reader = new KanjiFileReader(context);
        reader.start();
        while (reader.isAlive()) {
        }
        Kanji kanji = kanjiDAO.getKanji("亜")[0];

        assertEquals(kanji.getKanji(), "亜");
        assertEquals(kanji.getOnyomi(), "ア");
        assertEquals(kanji.getKunyomi(), "つ.ぐ");

        kanji = kanjiDAO.getKanji("唖")[0];
        assertEquals(kanji.getKanji(), "唖");
        assertEquals(kanji.getOnyomi(), "ア / アク");
        assertEquals(kanji.getKunyomi(), "おし");
    }

}