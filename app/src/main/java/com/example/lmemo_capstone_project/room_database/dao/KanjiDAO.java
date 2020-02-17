package com.example.lmemo_capstone_project.room_database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.lmemo_capstone_project.room_database.data_classes.Kanji;

@Dao
public interface KanjiDAO {
    @Insert
    void insertKanji(Kanji kanji);

    @Query("SELECT * FROM Kanji WHERE Kanji LIKE :keyword")
    Kanji[] getKanji(char keyword);
}
