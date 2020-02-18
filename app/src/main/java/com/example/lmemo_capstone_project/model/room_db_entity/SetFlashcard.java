package com.example.lmemo_capstone_project.model.room_db_entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SetFlashcard {
    @PrimaryKey
    @ColumnInfo(name = "SetID") private int setID;

    @ColumnInfo(name = "SetName") private String setName;
    @ColumnInfo(name = "PublicStatus") private boolean isPublic;
    @ColumnInfo(name = "OnlineID") private String onlineID;
    @ColumnInfo(name = "UserID") private String creatorID;

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
}
