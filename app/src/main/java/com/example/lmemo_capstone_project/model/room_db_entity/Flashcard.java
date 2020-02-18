package com.example.lmemo_capstone_project.model.room_db_entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Flashcard {
    @PrimaryKey
    @ColumnInfo(name = "FlashcardID") private int flashcardID;

    @ColumnInfo(name = "SpeedPerCharacter") private double speedPerCharacter;
    @ColumnInfo(name = "Accuracy") private double accuracy;
    @ColumnInfo(name = "LastState") private int lastState;
    @ColumnInfo(name = "Length") private int kanaLength;

    public int getFlashcardID() {
        return flashcardID;
    }

    public void setFlashcardID(int flashcardID) {
        this.flashcardID = flashcardID;
    }

    public double getSpeedPerCharacter() {
        return speedPerCharacter;
    }

    public void setSpeedPerCharacter(double speedPerCharacter) {
        this.speedPerCharacter = speedPerCharacter;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public int getLastState() {
        return lastState;
    }

    public void setLastState(int lastState) {
        this.lastState = lastState;
    }

    public int getKanaLength() {
        return kanaLength;
    }

    public void setKanaLength(int kanaLength) {
        this.kanaLength = kanaLength;
    }
}
