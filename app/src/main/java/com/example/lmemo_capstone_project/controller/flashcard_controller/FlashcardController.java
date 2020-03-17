package com.example.lmemo_capstone_project.controller.flashcard_controller;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.flashcard_view.FlashcardInfoFragment;

import java.util.List;

public class FlashcardController {

    private Context aContext;
    private List<Word> listFlashcard;
    private FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(aContext).flashcardDAO();

    public FlashcardController(Context aContext, List<Word> listFlashcard) {
        this.aContext = aContext;
        this.listFlashcard = listFlashcard;
    }

    /**
     * @param position: これはフラッシュカードのポジションです。
     *この関数フラッシュカードのポジションに基づいて削除します。
     */
    public void delete(int position) {
        Flashcard flashcard = flashcardDAO.getFlashCardByID(listFlashcard.get(position).getWordID())[0];
        flashcard.setLastState(99);
        flashcardDAO.updateFlashcard(flashcard);
    }


    /**
     * @param position: これはフラッシュカードのポジションです。
     * @param v　
     * この関数はフラッシュカードのインフォメイションを表示します。
     */
    public void flashcardInfo(int position, View v) {
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        WordDAO wordDAO = LMemoDatabase.getInstance(aContext).wordDAO();
        Word flashcardDetail = wordDAO.getAWords(listFlashcard.get(position).getKana())[0];
        Bundle bundle = new Bundle();
        FlashcardInfoFragment infoFragment = new FlashcardInfoFragment();
        bundle.putSerializable("wordResult", flashcardDetail);
        infoFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.FrameFlashcard, infoFragment, "flashcard");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
