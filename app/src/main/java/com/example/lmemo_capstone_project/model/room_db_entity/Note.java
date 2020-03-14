package com.example.lmemo_capstone_project.model.room_db_entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Note implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "NoteID") private int noteID;

    @ColumnInfo(name = "NoteContent") private String noteContent;
    @ColumnInfo(name = "TranslatedContent") private String translatedContent;
    @ColumnInfo(name = "PublicStatus") private boolean isPublic;
    @ColumnInfo(name = "OnlineID") private String onlineID;
    @ColumnInfo(name = "CreatedTime") private Date createdDate;
    @ColumnInfo(name = "UserID") private String creatorUserID;

    @Ignore
    private User creator;

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getTranslatedContent() {
        return translatedContent;
    }

    public void setTranslatedContent(String translatedContent) {
        this.translatedContent = translatedContent;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getOnlineID() {
        return onlineID;
    }

    public void setOnlineID(String onlineID) {
        this.onlineID = onlineID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatorUserID() {
        return creatorUserID;
    }

    public void setCreatorUserID(String creatorUserID) {
        this.creatorUserID = creatorUserID;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
