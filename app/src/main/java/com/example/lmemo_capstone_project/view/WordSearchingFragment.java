package com.example.lmemo_capstone_project.view;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WordSearchingFragment extends Fragment {

    public WordSearchingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_searching, container, false);
        wordSearchResult(container);
        return view;
    }

    private void wordSearchResult(ViewGroup container) {
        Bundle bundle = getArguments();
        if (bundle  != null) {
            for (String key: bundle.keySet()) {
                Log.d ("myApplication", key + " is a key in the bundle");
            }
            Word word = (Word) bundle.getSerializable("result");
            ((TextView) container.findViewById(R.id.tvKana)).setText(word.getKana());
            ((TextView) container.findViewById(R.id.tvKanji)).setText(word.getKanjiWriting());
            ((TextView) container.findViewById(R.id.tvMeaning)).setText(word.getMeaning());
            ((TextView) container.findViewById(R.id.tvPartOfSpeech)).setText(word.getPartOfSpeech());

        } else {
            Log.d ("myApplication",  " no key in bundle");
        }
    }


}
