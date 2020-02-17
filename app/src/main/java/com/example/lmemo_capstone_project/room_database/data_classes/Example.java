package com.example.lmemo_capstone_project.room_database.data_classes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Example {
    @PrimaryKey
    @ColumnInfo(name = "ExampleID") private int exampleID;

    @ColumnInfo(name = "ExampleSentence") private String exampleSentence;

    public int getExampleID() {
        return exampleID;
    }

    public void setExampleID(int exampleID) {
        this.exampleID = exampleID;
    }

    public String getExampleSentence() {
        return exampleSentence;
    }

    public void setExampleSentence(String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }
}
