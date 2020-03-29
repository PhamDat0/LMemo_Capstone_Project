package com.example.lmemo_capstone_project.model.room_db_entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity
public class SetFlashcard implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "SetID") private int setID;

    @ColumnInfo(name = "SetName") private String setName;
    @ColumnInfo(name = "PublicStatus") private boolean isPublic;
    @ColumnInfo(name = "OnlineID") private String onlineID;
    @ColumnInfo(name = "UserID") private String creatorID;

    @Ignore
    private List<Long> wordID;

    @Ignore
    private User creator;

    public int getSetID() {
        return setID;
    }

    public void setSetID(int setID) {
        this.setID = setID;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
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

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public List<Long> getWordID() {
        return wordID;
    }

    public void setWordID(List<Long> wordID) {
        this.wordID = wordID;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public boolean isCreatorAssigned() {
        return creator != null;
    }
}
