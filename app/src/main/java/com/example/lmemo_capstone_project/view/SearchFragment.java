package com.example.lmemo_capstone_project.view;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    private Button tabWord, tabKanji;
    private AutoCompleteTextView edtSearch;
    private LMemoDatabase lMemoDatabase = LMemoDatabase.getInstance(getContext());
    private WordDAO wordDAO = lMemoDatabase.wordDAO();
    private Bundle bundle = new Bundle();

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Make "Search" keyboard
        edtSearch = ((AutoCompleteTextView) view.findViewById(R.id.txtSearch));

//        String searchWord = edtSearch.getText().toString();
//        Word[] word = wordDAO.getWords(searchWord);
//        ArrayAdapter adapterWords = new ArrayAdapter(this,android.R.layout.simple_list_item_1,word);

//        edtSearch.setThreshold(1);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //Viet ham search vao day
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        tabKanji = (Button) view.findViewById(R.id.tabKanji);
        tabWord = (Button) view.findViewById(R.id.tabWord);

        //set on click cho 2 tab
        tabKanji.setOnClickListener(this);
        tabWord.setOnClickListener(this);

        return view;

    }

    private Word performSearch() {
        String searchWord = edtSearch.getText().toString();
        Word word = new Word();
        try {
            word = wordDAO.getAWord(searchWord)[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            word = new Word(-1,"Not Found","Not Found","Not Found","Not Found");
        }

        return word;

        //test log
//        for (String key: bundle.keySet()) {
//            Log.d ("myApplication", key + " is a key in the bundle");
//        }
//        Log.i("Test Search", "get item " + word.getMeaning() + "||" + word.getKana() + "||" +
//                word.getPartOfSpeech() + "||" +word.getKanjiWriting());
    }


    //onclick listener cho 2 tab word + kanji
    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Word word = performSearch();
        switch (v.getId()){
            case R.id.tabWord:
                performSearch();
                WordSearchingFragment wordFragment = new WordSearchingFragment();
                bundle.putSerializable("result", word);
                wordFragment.setArguments(bundle);
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
