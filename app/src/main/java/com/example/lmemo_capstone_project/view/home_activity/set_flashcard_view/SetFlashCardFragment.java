package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.GetSetFlashcardController;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.SetFlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;

import java.util.List;

public class SetFlashCardFragment extends Fragment {

    private Button btAddSet;
    private Button btBrowseSet;
    private ListView lvSetList;
    private GetSetFlashcardController getSetFlashcardController;
    private SetFlashcardController setFlashcardController;
    private EditText etSearch;

    public SetFlashCardFragment() {
        // Required empty public constructor
    }

    public static SetFlashCardFragment newInstance(String param1, String param2) {
        SetFlashCardFragment fragment = new SetFlashCardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_flash_card, container, false);
        setupReferences(view);
        loadOfflineSet();
        setupActionForButton();
        return view;
    }

    private void setupActionForButton() {
        btAddSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewSetFlashcard();
            }
        });
        btBrowseSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOnlineNote();
            }
        });
    }

    private void addNewSetFlashcard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter set's name: ");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String setName = input.getText().toString();
                setFlashcardController.createNewSet(setName);
                loadOfflineSet();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void loadOnlineNote() {
        getSetFlashcardController.getOnlineSet();
    }

    private void loadOfflineSet() {
        List<SetFlashcard> setFlashcards = getSetFlashcardController.getOfflineSet();
        updateUI(setFlashcards, SetFlashcardAdapter.OFFLINE_MODE);
    }

    public void updateUI(List<SetFlashcard> setFlashcards, int mode) {
        SetFlashcardAdapter setFlashcardAdapter = new SetFlashcardAdapter(setFlashcards, getActivity(), mode);
        lvSetList.setAdapter(setFlashcardAdapter);
    }

    private void setupReferences(View view) {
        btAddSet = view.findViewById(R.id.btAddSet);
        btBrowseSet = view.findViewById(R.id.btBrowseSet);
        lvSetList = view.findViewById(R.id.lvSetList);
        etSearch = view.findViewById(R.id.etSearchForSet);
        getSetFlashcardController = new GetSetFlashcardController(this);
        setFlashcardController = new SetFlashcardController(getActivity());
    }
}
