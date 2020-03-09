package com.example.lmemo_capstone_project.controller.note_controller;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteOfWordDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNoteController {
    private NoteDAO noteDB;
    private UserDAO userDB;
    private NoteOfWordDAO noteOfWordDAO;
    private FirebaseFirestore db;
    private User user;
    public AddNoteController(Activity activity){
        db = FirebaseFirestore.getInstance();
        userDB = LMemoDatabase.getInstance(activity.getApplicationContext()).userDAO();
        noteDB = LMemoDatabase.getInstance(activity.getApplicationContext()).noteDAO();
        noteOfWordDAO = LMemoDatabase.getInstance(activity.getApplicationContext()).noteOfWordDAO();
        user= userDB.getLocalUser()[0];
    }

    public void getNoteFromUI(int wordID, String noteContent, boolean noteStatus){

        Log.w("Controller add note", user.getDisplayName());
        NoteOfWord noteOfWord = new NoteOfWord();
        Note note = new Note();
        Date date = new Date();
        int noteID =0;
        if (noteDB.getLastNote().length !=0){
            noteID=noteDB.getLastNote()[0].getNoteID()+1;
        }
        note.setNoteID(noteID);
        note.setCreatedDate(date);
        note.setCreatorUserID(user.getUserID());
        note.setNoteContent(noteContent);
        note.setPublic(noteStatus);
        note.setTranslatedContent("");
        noteOfWord.setNoteID(noteID);
        noteOfWord.setWordID(wordID);
        if(noteStatus==false){
                addNoteToSQLite(note);
                noteOfWordDAO.insertNoteOfWord(noteOfWord);
        }
        else {
            if (user.getUserID().equals("GUEST") ||user.getUserID().isEmpty()){

            }
            else {
                addNoteToCloudFireStore(note, wordID);
                noteOfWordDAO.insertNoteOfWord(noteOfWord);

            }
        }



    }
    public void addNoteToSQLite(Note note){
        noteDB.insertNote(note);
    }
    private void addNoteToCloudFireStore(final Note note, final int wordID){
        Map<String, Object> addNote = new HashMap<>();
        addNote.put("noteContent", note.getNoteContent());
        addNote.put("translatedContent", note.getTranslatedContent());
        addNote.put("createdTime", note.getCreatedDate());
        addNote.put("userID", note.getCreatorUserID());
        final Map<String, Object> addWordOfNote = new HashMap<>();
        addWordOfNote.put("wordID", wordID);
        db.collection("notes").add(addNote).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.w("AddNoteActivity", "Add new note successful " + documentReference.getId());
                note.setOnlineID(documentReference.getId());
                db.collection("notes").document(documentReference.getId()).collection("words").document(""+wordID).set(addWordOfNote).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.w("AddWordSuccessfull", "Add word to note successful");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("AddNoteActivity", "Error writing document");
            }
        });
        addNoteToSQLite(note);
    }
}
