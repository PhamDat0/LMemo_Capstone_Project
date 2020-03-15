package com.example.lmemo_capstone_project.view.home_activity;


import android.app.Activity;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.controller.search_controller.SearchController;
import com.example.lmemo_capstone_project.controller.search_controller.WordNotFoundException;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    private AutoCompleteTextView edtSearch;
    private WordDAO wordDAO;
    private Bundle bundle = new Bundle();
    private int wordID = -1;
    private SearchController searchController;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        LMemoDatabase lMemoDatabase = LMemoDatabase.getInstance(getContext());
        wordDAO = lMemoDatabase.wordDAO();
        FlashcardDAO flashcardDAO = lMemoDatabase.flashcardDAO();
        searchController = new SearchController(wordDAO, flashcardDAO);
        edtSearch = view.findViewById(R.id.txtSearch);
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });
        performSuggestion();

        //Make "Search" keyboard
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    try {
                        getSearchResult();
                    } catch (WordNotFoundException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });

        edtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    getSearchResult();
                } catch (WordNotFoundException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Button tabKanji = view.findViewById(R.id.tabKanji);
        Button tabWord = view.findViewById(R.id.tabWord);

        //set on click cho 2 tab
        tabKanji.setOnClickListener(this);
        tabWord.setOnClickListener(this);
        getDailyWord();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            wordID = getArguments().getInt("wordID");
            Log.w("Search Fragment", "word id is" + wordID);
        }
    }

    private void getDailyWord() {
        if (wordID != -1) {
            Word dailyWord = wordDAO.getOneWord(wordID);
            searchController.addWordToFlashcard(dailyWord);
            fragmentDataTransfer(dailyWord);
            wordID = -1;
        }
    }
    //Suggestion word Function


    /**
     * この関数は日本語の言葉でSQLiteデータベースを勧めます。
     */
    private void performSuggestion() {
        //set threshold for suggestion show up
        edtSearch.setThreshold(1);
        String[] from = {"name"};
        int[] to = {android.R.id.text1};
        //create a simple cursorAdapter
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_dropdown_item_1line, null, from, to, 0);
        cursorAdapter.setStringConversionColumn(1);
        //create the filter query provider
        FilterQueryProvider provider = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String[] columns = {SyncStateContract.Columns._ID, "name"};
                MatrixCursor c = new MatrixCursor(columns);
                return searchController.insertSuggestion(constraint, c);
            }
        };
        //use the filter query provider on the cursor adapter
        cursorAdapter.setFilterQueryProvider(provider);
        edtSearch.setAdapter(cursorAdapter);
    }

    /**
     * @param word この関数は2つのフラグメントのデータを交換します。
     */
    private void fragmentDataTransfer(Word word) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        WordSearchingFragment wordFragment = new WordSearchingFragment();
        bundle.putSerializable("result", word);
        wordFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.searchFrameLayout, wordFragment, "SearchWord");
        fragmentTransaction.commit();
    }

    //onclick listener for 2 tab word + kanji
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabWord:
                try {
                    getSearchResult();
                } catch (WordNotFoundException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tabKanji:
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                KanjiSearchingFragment kanjiFragment = new KanjiSearchingFragment(edtSearch.getText().toString());
                fragmentTransaction.replace(R.id.searchFrameLayout, kanjiFragment, "SearchKanji");
                fragmentTransaction.commit();
                hideKeyboard(getActivity());
                break;
        }
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void getSearchResult() throws WordNotFoundException {
        String searchWord = edtSearch.getText().toString();
        Word word = searchController.performSearch(searchWord);
        searchController.addWordToFlashcard(word);
        fragmentDataTransfer(word);
        edtSearch.dismissDropDown();
        hideKeyboard(getActivity());
    }
}
