package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.GetSetFlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.User;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetFlashCardOnlineTab extends Fragment {

    private EditText etSearchSet;
    private ImageButton ibSearchSet;
    private ImageButton ibPrePage;
    private ImageButton ibNextPage;
    private ListView lvOnlineNote;
    private User currentUser;

    public SetFlashCardOnlineTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_set_flash_card_online_tab, container, false);

        setupReferences(inflate);
        loadYourOnlineSet();
        setupActionForButton();

        return inflate;
    }

    private void setupActionForButton() {

    }

    private void loadYourOnlineSet() {
        GetSetFlashcardController controller = new GetSetFlashcardController(this);
        controller.getUserOnlineSet(currentUser);
    }

    private void setupReferences(View inflate) {
        etSearchSet = inflate.findViewById(R.id.etSearchSet);
        ibSearchSet = inflate.findViewById(R.id.ibSearchSet);
        ibPrePage = inflate.findViewById(R.id.ibPrePage);
        ibNextPage = inflate.findViewById(R.id.ibNextPage);
        lvOnlineNote = inflate.findViewById(R.id.lvOnlineNote);
        currentUser = LMemoDatabase.getInstance(getContext()).userDAO().getLocalUser()[0];
    }

    public void updateUI(List<SetFlashcard> listSet) {
        SetFlashcardAdapter setFlashcardAdapter = new SetFlashcardAdapter(listSet, getActivity(), SetFlashcardAdapter.ONLINE_MODE);
        lvOnlineNote.setAdapter(setFlashcardAdapter);
    }
}
