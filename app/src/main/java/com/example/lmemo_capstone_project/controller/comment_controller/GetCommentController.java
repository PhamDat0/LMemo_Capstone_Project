package com.example.lmemo_capstone_project.controller.comment_controller;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.home_activity.comment_view.CommentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetCommentController {
    private FirebaseFirestore db;
    private CommentActivity commentActivity;
    private ArrayList<Comment> listComment;
    private ListenerRegistration registration;
    private boolean isProcessing;
    private ArrayList<User> listUser;

    public GetCommentController(CommentActivity commentActivity) {
        db = FirebaseFirestore.getInstance();
        this.commentActivity = commentActivity;
        isProcessing = false;
    }

    public void getAllCommentFromFirebase(String noteOnlineID) {
        isProcessing = false;
        stopListening();
        Query query = db.collection("comments").whereEqualTo("noteOnlineID",noteOnlineID).
                orderBy("createdDate", Query.Direction.ASCENDING);
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!isProcessing) {
                    isProcessing = true;
                    listComment = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                        Comment comment = processSnapshot(documentSnapshot);
                        Log.d("LIST_SIZE", documentSnapshot.getId() + " -> " + documentSnapshot.getData() + "\n" + comment.getDownvoters());
                        listComment.add(comment);
                        Log.d("LIST_SIZE", listComment.size() + " inside");
                    }
                    Log.d("LIST_SIZE", listComment.size() + " outside");
                    getUserList();
                }
            }
        });

    }

    private void getUserList() {
        Log.d("myApp", "How many times userlist is call");
        listUser = new ArrayList<>();
        isProcessing = !(listComment.size() == 0);
        if (listComment.size() != 0) {
            for (Comment comment : listComment) {
                String userID = comment.getUserID();
                DocumentReference userRef = db.collection("users").document(userID);
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("myApp", "DocumentSnapshot data: " + document.getData());
                                User user = document.toObject(User.class);
                                Map<String, Object> userMap = document.getData();
                                user.setGender((Boolean) userMap.get("isMale"));
                                listUser.add(user);
                                compareTwoListSize();
                            } else {
                                Log.d("myAppComment", "No such document");
                            }
                        } else {
                            Log.d("myAppComment", "get failed with ", task.getException());
                        }
                    }
                });
            }
        } else {
            compareTwoListSize();
        }
    }

    private void compareTwoListSize() {
        Log.d("compare_size_Comment", listComment.size() + " / " + listUser.size());
        if (listComment.size() == listUser.size()) {
            Map<String, User> listUserMap = new HashMap<>();
            for (User user : listUser) {
                listUserMap.put(user.getUserID(), user);
            }
            isProcessing = false;
            commentActivity.updateUI(listComment, listUserMap);
        }
    }

    private Comment processSnapshot(QueryDocumentSnapshot documentSnapshot) {
        Comment comment = documentSnapshot.toObject(Comment.class);
        Map<String, Object> commentMap = documentSnapshot.getData();
        comment.setUserID((String) commentMap.get("userID"));
        comment.setCommentID(documentSnapshot.getId());
        comment.setNoteOnlineID((String) commentMap.get("userID"));
        comment.setContent((String)commentMap.get("content"));
        comment.setUpvoters((List<String>) commentMap.get("upvoter"));
        comment.setDownvoters((List<String>) commentMap.get("downvoter"));
        return comment;
    }

    public void stopListening() {
        if (registration != null) {
            registration.remove();
        }
    }
}
