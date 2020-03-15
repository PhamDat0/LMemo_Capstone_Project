package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;

@Dao
public interface NoteOfWordDAO {
    @Insert
    void insertNoteOfWord(NoteOfWord noteOfWord);

    @Delete
    void deleteNoteOfWord(NoteOfWord noteOfWord);

    @Query("Select * from NoteOfWord nw where nw.NoteID = :noteID and nw.WordID = :wordID")
    NoteOfWord[] getNoteOfWord(int noteID, int wordID);
}
