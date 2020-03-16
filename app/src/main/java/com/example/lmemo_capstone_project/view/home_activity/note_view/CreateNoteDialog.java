package com.example.lmemo_capstone_project.view.home_activity.note_view;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

import androidx.fragment.app.DialogFragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.CannotPerformFirebaseRequest;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteOfWordDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.controller.note_controller.AddNoteController;
import com.example.lmemo_capstone_project.controller.search_controller.SearchController;
import com.example.lmemo_capstone_project.controller.search_controller.WordNotFoundException;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.NoteOfWord;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.search_view.AssociatedWordAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreateNoteDialog extends DialogFragment {

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
    private AddNoteController addNoteController;

    public CreateNoteDialog() {

    }

    public static CreateNoteDialog newDialogForAdding(Word word) {
        CreateNoteDialog frag = new CreateNoteDialog();
        Bundle args = new Bundle();
        args.putInt("mode", IN_ADDING_MODE);
        args.putSerializable("word", word);
        frag.setArguments(args);
        return frag;
    }

    public static CreateNoteDialog newDialogForEditing(Note note) {
        CreateNoteDialog frag = new CreateNoteDialog();
        Bundle args = new Bundle();
        args.putInt("mode", IN_EDITING_MODE);
        args.putSerializable("note", note);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.demo_popup_add_note, container, false);
        setUpReference(v);
        setUpSuggestion();
        setUpAction();
        return v;
    }

    private void setUpAction() {
        setUpActionForEnterWord();
        setUpActionForButton();
    }

    private void setUpActionForButton() {
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!txtTakeNote.getText().toString().isEmpty()) {
                        addNoteController.getNoteFromUI(associatedWordAdapter.getListOfWord(), txtTakeNote.getText().toString(), isNotePublic.isChecked());
                        Toast.makeText(getContext(), "Add note successful", Toast.LENGTH_LONG).show();
                        dismiss();
                    } else {
                        txtTakeNote.setError("Please enter note");
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (CannotPerformFirebaseRequest e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCancelAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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
                    } catch (WordNotFoundException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                } catch (WordNotFoundException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setUpSuggestion() {

        txtWord.setThreshold(1);
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

    private void setUpReference(View v) {
        listWord = new ArrayList<>();
        txtWord = v.findViewById(R.id.txtWord);
        txtTakeNote = v.findViewById(R.id.txtTakeNote);
        btnAddNote = v.findViewById(R.id.btnAddNote);
        isNotePublic = v.findViewById(R.id.isNotePublic);
        btnCancelAddNote = v.findViewById(R.id.btnCancelAddNote);
        listAssocitatedWord = v.findViewById(R.id.listAssocitatedWord);
        associatedWordAdapter = new AssociatedWordAdapter(getActivity(), listWord);
        listAssocitatedWord.setAdapter(associatedWordAdapter);
        WordDAO wordDAO = LMemoDatabase.getInstance(getContext()).wordDAO();
        FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(getContext()).flashcardDAO();
        searchController = new SearchController(wordDAO, flashcardDAO);
        addNoteController = new AddNoteController(getActivity());
        mode = getArguments().getInt("mode");
        switch (mode) {
            case IN_ADDING_MODE:
                word = (Word) getArguments().getSerializable("word");
                addToListView(word);
                note = new Note();
                break;
            case IN_EDITING_MODE:
                note = (Note) getArguments().getSerializable("note");
                setUpNoteForEdit();
                break;
            default:
                throw new UnsupportedOperationException("Wrong mode");
        }
    }

    private void setUpNoteForEdit() {
        txtTakeNote.setText(note.getNoteContent());
        NoteOfWordDAO noteOfWordDAO = LMemoDatabase.getInstance(getContext()).noteOfWordDAO();
        NoteOfWord[] notesOfWord = noteOfWordDAO.getNoteOfWord(note.getNoteID());
        WordDAO wordDAO = LMemoDatabase.getInstance(getContext()).wordDAO();
        for (NoteOfWord noteOfWord : notesOfWord) {
            addToListView(wordDAO.getWordWithID(noteOfWord.getWordID())[0]);
        }
        isNotePublic.setChecked(note.isPublic());
    }
}
