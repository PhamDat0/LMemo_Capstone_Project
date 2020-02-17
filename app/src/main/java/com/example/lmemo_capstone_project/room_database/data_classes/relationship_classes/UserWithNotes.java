package com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.lmemo_capstone_project.room_database.data_classes.Note;
import com.example.lmemo_capstone_project.room_database.data_classes.User;

import java.util.List;

public class UserWithNotes {
    @Embedded private User user;
    @Relation(
            parentColumn = "UserID",
            entityColumn = "UserID"
    )
    private List<Note> notes;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
