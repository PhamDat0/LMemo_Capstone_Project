package com.example.lmemo_capstone_project.model.room_db_entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    @ColumnInfo(name = "UserID")
    @NonNull
    private String userID = "";

    @ColumnInfo(name = "Gender") private boolean isMale;
    @ColumnInfo(name = "Email") private String email;
    @ColumnInfo(name = "DisplayName") private String displayName;
    @ColumnInfo(name = "ContributionPoint") private int contributionPoint;

    @NonNull
    public String getUserID() {
        return userID;
    }

    public void setUserID(@NonNull String userID) {
        this.userID = userID;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        this.isMale = male;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getContributionPoint() {
        return contributionPoint;
    }

    public void setContributionPoint(int contributionPoint) {
        this.contributionPoint = contributionPoint;
    }

    @Ignore
    public boolean isGuest() {
        return this.userID.equalsIgnoreCase("GUEST");
    }
}
