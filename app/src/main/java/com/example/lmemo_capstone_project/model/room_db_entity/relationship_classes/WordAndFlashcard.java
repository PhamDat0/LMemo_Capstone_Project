package com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

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
