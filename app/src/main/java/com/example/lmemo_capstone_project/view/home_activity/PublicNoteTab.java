package com.example.lmemo_capstone_project.view.home_activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lmemo_capstone_project.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicNoteTab extends Fragment {

    public PublicNoteTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_public_note_tab, container, false);
        return view;
    }
}
