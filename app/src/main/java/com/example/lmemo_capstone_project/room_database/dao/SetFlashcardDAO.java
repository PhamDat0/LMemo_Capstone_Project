package com.example.lmemo_capstone_project.room_database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.room_database.data_classes.SetFlashcard;
import com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes.FlashcardWithSets;
import com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes.SetWithFlashcards;

import java.util.List;

@Dao
public interface SetFlashcardDAO {
    @Insert
    void insertSetFlashcard(SetFlashcard setFlashcard);

    @Update
    void updateSetFlashcard(SetFlashcard setFlashcard);

    @Delete
    void deleteSetFlashcard(SetFlashcard setFlashcard);

    @Query("SELECT * FROM SetFlashcard")
    SetFlashcard[] getAllSets();

    @Transaction
    @Query("Select * FROM SetFlashcard")
    List<SetWithFlashcards> getSetsWithFlashcards();
}
