package com.example.lmemo_capstone_project.controller.note_controller;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteOfWordDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;
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
import com.google.firebase.firestore.ListenerRegistration;
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
    private static GetNoteController singleTonInstance;
    private NoteDAO noteDAO;
    private NoteOfWordDAO noteOfWordDAO;
    private UserDAO userDAO;
    private FirebaseFirestore db;
    private WordSearchingFragment wordSearchingFragment;
    private ArrayList<Note> listNote;
    private ArrayList<User> listUser;
    private ListenerRegistration registration;
    private boolean isProcessing;

    private GetNoteController(WordSearchingFragment wordSearchingFragment) {
        db = FirebaseFirestore.getInstance();
        this.wordSearchingFragment = wordSearchingFragment;
        noteDAO = LMemoDatabase.getInstance(wordSearchingFragment.getContext()).noteDAO();
        userDAO = LMemoDatabase.getInstance(wordSearchingFragment.getContext()).userDAO();
        noteOfWordDAO = LMemoDatabase.getInstance(wordSearchingFragment.getContext()).noteOfWordDAO();
        isProcessing = false;
    }

    public GetNoteController(NoteDAO noteDAO, NoteOfWordDAO noteOfWordDAO) {
        this.noteDAO = noteDAO;
        this.noteOfWordDAO = noteOfWordDAO;
    }

    public static GetNoteController getInstance(WordSearchingFragment wordSearchingFragment) {
        if (singleTonInstance == null) {
            singleTonInstance = new GetNoteController(wordSearchingFragment);
        } else {
            singleTonInstance.wordSearchingFragment = wordSearchingFragment;
        }
        return singleTonInstance;
    }

    /**
     * @param wordID 検索する言葉のID
     * @param sortMode 順序のモード
     * この関数はファイアベースからノートを取り、UIを更新します。
     */
    public void getAllNotesFromFirebase(int wordID, final int sortMode) {
        isProcessing = false;
        Log.d("myApp", "How many times noteList is call");
        stopListening();
        Query query = db.collection("notes").whereArrayContains("wordID", wordID).
                orderBy("createdTime", sortMode == TIME_ASC ? Query.Direction.ASCENDING : Query.Direction.DESCENDING);
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!isProcessing) {
                            isProcessing = true;
                            listNote = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note note = processSnapshot(documentSnapshot);
                                Log.d("LIST_SIZE", documentSnapshot.getId() + " -> " + documentSnapshot.getData() + "\n" + note.getDownvoterList());
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
                                            int result = (upvote1 - upvote2) / (Math.abs(upvote1 - upvote2) == 0 ? 1 : Math.abs(upvote1 - upvote2));
                                            result *= sortMode == UPVOTE_DESC ? 1 : -1;
                                            return result;
                                        }
                                    });
                            }
                            getUserList();
                        }
                    }
                });
    }

    /**
     * @param documentSnapshot ノートの情報を持っているオブジェクト
     * @return そのノートのオブジェクト
     * この関数はスナップショットでノートのオブジェクトを作成するだけでなく、そのノートは現在のユーザーのかどうか、
     * SQLiteにあるかどうか確認し、現在のユーザーのノートで、SQLiteにある場合にはそのノートを更新します。
     */
    private Note processSnapshot(QueryDocumentSnapshot documentSnapshot) {
        Note note = documentSnapshot.toObject(Note.class);
        Map<String, Object> noteMap = documentSnapshot.getData();
        note.setCreatorUserID((String) noteMap.get("userID"));
        note.setPublic(true);
        note.setOnlineID(documentSnapshot.getId());
        note.setWordList((List<Long>) noteMap.get("wordID"));
        note.setUpvoterList((List<String>) noteMap.get("upvoter"));
        note.setDownvoterList((List<String>) noteMap.get("downvoter"));
        Note[] localNote = noteDAO.getNotesByOnlineID(note.getOnlineID());
        Log.w("AddNoteActivity", "OnlineID from getNote out" + note.getOnlineID());
        if (localNote.length == 0) {
            Log.w("AddNoteActivity", "OnlineID from getNote in no local" + note.getOnlineID());
            User[] localUser = userDAO.getLocalUser();
            if (localUser != null && localUser.length != 0 && localUser[0].getUserID().equalsIgnoreCase(note.getCreatorUserID())) {
                if (noteDAO.getLastNote().length != 0) {
                    note.setNoteID(noteDAO.getLastNote()[0].getNoteID() + 1);
                } else {
                    note.setNoteID(1);
                }
                noteDAO.insertNote(note);
                addNoteOfWord(note, (List<Long>) noteMap.get("wordID"));
            } else {
                note.setNoteID(-1);
            }
        } else {
            Log.w("AddNoteActivity", "OnlineID from getNote in has local" + note.getOnlineID());
            note.setNoteID(localNote[0].getNoteID());
            noteDAO.updateNote(note);
            addNoteOfWord(note, (List<Long>) noteMap.get("wordID"));
        }
        return note;
    }

    /**
     * @param note   ノートの情報を持っているオブジェクト
     * @param wordID ノートに添付する言葉のIDのリスト
     *               この関数はノートと言葉をSQLiteに追加します。
     */
    private void addNoteOfWord(Note note, List<Long> wordID) {
        noteOfWordDAO.deleteAllAssociationOfOneNote(note.getNoteID());
        NoteOfWord noteOfWord = new NoteOfWord();
        noteOfWord.setNoteID(note.getNoteID());
        for (Long i : wordID) {
            long wid = i;
            noteOfWord.setWordID((int) wid);
            noteOfWordDAO.insertNoteOfWord(noteOfWord);
        }
    }


    /**
     * すべてのノートの作者の情報を取り、UIを更新します。
     */
    private void getUserList() {
        Log.d("myApp", "How many times userlist is call");
        listUser = new ArrayList<>();
        isProcessing = !(listNote.size() == 0);
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

    /**
     * ノートの数はユーザーの数と同じになれば、ノートと作者を取り終わり、UIを更新します。
     */
    private void compareTwoListSize() {
        Log.d("compare_size", listNote.size() + " / " + listUser.size());
        if (listNote.size() == listUser.size()) {
            Map<String, User> listUserMap = new HashMap<>();
            for (User user : listUser) {
                listUserMap.put(user.getUserID(), user);
            }
            isProcessing = false;
            wordSearchingFragment.updateUI(listNote, listUserMap);
        }
    }

    /**
     * @param mode 公開かプライベートか
     * @param userID ユーザーのID
     * @return SQLiteにあって、modeであるすべてのノートのリスト
     */
    public List<Note> getOfflineNote(boolean mode, String userID) {
        Note[] notesOfUser = noteDAO.getNotesOfUser(mode, userID);
        for (Note note : notesOfUser) {
            note.setWordList(getWordList(note.getNoteID()));
        }
        return Lists.newArrayList(notesOfUser);
    }

    /**
     * @param noteID ノートのID
     * @return ノートに添付した言葉のID
     */
    private List<Long> getWordList(int noteID) {
        NoteOfWord[] noteOfWordList = noteOfWordDAO.getNoteOfWord(noteID);
        List<Long> listWordID = new ArrayList<>();
        for (NoteOfWord noteOfWord : noteOfWordList) {
            listWordID.add((long) noteOfWord.getWordID());
        }
        return listWordID;
    }


    /**
     * ノートが変化するかを観察するのをやめる。
     */
    public void stopListening() {
        if (registration != null) {
            registration.remove();
        }
    }
}
