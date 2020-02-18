package com.example.lmemo_capstone_project.controller.database_controller.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.lmemo_capstone_project.model.room_db_entity.Example;

@Dao
public interface ExampleDAO {
    @Insert
    void insertExample(Example example);

    @Query("SELECT * FROM Example WHERE ExampleSentence LIKE :processedInput")
    Example[] getExampleSentences(String processedInput);
}