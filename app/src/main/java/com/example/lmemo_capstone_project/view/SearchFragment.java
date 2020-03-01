package com.example.lmemo_capstone_project.view;


import android.app.Activity;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.SyncStateContract;
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
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    private Button tabWord, tabKanji;
    private AutoCompleteTextView edtSearch;
    private LMemoDatabase lMemoDatabase = LMemoDatabase.getInstance(getContext());
    private WordDAO wordDAO = lMemoDatabase.wordDAO();
    private FlashcardDAO flashcardDAO = lMemoDatabase.flashcardDAO();
    private Bundle bundle = new Bundle();
    private Word word;
    private Flashcard flashcard;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        edtSearch = ((AutoCompleteTextView) view.findViewById(R.id.txtSearch));
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

        tabKanji = (Button) view.findViewById(R.id.tabKanji);
        tabWord = (Button) view.findViewById(R.id.tabWord);

        //set on click cho 2 tab
        tabKanji.setOnClickListener(this);
        tabWord.setOnClickListener(this);

        return view;
    }

    //Suggestion word Function
    private void performSuggestion() {
//        final String searchWord = edtSearch.getText().toString();
//        String s = searchWord.replace("*", "%");
//        s = s.replace("?", "_");
//        if((!s.contains("%"))&&(!s.contains("_"))) {
//            s += "%";
//        }
//        Log.i("STRING",s);
//        final String[] kanji = wordDAO.getKanji(s);
//        Log.i("Result_Length",kanji.length+"");

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
                String constrain = (String) constraint;
                constrain = constrain.replace("*", "%");
                constrain = constrain.replace("?", "_");
                if ((!constrain.contains("%")) && (!constrain.contains("_"))) {
                    constrain += "%";
                }
//                constrain = constrain.replace("*","%");
//                constrain = constrain.replace("?","_");
                constrain = constrain.toUpperCase();
                if (constraint == null) {
                    return null;
                }

                String[] columns = {SyncStateContract.Columns._ID, "name"};
                MatrixCursor c = new MatrixCursor(columns);
                try {
                    //when a list item contains the user input, add that to the Matrix Cursor
                    //this matrix cursor will be returned and the contents will be displayed
                    Word[] kanji = wordDAO.getSuggestion(constrain);
                    for (int i = 0; i < kanji.length; i++) {
                        c.newRow().add(i).add(kanji[i].getKanjiWriting().length() == 0 ? kanji[i].getKana() : kanji[i].getKanjiWriting());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return c;
            }
        };
        //use the filter query provider on the cursor adapter
        cursorAdapter.setFilterQueryProvider(provider);
        edtSearch.setAdapter(cursorAdapter);
    }


    //Perform Search desire word in database
    private Word performSearch() {
        String searchWord = edtSearch.getText().toString();
        if (searchWord.trim().length() == 0)
            return new Word(-2, "", "", "", "");
//        Word word;
        try {
            Word[] words = wordDAO.getWords(searchWord);
            word = bestFit(words, searchWord);
        } catch (ArrayIndexOutOfBoundsException e) {
            word = new Word(-1, "Not Found", "Not Found", "Not Found", "Not Found");
        }
//        Log.d ("myApplication",  "id:"+ );

        return word;

    }

    private Word bestFit(Word[] words, String searchWord) {
        Word result = words[0];

        for (Word word : words) {
            if (checkForBestFit(word, searchWord)) {
                result = word;
                break;
            }
        }

        return result;
    }

    private boolean checkForBestFit(Word word, String searchWord) {
        String[] kanjiWritings = word.getKanjiWriting().split("/");
        String[] kanaWritings = word.getKana().split("/");
        String[] meanings = word.getMeaning().split("/");
        for (String kanjiWriting : kanjiWritings) {
            if (searchWord.equals(kanjiWriting.trim()))
                return true;
        }
        for (String kanaWriting : kanaWritings) {
            if (searchWord.equals(kanaWriting.trim()))
                return true;
        }
        for (String meaning : meanings) {
            if (searchWord.equals(meaning.trim()))
                return true;
        }
        return false;
    }

    //Transfer data between fragment
    private void fragmentDataTransfer(Word word) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        WordSearchingFragment wordFragment = new WordSearchingFragment();
        bundle.putSerializable("result", word);
        wordFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.searchFrameLayout, wordFragment, "SearchWord");
        fragmentTransaction.commit();
    }

    //Add searched word to flashcard
    private void addToFlashCard() {
        flashcard = new Flashcard();
        Word word = performSearch();
        if (word.getWordID() != -1) {
            Flashcard[] checkingID = flashcardDAO.checkID(word.getWordID());
            if (checkingID == null) {
                flashcard.setFlashcardID(word.getWordID());
                flashcard.setAccuracy(0);
                flashcard.setSpeedPerCharacter(10);
                flashcard.setLastState(1);
                flashcard.setKanaLength(word.getKana().length());
                flashcardDAO.insertFlashcard(flashcard);
            }
        }
//        Log.d ("myApplication",  "id:"+ flashcardDAO.getAllFlashcard().toString());

    }

    //onclick listener for 2 tab word + kanji
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabWord:
//                performSearch();
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
        Word word = performSearch();
        if (word.getWordID() == -1)
            throw new WordNotFoundException("That word does not exist.");
        if (word.getWordID() == -2)
            throw new WordNotFoundException("You didn't enter anything.");
        fragmentDataTransfer(word);
        addToFlashCard();
        edtSearch.dismissDropDown();
        hideKeyboard(getActivity());
    }

    private class WordNotFoundException extends Exception {
        public WordNotFoundException(String s) {
            super(s);
        }
    }
}
