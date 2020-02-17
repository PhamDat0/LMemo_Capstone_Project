package com.example.lmemo_capstone_project.room_database.data_classes.relationship_classes;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.lmemo_capstone_project.room_database.data_classes.Note;
import com.example.lmemo_capstone_project.room_database.data_classes.NoteOfWord;
import com.example.lmemo_capstone_project.room_database.data_classes.Word;

import java.util.List;

public class WordWithNotes {
    @Embedded private Word word;
    @Relation(
            parentColumn = "WordID",
            entityColumn = "NoteID",
            associateBy = @Junction(NoteOfWord.class)
    )
    private List<Note> notes;

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
