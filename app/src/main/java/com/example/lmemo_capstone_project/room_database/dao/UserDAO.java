package com.example.lmemo_capstone_project.room_database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.room_database.data_classes.User;
import com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes.UserWithNotes;
import com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes.UserWithSets;

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
