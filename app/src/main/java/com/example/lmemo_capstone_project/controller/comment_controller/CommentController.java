package com.example.lmemo_capstone_project.controller.comment_controller;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.controller.my_account_controller.MyAccountController;
import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentController {
    private FirebaseFirestore db;
    private User user;
    private UserDAO userDAO;
    private static final int UPVOTE = 1;
    private static final int DOWNVOTE = 2;

    public CommentController(Context context) {
        db = FirebaseFirestore.getInstance();
        userDAO = LMemoDatabase.getInstance(context).userDAO();
        user = userDAO.getLocalUser()[0];
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
                ProgressDialog.getInstance().dismiss();
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
        commentToAdd.put("upvoter", new ArrayList<String>());
        commentToAdd.put("downvoter", new ArrayList<String>());
        return commentToAdd;
    }

    /**
     * @param comment        元のコメント
     * @param commentContent 更新した内容
     * この関数はコメントを更新します。内容だけ返信します。
     */
    public void editComment(Comment comment, String commentContent) {
        db.collection("comments").document(comment.getCommentID())
                .update("content", commentContent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("COMMENT_CONTROLLER", "Update content successfully");
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    public void deleteCommentFromFB(Comment comment) {
        String commentID = comment.getCommentID();
        Log.w("AddCommentActivity", "OnlineID controller 2" + comment.getCommentID());
        db.collection("comments").document(commentID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditCommentController", "DocumentSnapshot successfully deleted!");
                        MyAccountController myAccountController = new MyAccountController();
                        myAccountController.increaseUserPoint(user.getUserID(), -1);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        comment.setCommentID(null);
    }

    public void upvoteComment(Comment comment){
        if (!comment.getUpvoters().contains(user.getUserID())) {
            int pointForOwner = comment.getUpvoters().contains(user.getUserID()) ? 2 : 1;
            performVoteComment(comment, UPVOTE, pointForOwner);
        }
    }
    public void downvoteComment(Comment comment){
        if (!comment.getDownvoters().contains(user.getUserID())) {
            int pointForOwner = comment.getDownvoters().contains(user.getUserID()) ? -2 : -1;
            performVoteComment(comment, DOWNVOTE, pointForOwner);
        }
    }
    private void performVoteComment(final Comment comment, final int mode, final int pointForOwner){
        if (!user.getUserID().equalsIgnoreCase("GUEST")) {
            final DocumentReference docRef = db.collection("comments").document(comment.getCommentID());
            docRef.update("upvoter", FieldValue.arrayRemove(user.getUserID()),
                    "downvoter", FieldValue.arrayRemove(user.getUserID()),
                    mode == UPVOTE ? "upvoter" : "downvoter",
                    FieldValue.arrayUnion(user.getUserID())).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("Vote_success", "DELETE FROM LIST");
                    new MyAccountController().increaseUserPoint(comment.getUserID(), pointForOwner);
                    Log.i("Vote_success", mode == UPVOTE ? "upvote" : "downvote" + " on " + comment.getCommentID() + " by " + user.getUserID());
                    ProgressDialog.getInstance().dismiss();
                }
            });
        }

    }
}
