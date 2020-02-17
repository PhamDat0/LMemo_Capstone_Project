package com.example.lmemo_capstone_project.room_database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.lmemo_capstone_project.room_database.data_classes.Example;

@Dao
public interface ExampleDAO {
    @Insert
    void insertExample(Example example);

    @Query("SELECT * FROM Example WHERE ExampleSentence LIKE :processedInput")
    Example[] getExampleSentences(String processedInput);
}