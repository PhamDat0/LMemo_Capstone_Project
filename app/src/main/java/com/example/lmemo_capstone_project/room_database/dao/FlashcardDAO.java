package com.example.lmemo_capstone_project.room_database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.room_database.data_classes.Flashcard;
import com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes.FlashcardWithSets;

import java.util.List;

@Dao
public interface FlashcardDAO {
    @Insert
    void insertFlashcard(Flashcard flashcard);

    @Update
    void updateFlashcard(Flashcard flashcard);

    @Query("SELECT * FROM Flashcard")
    Flashcard[] getAllFlashcard();

    @Transaction
    @Query("Select * FROM Flashcard")
    List<FlashcardWithSets> getFlashcardsWithSets();
}
