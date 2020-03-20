package com.example.lmemo_capstone_project.view.home_activity.note_view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.controller.note_controller.GetNoteController;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PrivateNoteTab extends Fragment {

    private View view;

    public PrivateNoteTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_private_note_tab, container, false);
        loadNoteToUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("RESUME", "Get here");
        loadNoteToUI();
    }

    private void loadNoteToUI() {
        ListView noteListView = view.findViewById(R.id.noteViewer);
        NoteDAO noteDAO = LMemoDatabase.getInstance(getContext()).noteDAO();
        UserDAO userDAO = LMemoDatabase.getInstance(getContext()).userDAO();
        GetNoteController getNoteController = new GetNoteController(noteDAO);
        User user = userDAO.getLocalUser()[0];
        List<Note> listNote = getNoteController.getOfflineNote(GetNoteController.GET_OFFLINE_NOTE_PRIVATE, user.getUserID());
        Map<String, User> listUserMap = new HashMap<>();
        listUserMap.put(user.getUserID(), user);
        NoteListAdapter noteListAdapter = new NoteListAdapter(getActivity(), listNote, listUserMap, NoteListAdapter.VIEW_MODE);
        noteListView.setAdapter(noteListAdapter);
    }
}
