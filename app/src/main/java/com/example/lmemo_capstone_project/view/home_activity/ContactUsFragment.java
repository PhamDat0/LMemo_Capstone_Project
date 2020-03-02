package com.example.lmemo_capstone_project.view.home_activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {
    Button b1;

    public ContactUsFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        return view;
    }

}
