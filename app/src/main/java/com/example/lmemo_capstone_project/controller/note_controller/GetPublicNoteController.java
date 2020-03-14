package com.example.lmemo_capstone_project.controller.note_controller;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.NoteUser;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.UserWithNotes;
import com.example.lmemo_capstone_project.view.home_activity.NoteListAdapter;
import com.example.lmemo_capstone_project.view.home_activity.WordSearchingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ArrayTable;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetPublicNoteController {
    private FirebaseFirestore db;
    private WordSearchingFragment wordSearchingFragment;
    private ArrayList<Note> listNote;
    private ArrayList<User> listUser;

    public GetPublicNoteController(WordSearchingFragment wordSearchingFragment) {
        db = FirebaseFirestore.getInstance();
        this.wordSearchingFragment = wordSearchingFragment;
    }

    /**
     * @param listview
     * @param activity
     * @param wordID   　これはノートのwordID
     *                 この関数はファイアベースからノートを取ります
     */
    public void getAllNoteAscendingFromFirebase(final ListView listview, final Activity activity, int wordID) {
        db.collection("notes").whereArrayContains("wordID", wordID).
                orderBy("createdTime", Query.Direction.ASCENDING).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("myApp", "Get here");
                listNote = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Log.d("myApp", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                        Note note = documentSnapshot.toObject(Note.class);
                        Map<String, Object> noteMap = documentSnapshot.getData();
                        note.setCreatorUserID((String) noteMap.get("userID"));
                        listNote.add(note);
                    }
                    getUserList();
                } else {
                    Log.d("MyApp", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void getAllNoteDescendingFromFirebase(final ListView listview, final Activity activity, int wordID) {
        db.collection("notes").whereArrayContains("wordID", wordID).
                orderBy("createdTime", Query.Direction.DESCENDING).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                listNote = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Note note = documentSnapshot.toObject(Note.class);
                        Map<String, Object> noteMap = documentSnapshot.getData();
                        note.setCreatorUserID((String) noteMap.get("userID"));
                        listNote.add(note);
                    }
                    getUserList();
                } else {
                    Log.d("MyApp", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void getUserList() {
        listUser = new ArrayList<>();
        for (Note note : listNote) {
            DocumentReference userRef = db.collection("users").
                    document(note.getCreatorUserID());
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
                            Log.d("myApp", "No such document");
                        }
                    } else {
                        Log.d("myApp", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    private void compareTwoListSize() {
        Log.d("compare_size", listNote.size()+" / "+ listUser.size());
        if(listNote.size()==listUser.size()) {
            Map<String,User> listUserMap = new HashMap<>();
            for(User user: listUser) {
                listUserMap.put(user.getUserID(),user);
            }
            wordSearchingFragment.updateUI(listNote,listUserMap);
        }
    }
}
