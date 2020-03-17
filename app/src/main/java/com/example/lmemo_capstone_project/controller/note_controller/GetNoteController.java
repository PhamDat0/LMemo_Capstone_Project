package com.example.lmemo_capstone_project.controller.note_controller;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.home_activity.search_view.WordSearchingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetNoteController {
    public static final boolean GET_OFFLINE_NOTE_PRIVATE = false;
    public static final boolean GET_OFFLINE_NOTE_PUBLIC = true;
    public static final int TIME_DESC = 0;
    public static final int TIME_ASC = 1;
    public static final int UPVOTE_DESC = 2;
    public static final int UPVOTE_ASC = 3;
    private NoteDAO noteDAO;
    private FirebaseFirestore db;
    private WordSearchingFragment wordSearchingFragment;
    private ArrayList<Note> listNote;
    private ArrayList<User> listUser;

    public GetNoteController(WordSearchingFragment wordSearchingFragment) {
        db = FirebaseFirestore.getInstance();
        this.wordSearchingFragment = wordSearchingFragment;
        noteDAO = LMemoDatabase.getInstance(wordSearchingFragment.getContext()).noteDAO();
    }

    public GetNoteController(NoteDAO noteDAO) {
        this.noteDAO = noteDAO;
    }

    /**
     * @param wordID 検索する言葉のID
     * @param sortMode 順序のモード
     * この関数はファイアベースからノートを取ります
     */
    public void getAllNotesFromFirebase(int wordID, final int sortMode) {
        db.collection("notes").whereArrayContains("wordID", wordID).
                orderBy("createdTime", sortMode == TIME_ASC ? Query.Direction.ASCENDING : Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        listNote = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d("LIST_SIZE", documentSnapshot.getId());
                            Note note = processSnapshot(documentSnapshot);
                            listNote.add(note);
                            Log.d("LIST_SIZE", listNote.size() + " in");
                        }
                        Log.d("LIST_SIZE", listNote.size() + " out");
                        switch (sortMode) {
                            case TIME_ASC:
                            case TIME_DESC:
                                break;
                            default:
                                Collections.sort(listNote, new Comparator<Note>() {
                                    @Override
                                    public int compare(Note o1, Note o2) {
                                        int upvote1 = o1.getUpvoterList().size() - o1.getDownvoterList().size();
                                        int upvote2 = o2.getUpvoterList().size() - o2.getDownvoterList().size();
                                        int result = (upvote1 - upvote2) / Math.abs(upvote1 - upvote2);
                                        result *= sortMode == UPVOTE_ASC ? 1 : -1;
                                        return result;
                                    }
                                });
                        }
                        getUserList();
                    }
                });
    }

    private Note processSnapshot(QueryDocumentSnapshot documentSnapshot) {
        Note note = documentSnapshot.toObject(Note.class);
        Map<String, Object> noteMap = documentSnapshot.getData();
        note.setCreatorUserID((String) noteMap.get("userID"));
        note.setPublic(true);
        note.setOnlineID(documentSnapshot.getId());
        note.setUpvoterList((List<String>) noteMap.get("upvoter"));
        note.setDownvoterList((List<String>) noteMap.get("downvoter"));
        Note[] localNote = noteDAO.getNotesByOnlineID(note.getOnlineID());
        if (localNote.length == 0) {
            if (noteDAO.getLastNote().length != 0) {
                note.setNoteID(noteDAO.getLastNote()[0].getNoteID() + 1);
            } else {
                note.setNoteID(1);
            }
        } else {
            note.setNoteID(localNote[0].getNoteID());
        }
        return note;
    }

    private void getUserList() {
        listUser = new ArrayList<>();
        for (Note note : listNote) {
            String creatorUserID = note.getCreatorUserID();
            DocumentReference userRef = db.collection("users").document(creatorUserID);
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
        Log.d("compare_size", listNote.size() + " / " + listUser.size());
        if (listNote.size() == listUser.size()) {
            Map<String, User> listUserMap = new HashMap<>();
            for (User user : listUser) {
                listUserMap.put(user.getUserID(), user);
            }
            wordSearchingFragment.updateUI(listNote, listUserMap);
        }
    }

    public List<Note> getOfflineNote(boolean mode, String userID) {
        Note[] notesOfUser = noteDAO.getNotesOfUser(mode, userID);
        return Lists.newArrayList(notesOfUser);
    }
}
