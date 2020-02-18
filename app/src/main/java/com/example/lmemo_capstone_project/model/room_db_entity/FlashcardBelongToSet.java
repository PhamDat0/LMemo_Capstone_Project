package com.example.lmemo_capstone_project.model.room_db_entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"FlashcardID","SetID"})
public class FlashcardBelongToSet {
    @ColumnInfo(name = "FlashcardID") private int flashcardID;
    @ColumnInfo(name = "SetID") private int setID;

    public int getFlashcardID() {
        return flashcardID;
    }

    public void setFlashcardID(int flashcardID) {
        this.flashcardID = flashcardID;
    }

    public int getSetID() {
        return setID;
    }

    public void setSetID(int setID) {
        this.setID = setID;
    }
}
