package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.GetSetFlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetFlashCardOfflineTab extends Fragment {

    private Button btAddSet;
    private ListView lvSetList;
    private GetSetFlashcardController getSetFlashcardController;

    public SetFlashCardOfflineTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_set_flash_card_offline_tab, container, false);

        setupReferences(inflate);
        loadOfflineSet();
        setupActionForButton();

        return inflate;
    }

    private void setupActionForButton() {
        btAddSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateSetActivity.class);
                intent.putExtra("mode", CreateSetActivity.IN_ADDING_MODE);
                startActivity(intent);
            }
        });
    }

//    private void addNewSetFlashcard() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Enter set's name: ");
//
//        final EditText input = new EditText(getContext());
//        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
//        builder.setView(input);
//
//        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String setName = input.getText().toString();
//                setFlashcardController.createNewSet(setName);
//                loadOfflineSet();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//    }

    private void loadOfflineSet() {
        List<SetFlashcard> setFlashcards = getSetFlashcardController.getOfflineSet();
        updateUI(setFlashcards);
    }

    private void updateUI(List<SetFlashcard> setFlashcards) {
        SetFlashcardAdapter setFlashcardAdapter = new SetFlashcardAdapter(setFlashcards, getActivity(), SetFlashcardAdapter.OFFLINE_MODE);
        lvSetList.setAdapter(setFlashcardAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("RESUME", "Get here");
        loadOfflineSet();
    }

    private void setupReferences(View view) {
        btAddSet = view.findViewById(R.id.btAddSet);
        lvSetList = view.findViewById(R.id.lvSetList);
        getSetFlashcardController = new GetSetFlashcardController(new SetFlashCardOnlineTab());
    }
}
