package com.example.lmemo_capstone_project.controller.note_controller;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.view.home_activity.NoteListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

//        final Map<String, Object> addWordOfNote = new HashMap<>();
//        addWordOfNote.put("wordID", wordID+"");
        Log.d("myApp", ""+wordID);
//        db.collectionGroup("words").whereEqualTo("wordID",wordID).
        db.collection("notes").whereArrayContains("wordID", wordID).
//                addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        Log.d("myApp", queryDocumentSnapshots.getDocuments().get(0).getId());
//                    }
//                });
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("myApp", "Get here");
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