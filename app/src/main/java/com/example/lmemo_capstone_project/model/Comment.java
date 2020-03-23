package com.example.lmemo_capstone_project.model;

import java.util.Date;
import java.util.List;

public class Comment {
    private String noteOnlineID, userID, content, commentID;
    private Date createdDate;
    private List<String> upvoters, downvoters;

    public Comment() {
    }

    public String getNoteOnlineID() {
        return noteOnlineID;
    }

    public void setNoteOnlineID(String noteOnlineID) {
        this.noteOnlineID = noteOnlineID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public List<String> getUpvoters() {
        return upvoters;
    }

    public void setUpvoters(List<String> upvoters) {
        this.upvoters = upvoters;
    }

    public List<String> getDownvoters() {
        return downvoters;
    }

    public void setDownvoters(List<String> downvoters) {
        this.downvoters = downvoters;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
}
