package com.example.lmemo_capstone_project.view.home_activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {


    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        return view;
    }


}
