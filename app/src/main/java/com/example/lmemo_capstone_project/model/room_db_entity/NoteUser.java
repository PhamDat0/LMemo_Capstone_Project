package com.example.lmemo_capstone_project.model.room_db_entity;

import java.util.Date;

public class NoteUser {
    private int noteID;
    private String noteContent;
    private String translatedContent;
    private boolean isPublic;
    private String onlineID;
    private Date createdDate;
    private String creatorUserID;

    private String userID;
    private boolean gender;
    private String email;
    private String displayName;
    private int contributionPoint;
    private Date loginTime;

    public NoteUser(String userID, String email, String displayName, boolean isMale, int contributionPoint, Date loginTime,
                    String noteContent,String translatedContent,String creatorUserID) {
        this.userID = userID;
        this.email = email;
        this.displayName = displayName;
        this.gender = isMale;
        this.contributionPoint = contributionPoint;
        this.loginTime = loginTime;
        this.noteContent = noteContent;
        this.creatorUserID = creatorUserID;
        this.translatedContent = translatedContent;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

}
