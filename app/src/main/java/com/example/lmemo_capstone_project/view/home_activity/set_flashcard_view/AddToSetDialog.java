package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddToSetDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddToSetDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CURRENT_WORD = "currentWord";

    // TODO: Rename and change types of parameters
    private Word word;
    private ListView lvListSet;
    private Button btSaveSet;

    public AddToSetDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param word The searched word
     * @return A new instance of fragment AddToSetDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static AddToSetDialog newInstance(Word word) {
        AddToSetDialog fragment = new AddToSetDialog();
        Bundle args = new Bundle();
        args.putSerializable(CURRENT_WORD, word);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            word = (Word) getArguments().getSerializable(CURRENT_WORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_to_set_dialog, container, false);
        setPreferences(view);
        loadListSet();
        setAction();
        return view;
    }

    private void loadListSet() {
        LMemoDatabase instance = LMemoDatabase.getInstance(getContext());
        List<SetFlashcard> setFlashcards = instance.setFlashcardDAO().getOwnerFlashcard(instance.userDAO().getLocalUser()[0].getUserID());
        if (setFlashcards.size() == 0) {
            Toast.makeText(getContext(), "You doesn't have any set", Toast.LENGTH_LONG).show();
            this.dismiss();
        } else {
            for (SetFlashcard setFlashcard : setFlashcards) {
                setFlashcard.setWordID(instance.flashcardBelongToSetDAO().getFlashcardsBySetID(setFlashcard.getSetID()));
            }
            ListAdapter adapter = new ListSetForAddDeleteAdapter(getActivity(), setFlashcards, word);
            lvListSet.setAdapter(adapter);
        }
    }

    private void setAction() {
        btSaveSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setPreferences(View view) {
        lvListSet = view.findViewById(R.id.lvListSet);
        btSaveSet = view.findViewById(R.id.btSaveSet);
    }
}
