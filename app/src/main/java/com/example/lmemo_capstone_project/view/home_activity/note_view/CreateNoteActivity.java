package com.example.lmemo_capstone_project.view.home_activity.note_view;

import android.app.Activity;
import android.content.Intent;
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
import com.example.lmemo_capstone_project.controller.CannotPerformFirebaseRequest;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteOfWordDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.note_controller.NoteController;
import com.example.lmemo_capstone_project.controller.search_controller.SearchController;
import com.example.lmemo_capstone_project.controller.search_controller.WordNotFoundException;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;
import com.example.lmemo_capstone_project.view.home_activity.search_view.AssociatedWordAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreateNoteActivity extends AppCompatActivity {

    public static final int IN_ADDING_MODE = 1;
    public static final int IN_EDITING_MODE = 0;
    private AutoCompleteTextView txtWord;
    private EditText txtTakeNote;
    private Button btnAddNote;
    private Switch isNotePublic;
    private Button btnCancelAddNote;
    private ListView listAssocitatedWord;
    private int mode;
    private Word word;
    private Note note;
    private SearchController searchController;
    private List<Word> listWord;
    private AssociatedWordAdapter associatedWordAdapter;
    private NoteController addNoteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_popup_add_note);
        setUpReference();
        setUpSuggestion();
        setUpAction();
    }

    private void setUpAction() {
        setUpActionForEnterWord();
        setUpActionForButton();
    }

    private void setUpActionForButton() {
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getThisActivity());
                switch (mode) {
                    case IN_ADDING_MODE:
                        addNoteHandle();
                        break;
                    case IN_EDITING_MODE:
                        editNoteHandle();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "There's no such that mode", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCancelAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getThisActivity());
                finish();
            }
        });
    }

    private Activity getThisActivity() {
        return this;
    }

    private void editNoteHandle() {
        if (!txtTakeNote.getText().toString().isEmpty()) {
            if (note.isPublic() || isNotePublic.isChecked()) {
                if (InternetCheckingController.isOnline(getApplicationContext())) {
                    ProgressDialog.getInstance().show(CreateNoteActivity.this);
                    performEditNote();
                    setRequestCodeAndFinish(HomeActivity.EDIT_NOTE_REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "There is no internet", Toast.LENGTH_LONG).show();
                }
            } else {
                performEditNote();
                setRequestCodeAndFinish(HomeActivity.EDIT_NOTE_REQUEST_CODE);
            }
        } else {
            txtTakeNote.setError("Please enter note");
        }
    }

    private void performEditNote() {
        NoteController editAndDeleteNoteController = new NoteController(this);
        editAndDeleteNoteController.updateNote(note, txtTakeNote.getText().toString(), isNotePublic.isChecked(), associatedWordAdapter.getListOfWord());
    }

    private void addNoteHandle() {
        try {
            if (!txtTakeNote.getText().toString().isEmpty()) {
                if (!isNotePublic.isChecked()) {
                    addNoteController.getNoteFromUI(associatedWordAdapter.getListOfWord(), txtTakeNote.getText().toString(), isNotePublic.isChecked());
                    Toast.makeText(getApplicationContext(), "Add note successful", Toast.LENGTH_LONG).show();
                    setRequestCodeAndFinish(HomeActivity.ADD_NOTE_REQUEST_CODE);
                } else if (InternetCheckingController.isOnline(getApplicationContext())) {
                    ProgressDialog.getInstance().show(CreateNoteActivity.this);
                    addNoteController.getNoteFromUI(associatedWordAdapter.getListOfWord(), txtTakeNote.getText().toString(), isNotePublic.isChecked());
                    Toast.makeText(getApplicationContext(), "Add note successful", Toast.LENGTH_LONG).show();
                    setRequestCodeAndFinish(HomeActivity.ADD_NOTE_REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "There is no internet", Toast.LENGTH_LONG).show();
                }
            } else {
                txtTakeNote.setError("Please enter note");
            }
        } catch (Exception | CannotPerformFirebaseRequest e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
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

    private void setRequestCodeAndFinish(int addNoteRequestCode) {
        Intent intent = new Intent();
        if (word != null) {
            intent.putExtra("wordID", word.getWordID());
        }
        setResult(addNoteRequestCode, intent);
        waitToFinish();
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
    }

    private void setUpReference() {
        listWord = new ArrayList<>();
        txtWord = findViewById(R.id.txtWord);
        txtTakeNote = findViewById(R.id.txtTakeNote);
        btnAddNote = findViewById(R.id.btnAddNote);
        isNotePublic = findViewById(R.id.isNotePublic);
        btnCancelAddNote = findViewById(R.id.btnCancelAddNote);
        listAssocitatedWord = findViewById(R.id.listAssocitatedWord);
        associatedWordAdapter = new AssociatedWordAdapter(this, listWord);
        listAssocitatedWord.setAdapter(associatedWordAdapter);
        WordDAO wordDAO = LMemoDatabase.getInstance(getApplicationContext()).wordDAO();
        FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO();
        if (LMemoDatabase.getInstance(getApplicationContext()).userDAO().getLocalUser()[0].getUserID().equalsIgnoreCase("GUEST")) {
            isNotePublic.setVisibility(View.INVISIBLE);
        }
        searchController = new SearchController(wordDAO, flashcardDAO);
        addNoteController = new NoteController(this);
        mode = getIntent().getIntExtra("mode", IN_ADDING_MODE);
        switch (mode) {
            case IN_ADDING_MODE:
                word = (Word) getIntent().getSerializableExtra("word");
                addToListView(word);
                note = new Note();
                break;
            case IN_EDITING_MODE:
                note = (Note) getIntent().getSerializableExtra("note");
                setUpNoteForEdit();
                break;
            default:
                throw new UnsupportedOperationException("Wrong mode");
        }
    }

    private void setUpNoteForEdit() {
        ((TextView) findViewById(R.id.tvCreateANoteTitle)).setText(R.string.EditNoteTitle);
        txtTakeNote.setText(note.getNoteContent());
        NoteOfWordDAO noteOfWordDAO = LMemoDatabase.getInstance(getApplicationContext()).noteOfWordDAO();
        NoteOfWord[] notesOfWord = noteOfWordDAO.getNoteOfWord(note.getNoteID());
        WordDAO wordDAO = LMemoDatabase.getInstance(getApplicationContext()).wordDAO();
        for (NoteOfWord noteOfWord : notesOfWord) {
            Log.i("READ_HAS_NO_DEFECTS", noteOfWord.getWordID() + "");
            addToListView(wordDAO.getWordWithID(noteOfWord.getWordID())[0]);
        }
        isNotePublic.setChecked(note.isPublic());
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
}
