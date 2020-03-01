package com.example.lmemo_capstone_project.controller.database_controller.firebase_dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class OnlineUserDAO {

    private final FirebaseFirestore db;

    public OnlineUserDAO() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * @param user The object contains new user's information
     *             ユーザーの新しい情報を持っているオブジェクトです。
     *             この関数はユーザーの新しい情報をFirebaseにアップロードします。
     *             変更可能情報は性別とディスプレイの名前です。
     */
    public void updateUser(final User user) {
        DocumentReference userRef = db.collection("users").document(user.getUserID());
        userRef.update("displayName", user.getDisplayName(),
                "isMale", user.isMale())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase_User_Update", "User successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase_User_Update", "Error updating document", e);
                    }
                });
    }
}
