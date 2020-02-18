package com.example.lmemo_capstone_project.model.room_db_entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"NoteID","WordID"})
public class NoteOfWord {
    @ColumnInfo(name = "NoteID") private int noteID;
    @ColumnInfo(name = "WordID") private int wordID;

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public int getWordID() {
        return wordID;
    }

    public void setWordID(int wordID) {
        this.wordID = wordID;
    }
}
