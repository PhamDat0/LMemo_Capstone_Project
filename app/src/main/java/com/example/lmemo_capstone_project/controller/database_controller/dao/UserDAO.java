package com.example.lmemo_capstone_project.controller.database_controller.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.UserWithNotes;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.UserWithSets;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM User order by UserID asc LIMIT 1")
    User[] getLocalUser();

    @Transaction
    @Query("SELECT * FROM User order by UserID asc LIMIT 1")
    List<UserWithNotes> getUsersWithNotes();

    @Transaction
    @Query("SELECT * FROM User order by UserID asc LIMIT 1")
    List<UserWithSets> getUserWithSets();
}