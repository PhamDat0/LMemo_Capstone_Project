package com.example.lmemo_capstone_project.view.home_activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.KanjiDAO;
import com.example.lmemo_capstone_project.controller.search_controller.SearchController;
import com.example.lmemo_capstone_project.model.room_db_entity.Kanji;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class KanjiSearchingFragment extends Fragment {

    private List<Kanji> listKanji;
    private ListView kanjiListView;

    public KanjiSearchingFragment() {

    }

    public KanjiSearchingFragment(String enteredWord) {
        listKanji = new ArrayList<>();
        getKanji(enteredWord);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kanji_searching, container, false);
        kanjiListView = view.findViewById(R.id.kanjiListView);
        kanjiListView.setAdapter(new KanjiListAdapter(getActivity(), listKanji));
        return view;
    }

    /**
     * @param enteredWord A string contains the kanji that the application needs to search
     *                    検索する漢字を持っている文字列です。
     *                    この関数は漢字を検索し、結果のリストに漢字の情報を追加します。
     */
    public void getKanji(String enteredWord) {
        KanjiDAO kanjiDAO = LMemoDatabase.getInstance(getContext()).kanjiDAO();
        SearchController searchController = new SearchController(kanjiDAO);
        listKanji = searchController.searchForKanji(enteredWord);
    }
}
