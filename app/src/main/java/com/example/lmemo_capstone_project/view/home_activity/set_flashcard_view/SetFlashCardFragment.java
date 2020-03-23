package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetFlashCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetFlashCardFragment extends Fragment {

    public SetFlashCardFragment() {
        // Required empty public constructor
    }

    public static SetFlashCardFragment newInstance(String param1, String param2) {
        SetFlashCardFragment fragment = new SetFlashCardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_flash_card, container, false);

        return view;
    }
}
