package com.example.lmemo_capstone_project.controller.database_controller.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.lmemo_capstone_project.model.room_db_entity.Kanji;

@Dao
public interface KanjiDAO {
    @Insert
    void insertKanji(Kanji kanji);

    @Query("SELECT * FROM Kanji WHERE Kanji LIKE :keyword")
    Kanji[] getKanji(char keyword);
}
