package com.example.lmemo_capstone_project.view.home_activity;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FlashcardInfoFragment extends Fragment {

    private Word[] flashcardWord;
    CardView cvClose;
    public FlashcardInfoFragment() {
        // Required empty public constructor
    }


    public FlashcardInfoFragment(Word word) {
//        flashcardWord =
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_flashcard_info, container, false);
        wordSearchResult(view);
        cvClose = (CardView)view.findViewById(R.id.cvClose);
        cvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
//        getActivity().onBackPressed();
        return view;
    }

    private void wordSearchResult(View container) {
        Bundle bundle = getArguments();
        if (bundle  != null) {
//            for (String key: bundle.keySet()) {
//                Log.d ("myApplication", key + " is a key in the bundle");
//            }
            Word word = (Word) bundle.getSerializable("wordResult");
//            Log.i("Object",container.findViewById(R.id.tvKana)==null?"null":"tvKana");
            ((TextView) container.findViewById(R.id.tvKana)).setText("[ "+ word.getKana()+" ]");
            ((TextView) container.findViewById(R.id.tvKanji)).setText("  " + word.getKanjiWriting());
            ((TextView) container.findViewById(R.id.tvMeaning)).setText(" . "+ word.getMeaning());
            ((TextView) container.findViewById(R.id.tvPartOfSpeech)).setText(" * " + word.getPartOfSpeech());

        } else {
            Log.d ("myApplication",  " no key in bundle");
        }
    }
}