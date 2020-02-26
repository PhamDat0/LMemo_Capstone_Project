package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.WordWithNotes;

import java.util.List;

@Dao
public interface WordDAO {
    @Insert
    void insertWord(Word word);

    @Update
    void updateWord(Word word);

    @Query("DELETE FROM Word")
    void deleteAllWords();

    @Query("SELECT * FROM Word WHERE Kana LIKE '%' || :keyword || '%' OR Kanji LIKE '%' || :keyword || '%' OR Meaning LIKE '%' || :keyword || '%' order by WordID asc")
    Word[] getWords(String keyword);

    @Query("SELECT * FROM Word order by WordID asc")
    Word[] getAllWords();

    @Transaction
    @Query("SELECT * FROM Word")
    List<WordWithNotes> getWordsWithNotes();
}
