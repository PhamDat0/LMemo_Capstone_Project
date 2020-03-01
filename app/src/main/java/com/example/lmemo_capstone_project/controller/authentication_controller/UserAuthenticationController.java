package com.example.lmemo_capstone_project.controller.authentication_controller;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAuthenticationController {
    private static final String TAG = "LoginActivity";
    private final FirebaseFirestore db;
    private UserDAO userDAO;
    private List<String> listFUID;

    public UserAuthenticationController(Activity activity) {
        db = FirebaseFirestore.getInstance();
        userDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).userDAO();
        listFUID = new ArrayList<>();
    }

    private List<String> getAllDocumentID(final FirebaseUser currentUser) {
        // get all document id from cloud firestore
        com.google.firebase.firestore.Query query = db.collection("users");
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listFUID.add(document.getData().get("userID").toString());
                            }
                            addUserToDatabase(currentUser);
                        }
                    }

                });
        return listFUID;
    }

    private boolean checkUserExisted(String FID) {
        boolean isExisted = true;
        // check if user is existed on cloud firestore, if existed-return true, if user list is empty or not user not exist, return false
        if (listFUID.isEmpty()) {
            isExisted = false;
            Log.w(TAG, "this is empty " + listFUID.size());
        } else {
            for (int i = 0; i < listFUID.size(); i++) {
                if (listFUID.get(i).equals(FID)) {
                    isExisted = true;
                    break;
                } else {
                    isExisted = false;
                }
            }
        }
        return isExisted;
    }

    private void addUserToDatabase(FirebaseUser currentUser) {
        // add user to sqlite at local database and cloud firebase at online database
        String FID = currentUser.getUid();
        boolean isExisted = checkUserExisted(FID);
        if (!isExisted) {
            final User user = createNewUserInSQLite(currentUser);
            addUserToSQLite(user);
            createNewUserOnCloudFireStore(currentUser);
        } else {
            updateUserInSQLite(FID);
        }
        listFUID.clear();

    }

    private void updateUserInSQLite(String FID) {
        // update time when user login if user is existed,
        DocumentReference docRef = db.collection("users").document(FID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                Date date = new Date();
                user.setLoginTime(date);
                user.setGender((Boolean) documentSnapshot.get("isMale"));
                addUserToSQLite(user);
                Log.w(TAG, "Logged in after add to sqlite with updated" + user.getDisplayName() + "at time " + user.getLoginTime() + " gender " + user.isGender());

            }
        });
    }

    private void createNewUserOnCloudFireStore(FirebaseUser currentUser) {
        //create new user and up data to cloud firestore
        String FID = currentUser.getUid();
        String email = currentUser.getEmail();
        final String name = currentUser.getDisplayName();
        Map<String, Object> addUser = new HashMap<>();
        addUser.put("userID", FID);
        addUser.put("isMale", true);
        addUser.put("displayName", name);
        addUser.put("email", email);
        addUser.put("contributionPoint", 0);
        db.collection("users").document(FID).set(addUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.w(TAG, "Logged in with new user " + name);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document");
            }
        });
    }

    private User createNewUserInSQLite(FirebaseUser currentUser) {
        // create new user with personal information from firebase authentication
        Date date = new Date();
        User user = new User();
        String FID = currentUser.getUid();
        String email = currentUser.getEmail();
        if (email == null) {
            email = currentUser.getProviderData().get(0).getEmail();
        }
        String name = currentUser.getDisplayName();
        user.setUserID(FID);
        user.setDisplayName(name);
        user.setGender(true);
        user.setContributionPoint(0);
        user.setEmail(email);
        user.setLoginTime(date);
        return user;
    }

    public void useAppAsGuest() {
        // create guest user in local(SQLite) if user is not login/log out
        Date date = new Date();
        User user = new User();
        String FID = "GUEST";
        String email = "GUEST";
        String name = "GUEST";
        user.setUserID(FID);
        user.setDisplayName(name);
        user.setGender(true);
        user.setContributionPoint(0);
        user.setEmail(email);
        user.setLoginTime(date);
        addUserToSQLite(user);
    }

    private void addUserToSQLite(final User user) {
        // initialize new thread to add to sqlite, because sqlite doesn't allow to run command in Activity
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                userDAO.insertUser(user);
                Log.w(TAG, "local user is: " + userDAO.getLocalUser()[0].getDisplayName());
            }
        });

    }

    public void checkCurrentUser(FirebaseUser currentUser) {
        //check on firebase authentication if user is logging in or not. if not, user use app as guest
        if (currentUser == null) {
            useAppAsGuest();
        }
    }

    public void handlingLogin(FirebaseUser currentUser) {
        // handling login activity when login with google/facebook is successful
        getAllDocumentID(currentUser);
    }
}
