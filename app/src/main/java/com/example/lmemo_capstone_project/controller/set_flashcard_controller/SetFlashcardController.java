package com.example.lmemo_capstone_project.controller.set_flashcard_controller;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardBelongToSetDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.SetFlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.controller.my_account_controller.MyAccountController;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.FlashcardBelongToSet;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetFlashcardController {
    private static final int NOT_DELETE = 0;
    private static final int DELETE = 1;
    private static final String COLLECTION_PATH = "setFlashCards";
    private final FlashcardDAO flashcardDAO;
    private final WordDAO wordDAO;
    private FirebaseFirestore firebaseFirestore;
    private SetFlashcardDAO setFlashcardDAO;
    private FlashcardBelongToSetDAO flashcardBelongToSetDAO;
    private UserDAO userDAO;

    public SetFlashcardController(Activity aContext) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        LMemoDatabase instance = LMemoDatabase.getInstance(aContext);
        setFlashcardDAO = instance.setFlashcardDAO();
        flashcardDAO = instance.flashcardDAO();
        wordDAO = instance.wordDAO();
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
                updateSetToSQL(setFlashcard);
                new MyAccountController().increaseUserPoint(setFlashcard.getCreatorID(), 1);
                Log.d("SF_CONTROLLER", "Upload set successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setFlashcard.setOnlineID("");
                setFlashcard.setPublic(false);
                updateSetToSQL(setFlashcard);
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
                new MyAccountController().increaseUserPoint(setFlashcard.getCreatorID(), -1);
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

    public void deleteSetFromFirebase(SetFlashcard setFlashcard) {
        removeSetFromFirebase(setFlashcard, DELETE);
    }

    public void deleteOfflineSet(SetFlashcard setFlashcard) {
        deleteFromSQLite(setFlashcard);
    }

    public void downloadSet(SetFlashcard setFlashcard) {
        if (hasThisSetInSQLite(setFlashcard)) {
            setFlashcard.setSetID(getSetIDFromOnlineID(setFlashcard));
            deleteFromSQLite(setFlashcard);
        } else {
            SetFlashcard[] lastSet = setFlashcardDAO.getLastSet();
            if (lastSet.length != 0) {
                setFlashcard.setSetID(lastSet[0].getSetID() + 1);
            } else {
                setFlashcard.setSetID(1);
            }
        }
        User creator = setFlashcard.getCreator();
        creator.setLoginTime(new Date(1));
        userDAO.insertUser(creator);
        writeToSQLite(setFlashcard);
    }

    private void writeToSQLite(SetFlashcard setFlashcard) {
        setFlashcardDAO.insertSetFlashcard(setFlashcard);
        insertNecessaryFlashcard(setFlashcard);
        insertAssociationBetweenSetAndFlashcard(setFlashcard);
    }

    private void insertAssociationBetweenSetAndFlashcard(SetFlashcard setFlashcard) {
        flashcardBelongToSetDAO.deleteAllAssociationWithSet(setFlashcard.getSetID());
        FlashcardBelongToSet flashcardBelongToSet = new FlashcardBelongToSet();
        flashcardBelongToSet.setSetID(setFlashcard.getSetID());
        List<Long> wordIDList = setFlashcard.getWordID();
        for (Long wordID : wordIDList) {
            long wordIDToConvert = wordID;
            flashcardBelongToSet.setFlashcardID((int) wordIDToConvert);
            flashcardBelongToSetDAO.insertFlashcardBelongToSet(flashcardBelongToSet);
        }
    }

    private void insertNecessaryFlashcard(SetFlashcard setFlashcard) {
        List<Long> wordIDList = setFlashcard.getWordID();
        for (Long wordID : wordIDList) {
            long wordIDToConvert = wordID;
            Flashcard flashcard = new Flashcard();
            flashcard.setFlashcardID((int) wordIDToConvert);
            flashcard.setLastState(1);
            flashcard.setAccuracy(0);
            flashcard.setSpeedPerCharacter(10);
            flashcard.setKanaLength(wordDAO.getOneWord((int) wordIDToConvert).getKana().length());
            flashcardDAO.insertFlashcard(flashcard);
        }
    }

    private int getSetIDFromOnlineID(SetFlashcard setFlashcard) {
        return setFlashcardDAO.getSetWithOnlineID(setFlashcard.getOnlineID())[0].getSetID();
    }

    private boolean hasThisSetInSQLite(SetFlashcard setFlashcard) {
        return setFlashcardDAO.getSetWithOnlineID(setFlashcard.getOnlineID()).length != 0;
    }

    private SetFlashcard getSetFlashcardFromUser(String name) {
        SetFlashcard result = new SetFlashcard();
        result.setCreatorID(userDAO.getLocalUser()[0].getUserID());
        result.setPublic(false);
        try {
            result.setSetID(setFlashcardDAO.getLastSet()[0].getSetID() + 1);
        } catch (Exception e) {
            result.setSetID(1);
        }
        result.setSetName(name);
        result.setWordID(new ArrayList<Long>());
        return result;
    }

    public void createNewSet(String setName, List<Word> listOfWord, boolean isPublic) {
        SetFlashcard setFlashcardFromUser = getSetFlashcardFromUser(setName);
        List<Long> listWordID = getListWordID(listOfWord);
        setFlashcardFromUser.setWordID(listWordID);
        setFlashcardFromUser.setPublic(isPublic);
        setFlashcardDAO.insertSetFlashcard(setFlashcardFromUser);
        insertNecessaryFlashcard(setFlashcardFromUser);
        insertAssociationBetweenSetAndFlashcard(setFlashcardFromUser);
        if (isPublic) {
            uploadSetToFirebase(setFlashcardFromUser);
        }
    }

    private List<Long> getListWordID(List<Word> listOfWord) {
        List<Long> listWordID = new ArrayList<>();
        for (Word word : listOfWord) {
            listWordID.add((long) (word.getWordID()));
        }
        return listWordID;
    }

    public void updateSet(SetFlashcard setFlashcard, String setName, boolean newPublicStatus, List<Word> listOfWord) {
        List<Long> listWordID = getListWordID(listOfWord);
        setFlashcard.setSetName(setName);
        setFlashcard.setWordID(listWordID);
        if (setFlashcard.isPublic()) {
            if (newPublicStatus) {
                updateSetToFireBase(setFlashcard);
            } else {
                makeSetPrivate(setFlashcard);
            }
        } else {
            if (newPublicStatus) {
                uploadSetToFirebase(setFlashcard);
            } else {
                updateSetToSQL(setFlashcard);
            }
        }
    }

    private void updateSetToSQL(SetFlashcard setFlashcard) {
        setFlashcardDAO.updateSetFlashcard(setFlashcard);
        insertNecessaryFlashcard(setFlashcard);
        insertAssociationBetweenSetAndFlashcard(setFlashcard);
    }

    private void updateSetToFireBase(final SetFlashcard setFlashcard) {
        firebaseFirestore.collection(COLLECTION_PATH).document(setFlashcard.getOnlineID())
                .update(mapping(setFlashcard)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateSetToSQL(setFlashcard);
                Log.d("SF_CONTROLLER", "Update successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("SF_CONTROLLER", "Update failed");
            }
        });
    }
}
