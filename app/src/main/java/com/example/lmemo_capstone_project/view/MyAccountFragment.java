package com.example.lmemo_capstone_project.view;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lmemo_capstone_project.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {


    public MyAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        return view;
    }

}
