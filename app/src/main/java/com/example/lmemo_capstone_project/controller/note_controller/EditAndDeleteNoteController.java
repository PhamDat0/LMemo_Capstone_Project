package com.example.lmemo_capstone_project.controller.note_controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteOfWordDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.controller.my_account_controller.MyAccountController;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditAndDeleteNoteController {
    private Context context;
    private WordDAO wordDAO;
    private NoteDAO noteDAO;
    private UserDAO userDAO;
    private NoteOfWordDAO noteOfWordDAO;
    private FirebaseFirestore db;
    private User user;
    private List<String> listWordID;

    public EditAndDeleteNoteController(Activity activity) {
        this.context = activity.getApplicationContext();
        db = FirebaseFirestore.getInstance();
        wordDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).wordDAO();
        userDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).userDAO();
        noteDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).noteDAO();
        noteOfWordDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).noteOfWordDAO();
        user = userDAO.getLocalUser()[0];
        listWordID = new ArrayList<>();
    }

    public void updateNote(Note note, String noteContent, boolean noteStatus, List<Word> words) {
        if (note.isPublic()) {
            if (!noteStatus) {
                deleteNoteFromFB(note);
            } else {
                updateNoteOnFirebase(note, noteContent, noteStatus, words);
            }
        } else {
            if (noteStatus) {
                addNoteToFirebase(note, noteContent, noteStatus, words);
            }
        }
        updateNoteInSQLite(note, noteContent, noteStatus, words);
    }

    private void addNoteToFirebase(final Note note, String noteContent, boolean noteStatus, List<Word> words) {
        List<Integer> listWordID = new ArrayList<>();
        for (Word word : words) {
            listWordID.add(word.getWordID());
        }
        Map<String, Object> addNote = new HashMap<>();
        addNote.put("noteContent", noteContent);
        addNote.put("translatedContent", note.getTranslatedContent());
        addNote.put("createdTime", note.getCreatedDate() == null ? new Date() : note.getCreatedDate());
        addNote.put("userID", note.getCreatorUserID());
        addNote.put("wordID", listWordID);
        db.collection("notes").add(addNote).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.w("AddNoteActivity", "Add new note successful " + documentReference.getId());
                note.setOnlineID(documentReference.getId());
                updateUserContributionPoint(1);
                noteDAO.updateNote(note);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                note.setPublic(false);
                noteDAO.updateNote(note);
                Log.w("AddNoteActivity", "Error writing document");
            }
        });
    }

    private void updateNoteOnFirebase(Note note, String noteContent, boolean noteStatus, List<Word> words) {
        db.collection("notes").document(note.getOnlineID())
                .update("noteContent", noteContent, "wordID", words)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditNoteController", "DocumentSnapshot was successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EditNoteController", "DocumentSnapshot update fails!");
            }
        });
    }

    private void updateNoteInSQLite(Note note, String noteContent, boolean noteStatus, List<Word> words) {
        noteOfWordDAO.deleteAllAssociationOfOneNote(note.getNoteID());
        for (Word word : words) {
            Log.i("INSERT_HAS_NO_DEFECTS", word.getWordID() + "");
            NoteOfWord noteOfWord = new NoteOfWord();
            noteOfWord.setNoteID(note.getNoteID());
            noteOfWord.setWordID(word.getWordID());
            noteOfWordDAO.insertNoteOfWord(noteOfWord);
        }
        note.setNoteContent(noteContent);
        note.setTranslatedContent("");
        note.setPublic(noteStatus);
        noteDAO.updateNote(note);
    }

    private void deleteNoteFromFB(Note note) {
        String noteOnlineID = note.getOnlineID();
        db.collection("notes").document(noteOnlineID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditNoteController", "DocumentSnapshot successfully deleted!");
                        updateUserContributionPoint(-1);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        note.setOnlineID(null);
    }

    private void updateUserContributionPoint(int addPoint) {
        User user = userDAO.getLocalUser()[0];
        user.setContributionPoint(user.getContributionPoint() + addPoint);
        MyAccountController myAccountController = new MyAccountController();
        myAccountController.updateUser(user);
        userDAO.updateUser(user);
    }

    private void updateNoteOfflineToOnline(final Note note, final int wordID) {
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

    private void addAssociationToSQLite(int wordID, int noteID) {
        NoteOfWord noteOfWord = new NoteOfWord();
        noteOfWord.setWordID(wordID);
        noteOfWord.setNoteID(noteID);
        noteOfWordDAO.insertNoteOfWord(noteOfWord);
    }

    private void deleteAssociationInSQLite(NoteOfWord noteOfWord) {
        noteOfWordDAO.deleteNoteOfWord(noteOfWord);
    }

    private void addAssociationToFireBase(int wordID, Note note) {
        DocumentReference rf = db.collection("notes").document(note.getOnlineID());
        rf.update("wordID", FieldValue.arrayUnion(wordID));
    }

    private void deleteAssociationOnFirebase(Note note, int wordID) {
        DocumentReference rf = db.collection("notes").document(note.getOnlineID());
        rf.update("wordID", FieldValue.arrayRemove(wordID));
    }

    private void getAllAssociation(Note note) {
        db.collection("notes").document(note.getOnlineID()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                List<String> group = (List<String>) documentSnapshot.get("wordID");
            }
        });
    }

    public void deleteNote(Note note) {
        noteOfWordDAO.deleteAllAssociationOfOneNote(note.getNoteID());
        noteDAO.deleteNote(note);
        if (note.isPublic()) {
            deleteNoteFromFB(note);
        }
    }
}
