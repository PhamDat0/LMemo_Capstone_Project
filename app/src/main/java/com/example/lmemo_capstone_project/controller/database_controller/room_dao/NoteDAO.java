package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.NoteWithWords;

import java.util.List;

@Dao
public interface NoteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("SELECT * FROM Note")
    Note[] loadAllNotes();

//    @Query("SELECT DisplayName, NoteContent FROM Note N, User U, Word W WHERE N.UserID = U.UserID AND PublicStatus = 1")
//    Note[] loadNotesAssociateToWord();

    @Query("select * from note order by noteid desc limit 1")
    Note[] getLastNote();
    @Transaction
    @Query("SELECT * FROM Note")
    List<NoteWithWords> getNotesWithWords();

    @Query("SELECT * FROM Note WHERE PublicStatus = :mode AND UserID = :userID")
    Note[] getNotesOfUser(boolean mode, String userID);

    @Query("SELECT * FROM Note WHERE OnlineID = :onlineID")
    Note[] getNotesByOnlineID(String onlineID);

    @Query("SELECT * FROM Note WHERE NoteID = :noteID")
    Note[] getNotesByID(int noteID);

    @Query("SELECT * FROM Note WHERE PublicStatus = 1 AND UserID = :userID")
    Note[] getUserOnlineNoteOnDevices(String userID);

    @Query("SELECT * FROM Note WHERE PublicStatus = 0 AND UserID = :userID")
    Note[] getUserOfflineNoteOnDevices(String userID);
}
