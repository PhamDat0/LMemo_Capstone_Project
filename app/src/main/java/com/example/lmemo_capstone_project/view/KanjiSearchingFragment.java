package com.example.lmemo_capstone_project.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.model.room_db_entity.Kanji;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class KanjiSearchingFragment extends Fragment {

    public KanjiSearchingFragment() {
        // Required empty public constructor
    }
    private List<Kanji> listKanji;
    private ListView kanjiListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kanji_searching, container, false);
        listKanji = new ArrayList<>();
        getKanji();
        kanjiListView = view.findViewById(R.id.kanjiListView);
//        kanjiListView.setAdapter(new KanjiListAdapter(KanjiSearchingFragment.this,listKanji));
        return view;
    }
    public void getKanji(){


    }
}
