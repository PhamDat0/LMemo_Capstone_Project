package com.example.lmemo_capstone_project.view.home_activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.note_controller.AddNoteController;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WordSearchingFragment extends Fragment {
    Dialog addNoteDialog;
    Button btnOpenTakeNoteDialog;

    private TextToSpeech textToSpeech;

    public WordSearchingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_searching, container, false);
        btnOpenTakeNoteDialog = view.findViewById(R.id.btnOpenTakeNoteDialog);
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
        return view;
    }

    private  void showAddNoteDialog(View v){
        final AddNoteController addNoteController = new AddNoteController(this.getActivity());
        addNoteDialog.setContentView(R.layout.demo_popup_add_note);
        final EditText txtNoteContent = (EditText) addNoteDialog.findViewById(R.id.txtTakeNote);
        TextView txtWord = (TextView) addNoteDialog.findViewById(R.id.txtWord);
        Button btnSave = (Button) addNoteDialog.findViewById(R.id.btnAddNote);
        Button btnCancel = (Button) addNoteDialog.findViewById(R.id.btnCancelAddNote);
        final Switch isPublic = (Switch) addNoteDialog.findViewById(R.id.isNotePublic);
        final Word word = getWord();
        txtWord.setText(word.getKana());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!txtNoteContent.getText().toString().isEmpty()){
                        addNoteController.getNoteFromUI(word.getWordID(),txtNoteContent.getText().toString(),isPublic.isChecked());
                        Toast.makeText(getContext(), "Add note successful", Toast.LENGTH_LONG).show();
                        addNoteDialog.dismiss();
                    }
                    else {
                        txtNoteContent.setError("Please enter note");
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNoteDialog.dismiss();
            }
        });
        addNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addNoteDialog.show();
    }
    private void wordSearchResult(View container) {
        Bundle bundle = getArguments();
        if (bundle  != null) {
//            for (String key: bundle.keySet()) {
//                Log.d ("myApplication", key + " is a key in the bundle");
//            }
            btnOpenTakeNoteDialog.setVisibility(View.VISIBLE);

            final Word word = (Word) bundle.getSerializable("result");
//            Log.i("Object",container.findViewById(R.id.tvKana)==null?"null":"tvKana");
            ((TextView) container.findViewById(R.id.tvKana)).setText("[ "+ word.getKana()+" ]");
            ((TextView) container.findViewById(R.id.tvKanji)).setText("  " + word.getKanjiWriting());
            ((TextView) container.findViewById(R.id.tvMeaning)).setText(" . " + word.getMeaning().replace("\n", "\n . "));
            ((TextView) container.findViewById(R.id.tvPartOfSpeech)).setText(" * " + word.getPartOfSpeech());
            ImageButton btPronunciation = container.findViewById(R.id.btPronunciation);
            btPronunciation.setVisibility(View.VISIBLE);
            btPronunciation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech.speak(word.getKana().split("/")[0].trim(), TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
        } else {
            Log.d ("myApplication",  " no key in bundle");
            btnOpenTakeNoteDialog.setVisibility(View.INVISIBLE);
        }
    }
    private Word getWord(){
        Bundle bundle = getArguments();
        Word word = (Word) bundle.getSerializable("result");
        return word;
    }

}
