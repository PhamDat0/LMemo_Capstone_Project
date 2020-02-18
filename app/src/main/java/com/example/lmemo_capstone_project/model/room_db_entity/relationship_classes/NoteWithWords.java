package com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import java.util.List;

public class NoteWithWords {
    @Embedded private Note note;
    @Relation(
            parentColumn = "NoteID",
            entityColumn = "WordID",
            associateBy = @Junction(NoteOfWord.class)
    )
    private List<Word> words;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }
}
