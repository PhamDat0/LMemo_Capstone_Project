package com.example.lmemo_capstone_project.controller.set_flashcard_controller;

import android.app.Activity;
import android.util.Log;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardBelongToSetDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.SetFlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.FlashcardBelongToSet;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetFlashcardController {
    private static final int NOT_DELETE = 0;
    private static final int DELETE = 1;
    private static final String COLLECTION_PATH = "setFlashCards";
    private FirebaseFirestore firebaseFirestore;
    private SetFlashcardDAO setFlashcardDAO;
    private FlashcardBelongToSetDAO flashcardBelongToSetDAO;
    private UserDAO userDAO;

    public SetFlashcardController(Activity aContext) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        LMemoDatabase instance = LMemoDatabase.getInstance(aContext);
        setFlashcardDAO = instance.setFlashcardDAO();
        userDAO = instance.userDAO();
        flashcardBelongToSetDAO = instance.flashcardBelongToSetDAO();
    }

    public void uploadSetToFirebase(final SetFlashcard setFlashcard) {
        Map<String, Object> setToAdd = mapping(setFlashcard);
        firebaseFirestore.collection(COLLECTION_PATH).add(setToAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String onlineID = documentReference.getId();
                setFlashcard.setOnlineID(onlineID);
                setFlashcard.setPublic(true);
                setFlashcardDAO.updateSetFlashcard(setFlashcard);
                Log.d("SF_CONTROLLER", "Upload set successfully");
            }
        });
    }

    private Map<String, Object> mapping(SetFlashcard setFlashcard) {
        FlashcardBelongToSet[] flashcards = flashcardBelongToSetDAO.getFlashcardBySetID(setFlashcard.getSetID());
        List<Long> flashcardIDList = new ArrayList<>();
        for (FlashcardBelongToSet flashcard : flashcards) {
            flashcardIDList.add((long) flashcard.getFlashcardID());
        }
        if (flashcardIDList.size() < 10) {
            throw new UnsupportedOperationException("Public set must have at least 10 flashcards");
        }
        Map<String, Object> setToAdd = new HashMap<>();
        setToAdd.put("name", setFlashcard.getSetName());
        setToAdd.put("userID", setFlashcard.getCreatorID());
        setToAdd.put("flashcards", flashcardIDList);
        return setToAdd;
    }

    public void makeSetPrivate(SetFlashcard setFlashcard) {
        removeSetFromFirebase(setFlashcard, NOT_DELETE);
    }

    private void removeSetFromFirebase(final SetFlashcard setFlashcard, final int mode) {
        firebaseFirestore.collection(COLLECTION_PATH).document(setFlashcard.getOnlineID())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (mode) {
                    case NOT_DELETE:
                        setFlashcard.setPublic(false);
                        setFlashcard.setOnlineID("");
                        setFlashcardDAO.updateSetFlashcard(setFlashcard);
                        Log.d("SF_CONTROLLER", "Make set private successfully");
                        break;
                    case DELETE:
                        deleteFromSQLite(setFlashcard);
                        break;
                    default:
                        throw new UnsupportedOperationException("There is no such mode");
                }
            }
        });
    }

    private void deleteFromSQLite(SetFlashcard setFlashcard) {
        flashcardBelongToSetDAO.deleteAllAssociationWithSet(setFlashcard.getSetID());
        setFlashcardDAO.deleteSetFlashcard(setFlashcard);
    }
}
