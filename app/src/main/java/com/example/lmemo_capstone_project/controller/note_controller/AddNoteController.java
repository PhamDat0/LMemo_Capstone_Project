package com.example.lmemo_capstone_project.controller.note_controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.controller.CannotPerformFirebaseRequest;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteOfWordDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.controller.my_account_controller.MyAccountController;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNoteController {
    private Context context;
    private NoteDAO noteDB;
    private UserDAO userDB;
    private NoteOfWordDAO noteOfWordDAO;
    private FirebaseFirestore db;
    private User user;

    public AddNoteController(Activity activity) {
        this.context = activity.getApplicationContext();
        db = FirebaseFirestore.getInstance();
        userDB = LMemoDatabase.getInstance(activity.getApplicationContext()).userDAO();
        noteDB = LMemoDatabase.getInstance(activity.getApplicationContext()).noteDAO();
        noteOfWordDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).noteOfWordDAO();
        user = userDB.getLocalUser()[0];
    }

    public void getNoteFromUI(int wordID, String noteContent, boolean noteStatus) throws CannotPerformFirebaseRequest {
        Log.w("Controller add note", user.getDisplayName());
        NoteOfWord noteOfWord = new NoteOfWord();
        Note note = new Note();
        Date date = new Date();
        int noteID = 0;
        if (noteDB.getLastNote().length != 0) {
            noteID = noteDB.getLastNote()[0].getNoteID() + 1;
        }
        note.setNoteID(noteID);
        note.setCreatedDate(date);
        note.setCreatorUserID(user.getUserID());
        note.setNoteContent(noteContent);
        note.setPublic(noteStatus);
        note.setTranslatedContent("");
        noteOfWord.setNoteID(noteID);
        noteOfWord.setWordID(wordID);
        if (noteStatus == false) {
            addNoteToSQLite(note);
            noteOfWordDAO.insertNoteOfWord(noteOfWord);
        } else {
            if (!user.getUserID().equals("GUEST") && !user.getUserID().isEmpty()) {
                addNoteToCloudFireStore(note, wordID);
                noteOfWordDAO.insertNoteOfWord(noteOfWord);
            } else {
                throw new CannotPerformFirebaseRequest("You must log in first!");
            }
        }
    }

    public void addNoteToSQLite(Note note){
        noteDB.insertNote(note);
    }

    private void addNoteToCloudFireStore(final Note note, final int wordID) {
        Map<String, Object> addNote = new HashMap<>();
        addNote.put("noteContent", note.getNoteContent());
        addNote.put("translatedContent", note.getTranslatedContent());
        addNote.put("createdTime", note.getCreatedDate());
        addNote.put("userID", note.getCreatorUserID());
        addNote.put("wordID", Arrays.asList(wordID));
        db.collection("notes").add(addNote).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.w("AddNoteActivity", "Add new note successful " + documentReference.getId());
                note.setOnlineID(documentReference.getId());
                noteDB.updateNote(note);
                User user = LMemoDatabase.getInstance(context).userDAO().getLocalUser()[0];
                user.setContributionPoint(user.getContributionPoint() + 1);
                new MyAccountController().updateUser(user);
                Log.d("AddNoteActivity", user.getContributionPoint() + "");
                userDB.updateUser(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                note.setPublic(false);
                noteDB.updateNote(note);
                Log.w("AddNoteActivity", "Error writing document");
            }
        });
        addNoteToSQLite(note);
    }

    public void downloadAllPublicNoteToSQL(User user) {
        db.collection("notes").whereEqualTo("userID", user.getUserID()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.d("myApp", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                Note[] notesByOnlineID = noteDB.getNotesByOnlineID(documentSnapshot.getId());
                                if (notesByOnlineID.length == 0) {
                                    int noteID = 0;
                                    if (noteDB.getLastNote().length != 0) {
                                        noteID = noteDB.getLastNote()[0].getNoteID() + 1;
                                    }
                                    Note note = getNoteFromSnapshot(documentSnapshot, noteID);
                                    noteDB.insertNote(note);
                                } else {
                                    int noteID = notesByOnlineID[0].getNoteID();
                                    Note note = getNoteFromSnapshot(documentSnapshot, noteID);
                                    noteDB.updateNote(note);
                                }
                            }
                        }
                    }
                });
    }

    private Note getNoteFromSnapshot(QueryDocumentSnapshot documentSnapshot, int noteID) {
        Note note = documentSnapshot.toObject(Note.class);
        Map<String, Object> noteMap = documentSnapshot.getData();
        note.setCreatorUserID((String) noteMap.get("userID"));
        note.setOnlineID(documentSnapshot.getId());
        note.setPublic(true);
        note.setNoteID(noteID);
        return note;
    }
}
