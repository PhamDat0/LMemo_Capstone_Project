package com.example.lmemo_capstone_project.view;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lmemo_capstone_project.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    Button tabWord,tabKanji;
    EditText edtSearch;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Make "Search" keyboard
        edtSearch = (EditText)view.findViewById(R.id.edtSearch);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//
//                    //Viet ham search vao day
////                    performSearch();
//                    return true;
//                }
                return false;
            }
        });

        tabKanji = (Button)view.findViewById(R.id.tabKanji);
        tabWord = (Button)view.findViewById(R.id.tabWord);

        //set on click cho 2 tab
        tabKanji.setOnClickListener(this);
        tabWord.setOnClickListener(this);

        return view;

    }

    //onclick listener cho 2 tab word + kanji
    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

        switch (v.getId()){
            case R.id.tabWord:
                WordSearchingFragment wordFragment = new WordSearchingFragment();
                fragmentTransaction.replace(R.id.searchFrameLayout,wordFragment,"SearchWord");
                fragmentTransaction.commit();
                break;
            case R.id.tabKanji:
                KanjiSearchingFragment kanjiFragment = new KanjiSearchingFragment();
                fragmentTransaction.replace(R.id.searchFrameLayout,kanjiFragment,"SearchKanji");
                fragmentTransaction.commit();
                break;
        }

    }


}
