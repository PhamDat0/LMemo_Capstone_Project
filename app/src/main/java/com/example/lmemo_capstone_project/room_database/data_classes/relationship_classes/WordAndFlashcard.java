package com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.lmemo_capstone_project.room_database.data_classes.Flashcard;
import com.example.lmemo_capstone_project.room_database.data_classes.Word;

public class WordAndFlashcard {
    @Embedded private Word word;
    @Relation(
            parentColumn = "WordID",
            entityColumn = "FlashcardID"
    )
    private Flashcard flashcard;

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Flashcard getFlashcard() {
        return flashcard;
    }

    public void setFlashcard(Flashcard flashcard) {
        this.flashcard = flashcard;
    }
}
