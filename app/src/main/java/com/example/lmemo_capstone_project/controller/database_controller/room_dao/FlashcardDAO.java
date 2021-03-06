package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.FlashcardWithSets;

import java.util.List;

@Dao
public interface FlashcardDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFlashcard(Flashcard flashcard);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllFlashcard(List<Flashcard> flashcards);

    @Update
    void updateFlashcard(Flashcard flashcard);

    @Query("SELECT * FROM Flashcard WHERE LastState <> 99")
    Flashcard[] getAllVisibleFlashcard();

    @Query("SELECT * FROM Flashcard Where FlashcardID LIKE :id")
    Flashcard[] getFlashCardByID(int id);

    @Transaction
    @Query("Select * FROM Flashcard")
    List<FlashcardWithSets> getFlashcardsWithSets();

    @Query("SELECT COUNT(*) FROM Flashcard WHERE LastState <> 99")
    int getNumberOfVisibleFlashcards();

    @Query("DELETE FROM Flashcard WHERE FlashcardID=:id_of_not_existed")
    void deleteUsingID(int id_of_not_existed);

    @Query("UPDATE Flashcard SET LastState = 99 WHERE FlashcardID=:id_of_existed_with_last_state_of_99")
    void changeLastStateTo99(int id_of_existed_with_last_state_of_99);
}
