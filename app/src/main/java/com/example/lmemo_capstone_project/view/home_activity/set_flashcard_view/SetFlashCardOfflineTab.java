package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lmemo_capstone_project.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetFlashCardOfflineTab extends Fragment {

    public SetFlashCardOfflineTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_flash_card_offline_tab, container, false);
    }
}
