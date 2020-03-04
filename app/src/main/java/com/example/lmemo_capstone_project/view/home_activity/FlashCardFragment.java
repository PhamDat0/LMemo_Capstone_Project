package com.example.lmemo_capstone_project.view.home_activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FlashCardFragment extends Fragment {

    private ArrayList<Word> listFlashcard;
    private ListView flashcardListView;

    public FlashCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flash_card, container, false);
        getAllFlashcard();
        flashcardListView = view.findViewById(R.id.flashcardListView);
        FlashcardListAdapter flashcardAdapter = new FlashcardListAdapter(getActivity(), listFlashcard);
        flashcardListView.setAdapter(flashcardAdapter);
//        flashcardAdapter.notifyDataSetChanged();
//        getFragmentManager().beginTransaction()
//                .add(R.id.FrameFlashcard,new FlashcardInfoFragment()).addToBackStack(null).commit();

        return view;
    }

    /**
     * get all flashcard from database
     * この関数はフラッシュカード（単語帳）を検索、結果のリストにフラッシュカードの情報を追加します
     */
    private void getAllFlashcard() {
        WordDAO wordDAO = LMemoDatabase.getInstance(getContext()).wordDAO();
        Word[] allFlashcard = wordDAO.getAllFlashcard();
        listFlashcard = new ArrayList<>(Arrays.asList(allFlashcard));
    }
}

