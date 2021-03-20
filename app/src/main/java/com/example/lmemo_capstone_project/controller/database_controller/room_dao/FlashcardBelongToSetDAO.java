package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.lmemo_capstone_project.model.room_db_entity.FlashcardBelongToSet;

import java.util.List;

@Dao
public interface FlashcardBelongToSetDAO {
    @Insert
    void insertFlashcardBelongToSet(FlashcardBelongToSet flashcardBelongToSet);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllFlashcardBelongToSet(List<FlashcardBelongToSet> flashcardBelongToSets);

    @Delete
    void deleteFlashcardBelongToSet(FlashcardBelongToSet flashcardBelongToSet);

    @Query("SELECT * FROM FlashcardBelongToSet WHERE SetID=:setID")
    FlashcardBelongToSet[] getFlashcardBySetID(int setID);

    @Query("DELETE FROM FlashcardBelongToSet WHERE SetID=:setID")
    void deleteAllAssociationWithSet(int setID);

    @Query("SELECT FlashcardID FROM FlashcardBelongToSet WHERE SetID=:setID")
    List<Long> getFlashcardsBySetID(int setID);
}
