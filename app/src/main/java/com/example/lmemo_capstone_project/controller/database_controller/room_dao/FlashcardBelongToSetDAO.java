package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.example.lmemo_capstone_project.model.room_db_entity.FlashcardBelongToSet;

@Dao
public interface FlashcardBelongToSetDAO {
    @Insert
    void insertFlashcardBelongToSet(FlashcardBelongToSet flashcardBelongToSet);

    @Delete
    void deleteFlashcardBelongToSet(FlashcardBelongToSet flashcardBelongToSet);
}
