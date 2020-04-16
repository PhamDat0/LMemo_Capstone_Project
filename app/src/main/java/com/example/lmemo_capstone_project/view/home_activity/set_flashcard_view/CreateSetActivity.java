package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.app.Activity;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardBelongToSetDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.search_controller.SearchController;
import com.example.lmemo_capstone_project.controller.search_controller.WordNotFoundException;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.SetFlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.FlashcardBelongToSet;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.example.lmemo_capstone_project.view.home_activity.search_view.AssociatedWordAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreateSetActivity extends AppCompatActivity {

    public static final int IN_ADDING_MODE = 1;
    public static final int IN_EDITING_MODE = 0;
    private AutoCompleteTextView txtWord;
    private EditText txtSetName;
    private Button btnAddSet;
    private Switch isSetPublic;
    private Button btnCancelAddSet;
    private int mode;
    private SetFlashcard setFlashcard;
    private SearchController searchController;
    private AssociatedWordAdapter associatedWordAdapter;
    private SetFlashcardController setFlashcardController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_popup_add_set);
        setUpReference();
        setUpSuggestion();
        setUpAction();
    }

    private void setUpAction() {
        setUpActionForEnterWord();
        setUpActionForButton();
    }

    private void setUpActionForButton() {
        btnAddSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getThisActivity());
                switch (mode) {
                    case IN_ADDING_MODE:
                        addSetHandle();
                        break;
                    case IN_EDITING_MODE:
                        editSetHandle();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "There's no such that mode", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCancelAddSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getThisActivity());
                waitToFinish();
            }
        });
    }

    public void waitToFinish() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog instance = ProgressDialog.getInstance();
                while (instance.isShowing()) {
                }
                finish();
            }
        });
        thread.start();
    }

    private Activity getThisActivity() {
        return this;
    }

    private void editSetHandle() {
        List<Word> listOfWord = associatedWordAdapter.getListOfWord();
        if (listOfWord.size() < 1) {
            Toast.makeText(getApplicationContext(), "Set must have at least 1 card", Toast.LENGTH_LONG).show();
        } else {
            if (!txtSetName.getText().toString().isEmpty()) {
                if (setFlashcard.isPublic() || isSetPublic.isChecked()) {
                    if (InternetCheckingController.isOnline(getApplicationContext())) {
                        ProgressDialog.getInstance().show(CreateSetActivity.this);
                        performEditSet();
                    } else {
                        Toast.makeText(getApplicationContext(), "There is no internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    performEditSet();
                }
            } else {
                txtSetName.setError("Please enter note");
            }
        }
    }

    private void performEditSet() {
        setFlashcardController.updateSet(setFlashcard, txtSetName.getText().toString(), isSetPublic.isChecked(), associatedWordAdapter.getListOfWord());
        waitToFinish();
//        NoteController editAndDeleteNoteController = new NoteController(this);
//        editAndDeleteNoteController.updateNote(setFlashcard, txtSetName.getText().toString(), isSetPublic.isChecked(), associatedWordAdapter.getListOfWord());
    }

    private void addSetHandle() {
        List<Word> listOfWord = associatedWordAdapter.getListOfWord();
        if (listOfWord.size() < 1) {
            Toast.makeText(getApplicationContext(), "Set must have at least 1 card", Toast.LENGTH_LONG).show();
        } else {
            try {
                if (!txtSetName.getText().toString().isEmpty()) {
                    if (!isSetPublic.isChecked()) {
                        setFlashcardController.createNewSet(txtSetName.getText().toString(), listOfWord, isSetPublic.isChecked());
                        Toast.makeText(getApplicationContext(), "Add set successful", Toast.LENGTH_LONG).show();
                        waitToFinish();
                    } else if (InternetCheckingController.isOnline(getApplicationContext())) {
                        ProgressDialog.getInstance().show(CreateSetActivity.this);
                        setFlashcardController.createNewSet(txtSetName.getText().toString(), listOfWord, isSetPublic.isChecked());
                        Toast.makeText(getApplicationContext(), "Add set successful", Toast.LENGTH_LONG).show();
                        waitToFinish();
                    } else {
                        Toast.makeText(getApplicationContext(), "There is no internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    txtSetName.setError("Please enter note");
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setUpActionForEnterWord() {
        txtWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtWord.setText("");
            }
        });
        txtWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    try {
                        addSearchWordToList();
                        hideKeyboard(getThisActivity());
                    } catch (WordNotFoundException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });

        txtWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    addSearchWordToList();
                    hideKeyboard(getThisActivity());
                } catch (WordNotFoundException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setUpSuggestion() {

        txtWord.setThreshold(1);
        String[] from = {"name"};
        int[] to = {android.R.id.text1};
        //create a simple cursorAdapter
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getApplicationContext(),
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
        txtWord.setAdapter(cursorAdapter);

    }

    private void addSearchWordToList() throws WordNotFoundException {
        String searchWord = txtWord.getText().toString();
        Word word = searchController.performSearch(searchWord);
        addToListView(word);
    }

    private void addToListView(Word word) {
//        Log.d("SetUpAdapter", "success");
        associatedWordAdapter.addWordToList(word);
        if (!LMemoDatabase.getInstance(getApplicationContext()).userDAO().getLocalUser()[0].isGuest()) {
            if (associatedWordAdapter.getCount() >= 10) {
                isSetPublic.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setUpReference() {
        List<Word> listWord = new ArrayList<>();
        txtWord = findViewById(R.id.txtWord);
        txtSetName = findViewById(R.id.txtTakeNote);
        btnAddSet = findViewById(R.id.btnAddNote);
        isSetPublic = findViewById(R.id.isNotePublic);
        btnCancelAddSet = findViewById(R.id.btnCancelAddNote);
        ListView listAssociatedWord = findViewById(R.id.listAssocitatedWord);
        associatedWordAdapter = new AssociatedWordAdapter(this, listWord);
        listAssociatedWord.setAdapter(associatedWordAdapter);
        WordDAO wordDAO = LMemoDatabase.getInstance(getApplicationContext()).wordDAO();
        FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO();
        if (LMemoDatabase.getInstance(getApplicationContext()).userDAO().getLocalUser()[0].isGuest() || listWord.size() < 10) {
            isSetPublic.setVisibility(View.INVISIBLE);
            isSetPublic.setChecked(false);
        }
        searchController = new SearchController(wordDAO, flashcardDAO);
        setFlashcardController = new SetFlashcardController(this);
        mode = getIntent().getIntExtra("mode", IN_ADDING_MODE);
        switch (mode) {
            case IN_ADDING_MODE:
                setFlashcard = new SetFlashcard();
                break;
            case IN_EDITING_MODE:
                setFlashcard = (SetFlashcard) getIntent().getSerializableExtra("set");
                setUpSetForEdit();
                break;
            default:
                throw new UnsupportedOperationException("Wrong mode");
        }
    }

    private void setUpSetForEdit() {
        ((TextView) findViewById(R.id.tvEditSet)).setText("Edit a set");
        txtSetName.setText(setFlashcard.getSetName());
        FlashcardBelongToSetDAO flashcardBelongToSetDAO = LMemoDatabase.getInstance(getApplicationContext()).flashcardBelongToSetDAO();
        FlashcardBelongToSet[] flashcardBelongToSets = flashcardBelongToSetDAO.getFlashcardBySetID(setFlashcard.getSetID());
        WordDAO wordDAO = LMemoDatabase.getInstance(getApplicationContext()).wordDAO();
        for (FlashcardBelongToSet flashcardInSet : flashcardBelongToSets) {
            Log.i("READ_HAS_NO_DEFECTS", flashcardInSet.getFlashcardID() + "");
            addToListView(wordDAO.getWordWithID(flashcardInSet.getFlashcardID())[0]);
        }
        isSetPublic.setChecked(setFlashcard.isPublic());
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

    public void warningPublicSetLowerThan10Card() {
        if (isSetPublic.isChecked()) {
            Toast.makeText(getApplicationContext(), "Set lower than 10 cards is automatically change to private", Toast.LENGTH_LONG).show();
            isSetPublic.setChecked(false);
        }
        isSetPublic.setVisibility(View.INVISIBLE);
    }
}
