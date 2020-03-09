package com.example.lmemo_capstone_project.view.home_activity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WordSearchingFragment extends Fragment {

    private TextToSpeech textToSpeech;

    public WordSearchingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_searching, container, false);
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.JAPAN);
                }
            }
        });
        wordSearchResult(view);
        return view;
    }

    private void wordSearchResult(View container) {
        Bundle bundle = getArguments();
        if (bundle  != null) {
//            for (String key: bundle.keySet()) {
//                Log.d ("myApplication", key + " is a key in the bundle");
//            }
            final Word word = (Word) bundle.getSerializable("result");
//            Log.i("Object",container.findViewById(R.id.tvKana)==null?"null":"tvKana");
            ((TextView) container.findViewById(R.id.tvKana)).setText("[ "+ word.getKana()+" ]");
            ((TextView) container.findViewById(R.id.tvKanji)).setText("  " + word.getKanjiWriting());
            ((TextView) container.findViewById(R.id.tvMeaning)).setText(" . " + word.getMeaning().replace("\n", "\n . "));
            ((TextView) container.findViewById(R.id.tvPartOfSpeech)).setText(" * " + word.getPartOfSpeech());
            Button btPronunciation = container.findViewById(R.id.btPronunciation);
            btPronunciation.setVisibility(View.VISIBLE);
            btPronunciation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech.speak(word.getKana().split("/")[0].trim(), TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
        } else {
            Log.d ("myApplication",  " no key in bundle");
        }
    }


}
