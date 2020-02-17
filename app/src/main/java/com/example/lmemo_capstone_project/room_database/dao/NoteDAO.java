package com.example.lmemo_capstone_project.room_database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.room_database.data_classes.Note;
import com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes.NoteWithWords;

import java.util.List;

@Dao
public interface NoteDAO {
    @Insert
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("SELECT * FROM Note")
    Note[] loadAllNotes();

    @Query("SELECT * FROM Note WHERE PublicStatus = :publicStatus")
    Note[] loadNotesByStatus(boolean publicStatus);

    @Transaction
    @Query("SELECT * FROM Note")
    List<NoteWithWords> getNotesWithWords();
}
