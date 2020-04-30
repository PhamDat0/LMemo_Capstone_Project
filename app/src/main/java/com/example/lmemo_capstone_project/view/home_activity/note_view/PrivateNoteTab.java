package com.example.lmemo_capstone_project.view.home_activity.note_view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.note_controller.GetNoteController;
import com.example.lmemo_capstone_project.controller.note_controller.NoteController;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.example.lmemo_capstone_project.view.home_activity.UIUpdatable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PrivateNoteTab extends Fragment implements UIUpdatable {

    private View view;
    private NoteController addNoteController;

    public PrivateNoteTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_private_note_tab, container, false);
        addNoteController = new NoteController(getContext());
        loadNoteToUI();
        return view;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();

        if (InternetCheckingController.isOnline(getContext())) {
            ProgressDialog.getInstance().show(getContext());
            User user = LMemoDatabase.getInstance(getContext()).userDAO().getLocalUser()[0];
            if (!user.isGuest()) {
//                getAllPublicSetFlashcard(user);
                getAllPublicNotes(user);
            }
        } else {
            loadNoteToUI();
        }
    }

    private void getAllPublicNotes(User user) {
        addNoteController.downloadAllPublicNoteToSQL(user, this);
    }

    private void loadNoteToUI() {
        ListView noteListView = view.findViewById(R.id.noteViewer);
        NoteDAO noteDAO = LMemoDatabase.getInstance(getContext()).noteDAO();
        UserDAO userDAO = LMemoDatabase.getInstance(getContext()).userDAO();
        GetNoteController getNoteController = new GetNoteController(noteDAO, LMemoDatabase.getInstance(getContext()).noteOfWordDAO());
        User user = userDAO.getLocalUser()[0];
        List<Note> listNote = getNoteController.getOfflineNote(GetNoteController.GET_OFFLINE_NOTE_PRIVATE, user.getUserID());
        Map<String, User> listUserMap = new HashMap<>();
        listUserMap.put(user.getUserID(), user);
        NoteListAdapter noteListAdapter = new NoteListAdapter(getActivity(), listNote, listUserMap, NoteListAdapter.VIEW_MODE);
        noteListView.setAdapter(noteListAdapter);
    }

    @Override
    public void updateUI() {
        loadNoteToUI();
    }
}
