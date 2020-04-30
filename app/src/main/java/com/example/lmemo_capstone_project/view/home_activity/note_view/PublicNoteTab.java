package com.example.lmemo_capstone_project.view.home_activity.note_view;

import android.annotation.SuppressLint;
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
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.note_controller.GetNoteController;
import com.example.lmemo_capstone_project.controller.note_controller.NoteController;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.SetFlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.example.lmemo_capstone_project.view.home_activity.UIUpdatable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicNoteTab extends Fragment implements UIUpdatable {

    private View view;
    private SetFlashcardController setFlashcardController;
    private NoteController addNoteController;

    public PublicNoteTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_public_note_tab, container, false);
        setFlashcardController = new SetFlashcardController(getContext());
        addNoteController = new NoteController(getContext());
        loadNoteToUI();
        return view;
    }

    private void loadNoteToUI() {
        ListView noteListView = view.findViewById(R.id.noteViewer);
        LMemoDatabase instance = LMemoDatabase.getInstance(getContext());
        NoteDAO noteDAO = instance.noteDAO();
        UserDAO userDAO = instance.userDAO();
        GetNoteController getNoteController = new GetNoteController(noteDAO, instance.noteOfWordDAO());
        User user = userDAO.getLocalUser()[0];
        List<Note> listNote = getNoteController.getOfflineNote(GetNoteController.GET_OFFLINE_NOTE_PUBLIC, user.getUserID());
        for (Note note : listNote) {
            Log.w("AddNoteActivity", "OnlineID in public view: " + note.getOnlineID());
        }
        Map<String, User> listUserMap = new HashMap<>();
        listUserMap.put(user.getUserID(), user);
        NoteListAdapter noteListAdapter = new NoteListAdapter(getActivity(), listNote, listUserMap, NoteListAdapter.VIEW_MODE);
        noteListView.setAdapter(noteListAdapter);
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

    @Override
    public void updateUI() {
        loadNoteToUI();
    }
}
