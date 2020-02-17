package com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.lmemo_capstone_project.room_database.data_classes.Note;
import com.example.lmemo_capstone_project.room_database.data_classes.SetFlashcard;
import com.example.lmemo_capstone_project.room_database.data_classes.User;

import java.util.List;

public class UserWithSets {
    @Embedded
    private User user;
    @Relation(
            parentColumn = "UserID",
            entityColumn = "UserID"
    )
    private List<SetFlashcard> setFlashcards;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<SetFlashcard> getSetFlashcards() {
        return setFlashcards;
    }

    public void setSetFlashcards(List<SetFlashcard> setFlashcards) {
        this.setFlashcards = setFlashcards;
    }
}
