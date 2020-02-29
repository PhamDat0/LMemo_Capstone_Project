package com.example.lmemo_capstone_project.controller.database_controller.firebase_dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.exception.DublicatedMailException;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OnlineUserDAO {

    private final FirebaseFirestore db;

    public OnlineUserDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public void updateUser(User user) throws DublicatedMailException {
        if (hasADublicatedEmail(user.getEmail())) {
            throw new DublicatedMailException("This email has been registered.");
        }
        ;

        DocumentReference userRef = db.collection("users").document(user.getUserID());
        userRef.update("email", user.getEmail(),
                "displayName", user.getDisplayName(),
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

    private boolean hasADublicatedEmail(String email) {
        final List<Map> users = new ArrayList<>();
        final Boolean[] complete = new Boolean[1];
        complete[0] = false;
        Task<QuerySnapshot> querySnapshotTask = db.collection("users").whereEqualTo("email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                users.add(document.getData());
                                Log.d("QUERY", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("QUERY", "Error getting documents: ", task.getException());
                        }
                        complete[0] = true;
                    }
                });
        while (!complete[0]) {
        }
        Log.i("QUERY", "" + users.size());
        return users.size() != 0;
    }
}
