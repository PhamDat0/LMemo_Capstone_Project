package com.example.lmemo_capstone_project.controller.set_flashcard_controller;

import android.content.Context;
import android.util.Log;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardBelongToSetDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.SetFlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.FlashcardBelongToSet;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view.SetFlashCardOnlineTab;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetSetFlashcardController {

    private static final String COLLECTION_PATH = "setFlashCards";
    private static final long RECORD_PER_PAGE = 10;

    private UserDAO userDAO;
    private FlashcardBelongToSetDAO flashcardBelongToSetDAO;
    private SetFlashCardOnlineTab setFlashCardFragment;
    private SetFlashcardDAO setFlashcardDAO;
    private FirebaseFirestore firebaseFirestore;
    private List<SetFlashcard> listSet;


    public GetSetFlashcardController(SetFlashCardOnlineTab setFlashCardFragment) {
        this(setFlashCardFragment.getContext());
        this.setFlashCardFragment = setFlashCardFragment;
    }

    GetSetFlashcardController(Context context) {
        LMemoDatabase dbInstance = LMemoDatabase.getInstance(context);
        setFlashcardDAO = dbInstance.setFlashcardDAO();
        userDAO = dbInstance.userDAO();
        flashcardBelongToSetDAO = dbInstance.flashcardBelongToSetDAO();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /**
     * @return すべてのSQLiteの中にあるセットを持つリスト
     */
    public List<SetFlashcard> getOfflineSet() {
        List<SetFlashcard> listSetFlashcard = Lists.newArrayList(setFlashcardDAO.getAllSets());
        for (SetFlashcard setFlashcard : listSetFlashcard) {
            int setID = setFlashcard.getSetID();
            FlashcardBelongToSet[] flashcardBySetID = flashcardBelongToSetDAO.getFlashcardBySetID(setID);
            List<Long> wordID = new ArrayList<>();
            for (FlashcardBelongToSet fc : flashcardBySetID) {
                wordID.add((long) fc.getFlashcardID());
            }
            setFlashcard.setWordID(wordID);
            setFlashcard.setCreator(userDAO.getUserByID(setFlashcard.getCreatorID())[0]);
        }
        return listSetFlashcard;
    }

    public void getOnlineSet(String keyword) {
        listSet = new ArrayList<>();
        firebaseFirestore.collection(COLLECTION_PATH)
                .whereGreaterThanOrEqualTo("name", keyword).whereLessThanOrEqualTo("name", keyword + '\uf8ff')
                .orderBy("name", Query.Direction.ASCENDING).orderBy(FieldPath.documentId(), Query.Direction.ASCENDING)
                .limit(RECORD_PER_PAGE).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                Log.i("SEARCH_SET", "" + documents.size());
                for (DocumentSnapshot document : documents) {
                    SetFlashcard setFlashcard = getSetFromSnapshot(document);
                    listSet.add(setFlashcard);
                }
                //                reverseList(listSet);
                getSetOwners();
            }
        });
    }

    public void getMoreOnlineSet(String keyword, List<SetFlashcard> baseList) {
        listSet = Lists.reverse(baseList);
        SetFlashcard lastSetInList = listSet.get(listSet.size() - 1);
        firebaseFirestore.collection(COLLECTION_PATH)
                .whereGreaterThanOrEqualTo("name", keyword).whereLessThanOrEqualTo("name", keyword + '\uf8ff')
                .orderBy("name", Query.Direction.ASCENDING).orderBy(FieldPath.documentId(), Query.Direction.ASCENDING)
                .startAfter(lastSetInList.getSetName(), lastSetInList.getOnlineID()).limit(RECORD_PER_PAGE)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                Log.i("SEARCH_SET", "" + documents.size());
                for (DocumentSnapshot document : documents) {
                    SetFlashcard setFlashcard = getSetFromSnapshot(document);
                    listSet.add(setFlashcard);
                }
//                reverseList(listSet);
                getSetOwners();
            }
        });
    }

//    public void getUserOnlineSet(User currentUser) {
//        listSet = new ArrayList<>();
//        firebaseFirestore.collection(COLLECTION_PATH).whereEqualTo("userID", currentUser.getUserID())
//                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
//                for (DocumentSnapshot document : documents) {
//                    SetFlashcard setFlashcard = getSetFromSnapshot(document);
//                    listSet.add(setFlashcard);
//                }
//                getSetOwners();
//            }
//        });
//    }

    private void getSetOwners() {
        if (listSet.isEmpty() || isFinish()) {
            updateInterfaceIfFinish();
        } else {
            for (final SetFlashcard setFlashcard : listSet) {
                String creatorID = setFlashcard.getCreatorID();
                firebaseFirestore.collection("users").document(creatorID)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User owner = getUserFromSnapshot(documentSnapshot);
                        setFlashcard.setCreator(owner);
                        updateInterfaceIfFinish();
                    }
                });
            }
        }
    }

    private synchronized void updateInterfaceIfFinish() {
        if (isFinish()) {
            ProgressDialog.getInstance().dismiss();
            listSet = Lists.reverse(listSet);
            setFlashCardFragment.updateUI(listSet);
        }
    }
//
//    private void orderListByNewLoadedSetFirst(List<SetFlashcard> listSet) {
//        this.listSet = Lists.reverse(listSet);
//    }

    private boolean isFinish() {
        for (SetFlashcard setFlashcard : listSet) {
            if (setFlashcard.getCreator() == null)
                return false;
        }
        return true;
    }


    private User getUserFromSnapshot(DocumentSnapshot document) {
        User user = document.toObject(User.class);
        Map<String, Object> userMap = document.getData();
        user.setGender((Boolean) userMap.get("isMale"));
        return user;
    }

    private SetFlashcard getSetFromSnapshot(DocumentSnapshot document) {
        SetFlashcard setFlashcard = new SetFlashcard();
        setFlashcard.setSetName((String) document.get("name"));
        setFlashcard.setPublic(true);
        setFlashcard.setWordID((List<Long>) document.get("flashcards"));
        setFlashcard.setCreatorID((String) document.get("userID"));
        setFlashcard.setCreator(null);
        setFlashcard.setOnlineID(document.getId());
        updateOfflineSetIfNecessary(setFlashcard);
        return setFlashcard;
    }

    private void updateOfflineSetIfNecessary(SetFlashcard setFlashcard) {
        if (setFlashcard.getCreatorID().equalsIgnoreCase(userDAO.getLocalUser()[0].getUserID())) {
            SetFlashcardController setFlashcardController = new SetFlashcardController(setFlashCardFragment.getActivity());
            setFlashcardController.downloadSet(setFlashcard);
        }
    }
}
