package com.example.lmemo_capstone_project.controller.comment_controller;

import android.util.Log;

import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentController {
    private FirebaseFirestore db;

    public CommentController() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * @param note           このコメントを持つノート
     * @param currentUser    コメントしたユーザー
     * @param commentContent コメントの内容
     *                       この関数はコメントをFirebaseにアップロードします。
     */
    public void addComment(Note note, User currentUser, String commentContent) {
        Map<String, Object> commentToAdd = getInfoToMap(note, currentUser, commentContent);
        db.collection("comments").add(commentToAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("COMMENT_CONTROLLER", "Add successfully");
            }
        });
    }

    /**
     * @param note           このコメントを持つノート
     * @param currentUser    コメントしたユーザー
     * @param commentContent コメントの内容
     * @return コメントの情報を持つマップのオブジェクト
     */
    private Map<String, Object> getInfoToMap(Note note, User currentUser, String commentContent) {
        Map<String, Object> commentToAdd = new HashMap<>();
        commentToAdd.put("noteOnlineID", note.getOnlineID());
        commentToAdd.put("userID", currentUser.getUserID());
        commentToAdd.put("createdDate", new Date());
        commentToAdd.put("content", commentContent);
        commentToAdd.put("upvoters", new ArrayList<String>());
        commentToAdd.put("downvoters", new ArrayList<String>());
        return commentToAdd;
    }

    /**
     * @param comment        元のコメント
     * @param commentContent 更新した内容
     */
    public void editComment(Comment comment, String commentContent) {
        db.collection("comments").document(comment.getCommentID())
                .update("content", commentContent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("COMMENT_CONTROLLER", "Update content successfully");
            }
        });
    }
}
