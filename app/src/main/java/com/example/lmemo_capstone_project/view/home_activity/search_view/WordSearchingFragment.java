package com.example.lmemo_capstone_project.view.home_activity.search_view;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.note_controller.GetNoteController;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.note_view.CreateNoteDialog;
import com.example.lmemo_capstone_project.view.home_activity.note_view.NoteListAdapter;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordSearchingFragment extends Fragment {
    private Dialog addNoteDialog;
    private Button btnOpenTakeNoteDialog;
    private ListView noteListView;
    private int wordID;
    private TextToSpeech textToSpeech;
    private Spinner spinnerSort;

    public WordSearchingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_searching, container, false);
        noteListView = view.findViewById(R.id.NoteListView);
        btnOpenTakeNoteDialog = view.findViewById(R.id.btnOpenTakeNoteDialog);
        addListenerOnSpinnerItemSelection(view);
        wordSearchResult(view);
        addNoteDialog = new Dialog(this.getActivity());
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.JAPAN);
                }
            }
        });
        btnOpenTakeNoteDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteDialog(v);
            }
        });
//        loadPublicNote();
        return view;
    }

    private void showAddNoteDialog(View v) {
//        final AddNoteController addNoteController = new AddNoteController(this.getActivity());
//        addNoteDialog.setContentView(R.layout.demo_popup_add_note);
//        final EditText txtNoteContent = (EditText) addNoteDialog.findViewById(R.id.txtTakeNote);
//        TextView txtWord = (TextView) addNoteDialog.findViewById(R.id.txtWord);
//        Button btnSave = (Button) addNoteDialog.findViewById(R.id.btnAddNote);
//        Button btnCancel = (Button) addNoteDialog.findViewById(R.id.btnCancelAddNote);
//        final Switch isPublic = (Switch) addNoteDialog.findViewById(R.id.isNotePublic);
//        final Word word = getWord();
//        txtWord.setText(word.getKana());
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    if (!txtNoteContent.getText().toString().isEmpty()) {
//                        addNoteController.getNoteFromUI(word.getWordID(), txtNoteContent.getText().toString(), isPublic.isChecked());
//                        Toast.makeText(getContext(), "Add note successful", Toast.LENGTH_LONG).show();
//                        addNoteDialog.dismiss();
//                        loadPublicNote();
//                    } else {
//                        txtNoteContent.setError("Please enter note");
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                } catch (CannotPerformFirebaseRequest e) {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addNoteDialog.dismiss();
//            }
//        });
//        addNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        addNoteDialog.show();
        FragmentManager fm = getActivity().getSupportFragmentManager();

        CreateNoteDialog editNameDialogFragment = CreateNoteDialog.newDialogForAdding(getWord());

        editNameDialogFragment.show(fm, "Add note");
    }

    private void wordSearchResult(View container) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            btnOpenTakeNoteDialog.setVisibility(View.VISIBLE);
            final Word word = (Word) bundle.getSerializable("result");
            wordID = word.getWordID();
            ((TextView) container.findViewById(R.id.tvKana)).setText("[ " + word.getKana() + " ]");
            ((TextView) container.findViewById(R.id.tvKanji)).setText("  " + word.getKanjiWriting());
            ((TextView) container.findViewById(R.id.tvMeaning)).setText(" . " + word.getMeaning().replace("\n", "\n . "));
            ((TextView) container.findViewById(R.id.tvPartOfSpeech)).setText(" * " + word.getPartOfSpeech());
            ImageButton btPronunciation = container.findViewById(R.id.btPronunciation);
            btPronunciation.setVisibility(View.VISIBLE);
            spinnerSort.setVisibility(View.VISIBLE);
            btPronunciation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech.speak(word.getKana().split("/")[0].trim(), TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
        } else {
            Log.d("myApplication", " no key in bundle");
            btnOpenTakeNoteDialog.setVisibility(View.INVISIBLE);
            spinnerSort.setVisibility(View.INVISIBLE);
        }
        loadPublicNote();
    }

    private Word getWord() {
        Bundle bundle = getArguments();
        Word word = (Word) bundle.getSerializable("result");
        return word;
    }

    private void loadPublicNote() {
        final GetNoteController getNoteController = new GetNoteController(this);
//        getPublicNoteController.getAllNoteAscendingFromFirebase(noteListView, getActivity(), wordID);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerSort.getSelectedItem().equals(parent.getItemAtPosition(0)) && InternetCheckingController.isOnline(getContext())) {
                    getNoteController.getAllNoteAscendingFromFirebase(noteListView, getActivity(), wordID);
                } else if (spinnerSort.getSelectedItem().equals(parent.getItemAtPosition(1)) && InternetCheckingController.isOnline(getContext())) {
                    getNoteController.getAllNoteDescendingFromFirebase(noteListView, getActivity(), wordID);
                } else if (spinnerSort.getSelectedItem().equals(parent.getItemAtPosition(2)) && InternetCheckingController.isOnline(getContext())) {
                    //
                } else if (spinnerSort.getSelectedItem().equals(parent.getItemAtPosition(3)) && InternetCheckingController.isOnline(getContext())) {
                    //
                } else {
                    Toast.makeText(getContext(), "No Internet connection [Offline Mode]", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //callback
            }
        });
    }

    public void updateUI(ArrayList<Note> listNote, Map<String, User> listUserMap) {
        Log.i("Note_And_User", listNote.get(0).getOnlineID() + " / " +
                listUserMap.get(listNote.get(0).getCreatorUserID()).getUserID() + " ; ");
        NoteListAdapter noteListAdapter = new NoteListAdapter(getActivity(), listNote, listUserMap);
        noteListView.setAdapter(noteListAdapter);
    }

    private void addListenerOnSpinnerItemSelection(View v) {
        spinnerSort = v.findViewById(R.id.spinnerSort);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerSort.setAdapter(adapter);
    }
}