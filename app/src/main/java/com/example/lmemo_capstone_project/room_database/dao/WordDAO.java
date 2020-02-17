package com.example.lmemo_capstone_project.room_database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.room_database.data_classes.Word;
import com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes.WordWithNotes;

import java.util.List;

@Dao
public interface WordDAO {
    @Insert
    void insertWord(Word word);

    @Update
    void updateWord(Word word);

    @Query("SELECT * FROM Word WHERE Kana LIKE :keyword OR Kanji LIKE :keyword OR Meaning LIKE :keyword")
    Word[] getWords(String keyword);

    @Transaction
    @Query("SELECT * FROM Word")
    List<WordWithNotes> getWordsWithNotes();
}
