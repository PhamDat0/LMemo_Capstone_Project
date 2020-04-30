package com.example.lmemo_capstone_project.controller.note_controller;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.controller.CannotPerformFirebaseRequest;
import com.example.lmemo_capstone_project.controller.StringProcessUtilities;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteOfWordDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.controller.my_account_controller.MyAccountController;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteController {
    private static final int UPVOTE = 1;
    private static final int DOWNVOTE = 2;

    private NoteDAO noteDAO;
    private UserDAO userDAO;
    private NoteOfWordDAO noteOfWordDAO;
    private FirebaseFirestore db;
    private User user;

    public NoteController(Context context) {
        LMemoDatabase lMemoDatabase = LMemoDatabase.getInstance(context);
        noteDAO = lMemoDatabase.noteDAO();
        userDAO = lMemoDatabase.userDAO();
        noteOfWordDAO = lMemoDatabase.noteOfWordDAO();
        db = FirebaseFirestore.getInstance();
        user = userDAO.getLocalUser()[0];
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
                addNoteToCloudFireStore(note, noteContent, noteStatus, words);
            }
        }
        updateNoteInSQLite(note, noteContent, noteStatus, words);
    }

    /**
     * @param note        ノートのオブジェクト。ユーザーのIDを持っているオブジェクト
     * @param noteContent ノートの内容
     * @param noteStatus  公開か秘密か
     * @param words       ノートに添付された言葉のリスト
     *                    この関数はノートをFireStoreにアップロードします。
     */
    private void addNoteToCloudFireStore(final Note note, String noteContent, boolean noteStatus, List<Word> words) {
        List<Long> listWordID = new ArrayList<>();
        for (Word word : words) {
            listWordID.add((long) word.getWordID());
        }
        note.setNoteContent(noteContent);
        note.setCreatedTime(note.getCreatedTime() == null ? new Date() : note.getCreatedTime());
        addNoteToCloudFireStore(note, listWordID);
    }

    /**
     * @param note   ノートの情報を持っているオブジェクト
     * @param wordID ノートに添付された言葉のIDのリスト
     *               この関数はノートをFireStoreにアップロードします。
     */
    private void addNoteToCloudFireStore(final Note note, final List<Long> wordID) {
        Map<String, Object> addNote = new HashMap<>();
        addNote.put("noteContent", note.getNoteContent());
        addNote.put("translatedContent", note.getTranslatedContent());
        addNote.put("createdTime", note.getCreatedTime());
        addNote.put("userID", note.getCreatorUserID());
        addNote.put("wordID", wordID);
        addNote.put("upvoter", note.getUpvoterList() == null ? new ArrayList<String>() : note.getUpvoterList());
        addNote.put("downvoter", note.getDownvoterList() == null ? new ArrayList<String>() : note.getDownvoterList());
        final boolean baseStatus = note.isPublic();
        db.collection("notes").add(addNote).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.w("AddNoteActivity", "Add new note successful " + documentReference.getId());
                if (!baseStatus) {
                    note.setOnlineID(documentReference.getId());
                    Log.w("AddNoteActivity", "OnlineID " + note.getOnlineID());
                    noteDAO.updateNote(note);
                }
                Log.d("AddNoteActivity", "We get this far");
                user.setContributionPoint(user.getContributionPoint() + 1);
                new MyAccountController().increaseUserPoint(user.getUserID(), 1);
                userDAO.updateUser(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                note.setPublic(false);
//                noteDAO.updateNote(note);
                ProgressDialog.getInstance().dismiss();
                Log.w("AddNoteActivity", "Error writing document");
            }
        });
    }

    /**
     * @param note        編集するノート
     * @param noteContent 新しい内容
     * @param noteStatus  新しい状態。公開か秘密か
     * @param words       ノートに添付された言葉のリスト
     *                    この関数はnoteContentとnoteStatusとwordsでFireStoreにある１つのノートを編集します。
     */
    private void updateNoteOnFirebase(Note note, String noteContent, boolean noteStatus, List<Word> words) {
        List<Integer> listWordID = new ArrayList<>();
        for (Word word : words) {
            listWordID.add(word.getWordID());
        }
        db.collection("notes").document(note.getOnlineID())
                .update("noteContent", noteContent, "wordID", listWordID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditNoteController", "DocumentSnapshot was successfully updated!");
                        ProgressDialog.getInstance().dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EditNoteController", "DocumentSnapshot update fails!");
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    /**
     * @param note        編集するノート
     * @param noteContent 新しい内容
     * @param noteStatus  新しい状態。公開か秘密か
     * @param words       ノートに添付された言葉のリスト
     *                    この関数はnoteContentとnoteStatusとwordsでSQLiteにある１つのノートを編集します。
     */
    private void updateNoteInSQLite(Note note, String noteContent, boolean noteStatus, List<Word> words) {
        noteOfWordDAO.deleteAllAssociationOfOneNote(note.getNoteID());
        List<Long> listWordID = new ArrayList<>();
        for (Word word : words) {
            listWordID.add((long) word.getWordID());
        }
        insertWordToNoteOfWord(note, listWordID);
        note.setNoteContent(noteContent);
        note.setTranslatedContent("");
        note.setPublic(noteStatus);
        noteDAO.updateNote(note);
    }

    /**
     * @param note 削除するノート
     *             noteをFireStoreから削除し、
     */
    private void deleteNoteFromFB(Note note) {
        final String noteOnlineID = note.getOnlineID();
        Log.w("AddNoteActivity", "OnlineID controller 2" + note.getOnlineID());
        db.collection("notes").document(noteOnlineID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EditNoteController", "DocumentSnapshot successfully deleted!");
                        db.collection("comment").whereEqualTo("noteOnlineID", noteOnlineID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                    db.collection("comment").document(document.getId()).delete();
                                }
                                new MyAccountController().increaseUserPoint(user.getUserID(), -1);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new MyAccountController().increaseUserPoint(user.getUserID(), -1);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ProgressDialog.getInstance().dismiss();
                throw new UnsupportedOperationException("An unknown error occurs");
            }
        });
        note.setOnlineID(null);
    }

    public void deleteNote(Note note) {
        noteOfWordDAO.deleteAllAssociationOfOneNote(note.getNoteID());
        noteDAO.deleteNote(note);
        if (note.isPublic()) {
            Log.w("AddNoteActivity", "OnlineID controller 1 " + note.getOnlineID());
            deleteNoteFromFB(note);
        }
    }

    public void getNoteFromUI(List<Word> words, String noteContent, boolean noteStatus) throws CannotPerformFirebaseRequest {
//        Log.w("Controller add note", user.getDisplayName());
        NoteOfWord noteOfWord = new NoteOfWord();
        Note note = new Note();
        Date date = new Date();
        int noteID = 0;
        if (noteDAO.getLastNote().length != 0) {
            noteID = noteDAO.getLastNote()[0].getNoteID() + 1;
        } else {
            noteID = 1;
        }
        note.setNoteID(noteID);
        note.setCreatedTime(date);
        note.setCreatorUserID(user.getUserID());
        note.setNoteContent(noteContent);
        note.setPublic(noteStatus);
        note.setTranslatedContent("");
        List<Long> wordIDArray = new ArrayList<>();
        for (Word word : words) {
            wordIDArray.add((long) word.getWordID());
        }
        note.setWordList(wordIDArray);
        noteOfWord.setNoteID(noteID);
//        noteOfWord.setWordID(wordID);
        if (!noteStatus) {
            addNoteToSQLite(note);
            addNoteOfWordToSQL(words, noteOfWord);
        } else {
            if (!user.getUserID().equals("GUEST") && !user.getUserID().isEmpty()) {
//                addNoteToSQLite(note);
                addNoteToCloudFireStore(note, wordIDArray);
//                addNoteOfWordToSQL(words, noteOfWord);
            } else {
                throw new CannotPerformFirebaseRequest("You must log in first!");
            }
        }
    }

    /**
     * @param words      List of words
     * @param noteOfWord The note of word contains the inserted NoteID
     * @throws Exception throw exception if noteOfWord doesn't contain NoteID
     */
    private void addNoteOfWordToSQL(List<Word> words, NoteOfWord noteOfWord) {
        for (Word word : words) {
            noteOfWord.setWordID(word.getWordID());
            noteOfWordDAO.insertNoteOfWord(noteOfWord);
        }
    }

    private void addNoteToSQLite(Note note) {
        noteDAO.insertNote(note);
    }

    public void downloadAllPublicNoteToSQL(final User user) {
        db.collection("notes").whereEqualTo("userID", user.getUserID()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d("myApp", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            Note[] notes = noteDAO.getUserOnlineNoteOnDevices(user.getUserID());
                            for (Note note : notes) {
                                note.setPublic(false);
                                noteDAO.updateNote(note);
                            }
                            Note[] notesByOnlineID = noteDAO.getNotesByOnlineID(documentSnapshot.getId());
                            if (notesByOnlineID.length == 0) {
                                int noteID;
                                if (noteDAO.getLastNote().length != 0) {
                                    noteID = noteDAO.getLastNote()[0].getNoteID() + 1;
                                } else {
                                    noteID = 1;
                                }
                                Note note = getNoteFromSnapshot(documentSnapshot, noteID);
                                noteDAO.insertNote(note);
                                insertWordToNoteOfWord(note, note.getWordList());
                            } else {
                                int noteID = notesByOnlineID[0].getNoteID();
                                Note note = getNoteFromSnapshot(documentSnapshot, noteID);
                                noteDAO.updateNote(note);
                                insertWordToNoteOfWord(note, note.getWordList());
                            }
                            notes = noteDAO.getUserOfflineNoteOnDevices(user.getUserID());
                            for (Note note : notes) {
                                if (!StringProcessUtilities.isEmpty(note.getOnlineID())) {
                                    note.setOnlineID("");
                                    noteDAO.updateNote(note);
                                }
                            }
                        }
                        ProgressDialog.getInstance().dismiss();
                    }
                });
    }

    private void insertWordToNoteOfWord(Note note, List<Long> wordList) {
        NoteOfWord noteOfWord = new NoteOfWord();
        noteOfWord.setNoteID(note.getNoteID());
        for (Long word : wordList) {
            long wordID = word;
            noteOfWord.setWordID((int) wordID);
            noteOfWordDAO.insertNoteOfWord(noteOfWord);
        }
    }

    private Note getNoteFromSnapshot(QueryDocumentSnapshot documentSnapshot, int noteID) {
        Note note = documentSnapshot.toObject(Note.class);
        Map<String, Object> noteMap = documentSnapshot.getData();
        note.setCreatorUserID((String) noteMap.get("userID"));
        note.setOnlineID(documentSnapshot.getId());
        List<Long> wordIDList = (List<Long>) noteMap.get("wordID");
        note.setWordList(wordIDList);
        note.setPublic(true);
        note.setNoteID(noteID);
        return note;
    }

    public void upvote(Note note) throws CannotPerformFirebaseRequest {
        if (!note.getUpvoterList().contains(user.getUserID())) {
            int pointForOwner = note.getDownvoterList().contains(user.getUserID()) ? 2 : 1;
            performVote(note, UPVOTE, pointForOwner);
        }
    }

    public void downvote(Note note) throws CannotPerformFirebaseRequest {
        if (!note.getDownvoterList().contains(user.getUserID())) {
            int pointForOwner = note.getUpvoterList().contains(user.getUserID()) ? -2 : -1;
            performVote(note, DOWNVOTE, pointForOwner);
        }
    }

    private void performVote(final Note note, final int mode, final int pointForOwner) throws CannotPerformFirebaseRequest {
        if (!user.getUserID().equalsIgnoreCase("GUEST")) {
            final DocumentReference docRef = db.collection("notes").document(note.getOnlineID());
            docRef.update("upvoter", FieldValue.arrayRemove(user.getUserID()), "downvoter", FieldValue.arrayRemove(user.getUserID()), mode == UPVOTE ? "upvoter" : "downvoter", FieldValue.arrayUnion(user.getUserID())).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("Vote_success", "DELETE FROM LIST");
                    new MyAccountController().increaseUserPoint(note.getCreatorUserID(), pointForOwner);
                    Log.i("Vote_success", mode == UPVOTE ? "upvote" : "downvote" + " on " + note.getOnlineID() + " by " + user.getUserID());
                }
            });
        } else {
            throw new CannotPerformFirebaseRequest("You must logged in");
        }
    }
}
