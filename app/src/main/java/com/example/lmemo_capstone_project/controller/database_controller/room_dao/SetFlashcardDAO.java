package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.SetWithFlashcards;

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

    @Query("SELECT * FROM SetFlashcard ORDER BY SetID DESC LIMIT 1")
    SetFlashcard[] getLastSet();

    @Query("SELECT * FROM SetFlashcard WHERE OnlineID=:onlineID")
    SetFlashcard[] getSetWithOnlineID(String onlineID);

    @Query("SELECT * FROM SetFlashcard WHERE UserID=:userID")
    List<SetFlashcard> getOwnerFlashcard(String userID);

    @Query("SELECT * FROM SetFlashcard WHERE PublicStatus = 1 AND UserID = :userID ")
    SetFlashcard[] getUserOnlineSetOnDevice(String userID);

    @Query("SELECT * FROM SetFlashcard WHERE PublicStatus = 0 AND UserID = :userID ")
    SetFlashcard[] getUserOfflineSetOnDevice(String userID);
}
