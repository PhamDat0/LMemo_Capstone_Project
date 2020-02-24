package com.example.lmemo_capstone_project.model.room_db_entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Kanji {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Kanji")
    private String kanji;

    @ColumnInfo(name = "SGVStrokeFile") private String SGVStrokeFile;
    @ColumnInfo(name = "Onyomi") private String onyomi;
    @ColumnInfo(name = "Kunyomi") private String kunyomi;

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public String getSGVStrokeFile() {
        return SGVStrokeFile;
    }

    public void setSGVStrokeFile(String SGVStrokeFile) {
        this.SGVStrokeFile = SGVStrokeFile;
    }

    public String getOnyomi() {
        return onyomi;
    }

    public void setOnyomi(String onyomi) {
        this.onyomi = onyomi;
    }

    public String getKunyomi() {
        return kunyomi;
    }

    public void setKunyomi(String kunyomi) {
        this.kunyomi = kunyomi;
    }
}
