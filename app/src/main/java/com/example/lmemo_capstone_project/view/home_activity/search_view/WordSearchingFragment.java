package com.example.lmemo_capstone_project.view.home_activity.search_view;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.note_controller.GetNoteController;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;
import com.example.lmemo_capstone_project.view.home_activity.note_view.CreateNoteActivity;
import com.example.lmemo_capstone_project.view.home_activity.note_view.NoteListAdapter;
import com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view.AddToSetDialog;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordSearchingFragment extends Fragment {
    private ImageButton btnOpenTakeNoteDialog;
    private ImageButton btAddWordToSet;
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
        btAddWordToSet = view.findViewById(R.id.ibAddToSet);
        addListenerOnSpinnerItemSelection(view);
        setUpSpinner();
        wordSearchResult(view);
        spinnerSort.setSelection(2);
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
        btAddWordToSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToSetDialog();
            }
        });
//        loadPublicNote();
        return view;
    }

    private void showAddToSetDialog() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        AddToSetDialog addToSetDialog = AddToSetDialog.newInstance(getWord());
        addToSetDialog.show(ft, "dialog");
    }

    private void setUpSpinner() {
        final GetNoteController getNoteController = GetNoteController.getInstance(this);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerSort.getSelectedItem().equals(parent.getItemAtPosition(0)) && InternetCheckingController.isOnline(getContext())) {
//                    getNoteController.getAllNoteAscendingFromFirebase(noteListView, getActivity(), wordID);
                    getNoteController.getAllNotesFromFirebase(wordID, GetNoteController.TIME_ASC);
                } else if (spinnerSort.getSelectedItem().equals(parent.getItemAtPosition(1)) && InternetCheckingController.isOnline(getContext())) {
//                    getNoteController.getAllNoteDescendingFromFirebase(noteListView, getActivity(), wordID);
                    getNoteController.getAllNotesFromFirebase(wordID, GetNoteController.TIME_DESC);
                } else if (spinnerSort.getSelectedItem().equals(parent.getItemAtPosition(2)) && InternetCheckingController.isOnline(getContext())) {
                    getNoteController.getAllNotesFromFirebase(wordID, GetNoteController.UPVOTE_ASC);
                } else if (spinnerSort.getSelectedItem().equals(parent.getItemAtPosition(3)) && InternetCheckingController.isOnline(getContext())) {
                    getNoteController.getAllNotesFromFirebase(wordID, GetNoteController.UPVOTE_DESC);
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

    private void showAddNoteDialog(View v) {
        Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
        intent.putExtra("mode", CreateNoteActivity.IN_ADDING_MODE);
        intent.putExtra("word", getWord());
        startActivityForResult(intent, HomeActivity.ADD_NOTE_REQUEST_CODE);
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
            Log.d("myApplication", " there is key in bundle");
            loadPublicNote();
        } else {
            Log.d("myApplication", " no key in bundle");
            btnOpenTakeNoteDialog.setVisibility(View.INVISIBLE);
            spinnerSort.setVisibility(View.INVISIBLE);
        }
    }

    private Word getWord() {
        Bundle bundle = getArguments();
        Word word = (Word) bundle.getSerializable("result");
        return word;
    }

    private void loadPublicNote() {
        spinnerSort.setSelection(2);
//        final GetNoteController getNoteController = GetNoteController.getInstance(this);
//        getPublicNoteController.getAllNoteAscendingFromFirebase(noteListView, getActivity(), wordID);
//        getNoteController.getAllNotesFromFirebase(wordID, GetNoteController.UPVOTE_ASC);
    }

    public void updateUI(ArrayList<Note> listNote, Map<String, User> listUserMap) {
//        Log.i("Note_And_User", listNote.get(0).getOnlineID() + " / " +
//                listUserMap.get(listNote.get(0).getCreatorUserID()).getUserID() + " ; ");
        for (Note note : listNote) {
            Log.i("UPVOTER_DOWNVOTER1", note.getUpvoterList() + "" + note.getDownvoterList());
        }
        NoteListAdapter noteListAdapter = new NoteListAdapter(getActivity(), listNote, listUserMap, NoteListAdapter.SEARCH_MODE);
        noteListView.setAdapter(noteListAdapter);
        setDynamicHeight(noteListView);
        ProgressDialog instance = ProgressDialog.getInstance();
        instance.dismiss();
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

    private void setDynamicHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}