package com.example.lmemo_capstone_project.model.room_db_entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class User implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "UserID")
    @NonNull
    private String userID = "";

    @ColumnInfo(name = "Gender")
    private boolean gender;
    @ColumnInfo(name = "Email")
    private String email;
    @ColumnInfo(name = "DisplayName")
    private String displayName;
    @ColumnInfo(name = "ContributionPoint")
    private int contributionPoint;
    @ColumnInfo(name = "LoginTime")
    private Date loginTime;

    @Ignore
    public User(String userID, String email, String displayName, boolean isMale, int contributionPoint, Date loginTime) {
        this.userID = userID;
        this.email = email;
        this.displayName = displayName;
        this.gender = isMale;
        this.contributionPoint = contributionPoint;
        this.loginTime = loginTime;
    }

    @NonNull

    public String getUserID() {
        return userID;
    }

    public void setUserID(@NonNull String userID) {
        this.userID = userID;
    }

    public User() {

    }

    public boolean isGender() {
        return gender;
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

    public void setGender(boolean isMale) {
        this.gender = isMale;
    }

    public void setContributionPoint(int contributionPoint) {
        this.contributionPoint = contributionPoint;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    @Ignore
    public boolean isGuest() {
        return this.userID.equalsIgnoreCase("GUEST");
    }
}
