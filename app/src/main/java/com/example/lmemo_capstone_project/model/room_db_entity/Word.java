package com.example.lmemo_capstone_project.model.room_db_entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Word {
    @PrimaryKey
    @ColumnInfo(name = "WordID") private int wordID;

    @ColumnInfo(name = "Kana") private String kana;
    @ColumnInfo(name = "Kanji") private String kanjiWriting;
    @ColumnInfo(name = "Meaning") private String meaning;
    @ColumnInfo(name = "PartOfSpeech") private String partOfSpeech;

    public Word() {
    }

    @Ignore
    public Word(int wordID, String kana, String kanjiWriting, String meaning, String partOfSpeech) {
        this.wordID = wordID;
        this.kana = kana;
        this.kanjiWriting = kanjiWriting;
        this.meaning = meaning;
        this.partOfSpeech = partOfSpeech;
    }

    public int getWordID() {
        return wordID;
    }

    public void setWordID(int wordID) {
        this.wordID = wordID;
    }

    public String getKana() {
        return kana;
    }

    public void setKana(String kana) {
        this.kana = kana;
    }

    public String getKanjiWriting() {
        return kanjiWriting;
    }

    public void setKanjiWriting(String kanjiWriting) {
        this.kanjiWriting = kanjiWriting;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }
}
