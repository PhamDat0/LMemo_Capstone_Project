package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.NoteWithWords;

import java.util.List;

@Dao
public interface NoteDAO {
    @Insert()
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("SELECT * FROM Note")
    Note[] loadAllNotes();

    @Query("SELECT * FROM Note WHERE PublicStatus = :publicStatus")
    Note[] loadNotesByStatus(boolean publicStatus);

//    @Query("SELECT DisplayName, NoteContent FROM Note N, User U, Word W WHERE N.UserID = U.UserID AND PublicStatus = 1")
//    Note[] loadNotesAssociateToWord();

    @Query("select * from note order by noteid desc limit 1")
    Note[] getLastNote();
    @Transaction
    @Query("SELECT * FROM Note")
    List<NoteWithWords> getNotesWithWords();
}
