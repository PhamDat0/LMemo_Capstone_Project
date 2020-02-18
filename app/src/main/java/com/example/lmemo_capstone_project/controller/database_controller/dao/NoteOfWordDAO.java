package com.example.lmemo_capstone_project.controller.database_controller.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;

@Dao
public interface NoteOfWordDAO {
    @Insert
    void insertNoteOfWord(NoteOfWord noteOfWord);

    @Delete
    void deleteNoteOfWord(NoteOfWord noteOfWord);
}
