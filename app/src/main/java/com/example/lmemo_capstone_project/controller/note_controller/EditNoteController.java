package com.example.lmemo_capstone_project.controller.note_controller;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteOfWordDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditNoteController {
    private WordDAO wordDAO;
    private NoteDAO noteDAO;
    private UserDAO userDAO;
    private NoteOfWordDAO noteOfWordDAO;
    private FirebaseFirestore db;
    private User user;
    private List<String> listWordID;
    public EditNoteController(Activity activity){
        db = FirebaseFirestore.getInstance();
        wordDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).wordDAO();
        userDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).userDAO();
        noteDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).noteDAO();
        noteOfWordDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).noteOfWordDAO();
        user= userDAO.getLocalUser()[0];
        listWordID = new ArrayList<>();
    }
    public void updateNoteOfWordInSQLite(Note note, String noteContent, boolean noteStatus, int wordID){
        Date date = new Date();
        note.setNoteContent(noteContent);
        note.setTranslatedContent("");
        note.setCreatedDate(date);
        note.setPublic(noteStatus);
        noteDAO.updateNote(note);
    }

    private void updateNoteOnlineToOffLine(Note note){
        String noteOnlineID = note.getOnlineID();
        db.collection("notes").document(noteOnlineID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("EditNoteController", "DocumentSnapshot successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        note.setOnlineID(null);
    }
    private void updateNoteOfflineToOnline(final Note note, final int wordID){
        Map<String, Object> addNote = new HashMap<>();
        addNote.put("noteContent", note.getNoteContent());
        addNote.put("translatedContent", note.getTranslatedContent());
        addNote.put("createdTime", note.getCreatedDate());
        addNote.put("userID", note.getCreatorUserID());
        addNote.put("wordID", Arrays.asList(wordID));
        db.collection("notes").add(addNote).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.w("EditNoteController", "Add new note successful " + documentReference.getId());
                note.setOnlineID(documentReference.getId());
                noteDAO.updateNote(note);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void addAssociationToSQLite(int wordID, int noteID){
        NoteOfWord noteOfWord = new NoteOfWord();
        noteOfWord.setWordID(wordID);
        noteOfWord.setNoteID(noteID);
        noteOfWordDAO.insertNoteOfWord(noteOfWord);
    }
    private void deleteAssociationInSQLite(NoteOfWord noteOfWord){
        noteOfWordDAO.deleteNoteOfWord(noteOfWord);
    }
    private void addAssociationToFireBase(int wordID, Note note){
        DocumentReference rf = db.collection("notes").document(note.getOnlineID());
        rf.update("wordID", FieldValue.arrayUnion(wordID));
    }
    private void deleteAssociationOnFirebase(Note note, int wordID){
        DocumentReference rf = db.collection("notes").document(note.getOnlineID());
        rf.update("wordID", FieldValue.arrayRemove(wordID));
    }
    private void getAllAssociation(Note note){
        db.collection("notes").document(note.getOnlineID()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                List<String> group = (List<String>) documentSnapshot.get("wordID");
            }
        });
    }
}
