package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.note_controller.NoteController;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.GetSetFlashcardController;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.SetFlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.example.lmemo_capstone_project.view.home_activity.UIUpdatable;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetFlashCardOnlineTab extends Fragment implements UIUpdatable {

    private EditText etSearchSet;
    private ImageButton ibSearchSet;
    private ImageButton ibNextPage;
    private ListView lvOnlineNote;
    private GetSetFlashcardController controller;
    private List<SetFlashcard> currentListSet;
    private SetFlashcardController setFlashcardController;
    private NoteController addNoteController;

    public SetFlashCardOnlineTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_set_flash_card_online_tab, container, false);
        setupReferences(inflate);
//        loadYourOnlineSet();
        setupActionForButton();

        return inflate;
    }

    private void setupActionForButton() {
        ibSearchSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetCheckingController.isOnline(getContext())) {
                    ProgressDialog.getInstance().show(getContext());
                    String keyword = etSearchSet.getText().toString();
                    controller.getOnlineSet(keyword);
                } else {
                    Toast.makeText(getContext(), "There is no internet connections", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//    private void loadYourOnlineSet() {
//        if (InternetCheckingController.isOnline(getContext())) {
//            ProgressDialog.getInstance().show(getContext());
//            controller.getUserOnlineSet(currentUser);
//            ibNextPage.setVisibility(View.GONE);
//            ibPrePage.setVisibility(View.GONE);
//        } else {
//            Toast.makeText(getContext(), "There is no internet, online set may not function", Toast.LENGTH_LONG).show();
//        }
//    }

    private void setupReferences(View inflate) {
        etSearchSet = inflate.findViewById(R.id.etSearchSet);
        ibSearchSet = inflate.findViewById(R.id.ibSearchSet);
        ibNextPage = inflate.findViewById(R.id.ibNextPage);
        lvOnlineNote = inflate.findViewById(R.id.lvOnlineNote);
        controller = new GetSetFlashcardController(this);
        setFlashcardController = new SetFlashcardController(getContext());
        addNoteController = new NoteController(getContext());
    }

    public void updateUI(List<SetFlashcard> listSet) {
        currentListSet = listSet;
        SetFlashcardAdapter setFlashcardAdapter = new SetFlashcardAdapter(listSet, getActivity(), SetFlashcardAdapter.ONLINE_MODE);
        lvOnlineNote.setAdapter(setFlashcardAdapter);
        if (listSet.size() != 0) {
            ibNextPage.setVisibility(View.VISIBLE);
            ibNextPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (InternetCheckingController.isOnline(getContext())) {
                        ProgressDialog.getInstance().show(getContext());
                        String keyword = etSearchSet.getText().toString();
                        controller.getMoreOnlineSet(keyword, currentListSet);
                    } else {
                        Toast.makeText(getContext(), "There is no internet connections", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            ibNextPage.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        Log.i("RESUME", "ONLINE1");
        if (InternetCheckingController.isOnline(getContext())) {
            Log.i("RESUME", "ONLINE2");
//            User user = LMemoDatabase.getInstance(getContext()).userDAO().getLocalUser()[0];
//            if (!user.isGuest()) {
//                ProgressDialog.getInstance().show(getContext());
//                getAllPublicSetFlashcard(user);
//            }
            ProgressDialog.getInstance().show(getContext());
            controller.getOnlineSet("");
        }

    }

    private void getAllPublicSetFlashcard(User user) {
        setFlashcardController.getUserOnlineSet(user, this);
    }

//    @Override
//    public void setMenuVisibility(final boolean visible) {
//        if (visible) {
//            if (InternetCheckingController.isOnline(getContext())) {
//                ProgressDialog.getInstance().show(getContext());
//                controller.getOnlineSet("");
//            }
//        }
//
//        super.setMenuVisibility(visible);
//    }

    @Override
    public void updateUI() {

    }

    //Refresh when change tab
//    private MyReceiver r;
//    public void refresh() {
//        loadYourOnlineSet();
//        Log.i("Refresh", "YES");
//    }
//
//    public void onPause() {
//        super.onPause();
//        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(r);
//    }
//
//    public void onResume() {
//        super.onResume();
//        loadYourOnlineSet();
//        r = new MyReceiver();
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(r,
//                new IntentFilter("TAG_REFRESH"));
//    }
//
//    private class MyReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            SetFlashCardOnlineTab.this.refresh();
//        }
//    }
//
//    @NonNull
//    @Override
//    public String toString() {
//        return "Online";
//    }
}
