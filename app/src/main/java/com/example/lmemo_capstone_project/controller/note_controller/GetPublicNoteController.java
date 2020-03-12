package com.example.lmemo_capstone_project.controller.note_controller;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.UserWithNotes;
import com.example.lmemo_capstone_project.view.home_activity.NoteListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;

public class GetPublicNoteController {
    private FirebaseFirestore db;
    private ArrayList<Note> listNote;
    private NoteListAdapter noteListAdapter;
//    private int wordID;
    private WordDAO wordDAO;
//    private Word[] word;

    public GetPublicNoteController(Activity activity){
        db = FirebaseFirestore.getInstance();
        wordDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).wordDAO();
    }

//    private void getWordID() {
//        word = wordDAO.getAllWordID();
//
//        return wordID;
//    }

    public void getAllNoteFromFirebase(final ListView listview, final Activity activity, int wordID) {
        db = FirebaseFirestore.getInstance();

//        DocumentReference docRef = db.collection("users").document("userID");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//            }
//        });

        int[] x = new int[1];
        x[0] = wordID;
        Log.d("myApp", ""+wordID);
//        db.collectionGroup("words").whereEqualTo("wordID",wordID).
        db.collection("notes").whereArrayContains("words", Arrays.asList(x)).
//                addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        Log.d("myApp", queryDocumentSnapshots.getDocuments().get(0).getId());
//                    }
//                });
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Log.d("myApp", documentSnapshot.getId() + " => " + documentSnapshot.getData());
//                        db.collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                listNote = new ArrayList<>();
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                        Log.d("myApp", documentSnapshot.getId() + " => " + documentSnapshot.getData());
//                                        Note note = documentSnapshot.toObject(Note.class);
//                                        listNote.add(note);
//                                    }
//                                    ListView noteListView = listview;
//                                    NoteListAdapter noteListAdapter = new NoteListAdapter(activity, listNote);
//                                    noteListView.setAdapter(noteListAdapter);
//                                } else {
//                                    Log.d("MyApp", "Error getting documents: ", task.getException());
//                                }
                            }
//                        });
//                }
            }
        });
    }
}