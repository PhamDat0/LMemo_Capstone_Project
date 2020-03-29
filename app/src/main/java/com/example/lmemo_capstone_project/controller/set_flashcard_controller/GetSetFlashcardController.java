package com.example.lmemo_capstone_project.controller.set_flashcard_controller;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardBelongToSetDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.SetFlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.FlashcardBelongToSet;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view.SetFlashCardFragment;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class GetSetFlashcardController {

    private UserDAO userDAO;
    private FlashcardBelongToSetDAO flashcardBelongToSetDAO;
    private SetFlashCardFragment setFlashCardFragment;
    private SetFlashcardDAO setFlashcardDAO;

    public GetSetFlashcardController(SetFlashCardFragment setFlashCardFragment) {
        this.setFlashCardFragment = setFlashCardFragment;
        LMemoDatabase dbInstance = LMemoDatabase.getInstance(setFlashCardFragment.getContext());
        setFlashcardDAO = dbInstance.setFlashcardDAO();
        userDAO = dbInstance.userDAO();
        flashcardBelongToSetDAO = dbInstance.flashcardBelongToSetDAO();
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

    public void getOnlineSet() {

    }
}
