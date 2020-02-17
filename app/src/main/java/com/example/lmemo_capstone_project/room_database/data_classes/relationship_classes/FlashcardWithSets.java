package com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.lmemo_capstone_project.room_database.data_classes.Flashcard;
import com.example.lmemo_capstone_project.room_database.data_classes.FlashcardBelongToSet;
import com.example.lmemo_capstone_project.room_database.data_classes.SetFlashcard;

import java.util.List;

public class FlashcardWithSets {
    @Embedded private Flashcard flashcard;
    @Relation(
            parentColumn = "FlashcardID",
            entityColumn = "SetID",
            associateBy = @Junction(FlashcardBelongToSet.class)
    )
    private List<SetFlashcard> setFlashcards;

    public Flashcard getFlashcard() {
        return flashcard;
    }

    public void setFlashcard(Flashcard flashcard) {
        this.flashcard = flashcard;
    }

    public List<SetFlashcard> getSetFlashcards() {
        return setFlashcards;
    }

    public void setSetFlashcards(List<SetFlashcard> setFlashcards) {
        this.setFlashcards = setFlashcards;
    }
}
