package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;

@Dao
public interface NoteOfWordDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNoteOfWord(NoteOfWord noteOfWord);

    @Delete
    void deleteNoteOfWord(NoteOfWord noteOfWord);

    @Query("Select * from NoteOfWord nw where nw.NoteID = :noteID")
    NoteOfWord[] getNoteOfWord(int noteID);

    @Query("DELETE from NoteOfWord where NoteID = :noteID")
    void deleteAllAssociationOfOneNote(int noteID);
}
