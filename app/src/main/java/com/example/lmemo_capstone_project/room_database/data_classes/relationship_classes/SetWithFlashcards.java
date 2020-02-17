package com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.lmemo_capstone_project.room_database.data_classes.Flashcard;
import com.example.lmemo_capstone_project.room_database.data_classes.FlashcardBelongToSet;
import com.example.lmemo_capstone_project.room_database.data_classes.SetFlashcard;

import java.util.List;

public class SetWithFlashcards {
    @Embedded private SetFlashcard setFlashcard;
    @Relation(
            parentColumn = "SetID",
            entityColumn = "FlashcardID",
            associateBy = @Junction(FlashcardBelongToSet.class)
    )
    private List<Flashcard> flashcards;

    public SetFlashcard getSetFlashcard() {
        return setFlashcard;
    }

    public void setSetFlashcard(SetFlashcard setFlashcard) {
        this.setFlashcard = setFlashcard;
    }

    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }
}
