package com.example.lmemo_capstone_project.controller.my_account_controller;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.home_activity.account_view.MyAccountFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MyAccountController {

    private final FirebaseFirestore db;

    public MyAccountController() {
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
                "isMale", user.isGender(),
                "contributionPoint", user.getContributionPoint())
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

    public void updateSQLWithOnlineInfoForViewInfo(final User user, final UserDAO userDAO, final View container, final MyAccountFragment myAccountFragment) {
        DocumentReference userRef = db.collection("users").document(user.getUserID());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("myApp", "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> userMap = document.getData();
                        long contributionPoint = (Long) userMap.get("contributionPoint");
                        user.setContributionPoint((int) contributionPoint);
                        user.setGender((Boolean) userMap.get("isMale"));
                        user.setDisplayName((String) userMap.get("displayName"));
                        userDAO.updateUser(user);
                        myAccountFragment.updateUI(container);
                    } else {
                        Log.d("myApp", "No such document");
                    }
                } else {
                    Log.d("myApp", "get failed with ", task.getException());
                }
            }
        });
    }
}
