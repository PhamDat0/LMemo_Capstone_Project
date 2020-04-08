package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.GetSetFlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;

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
    private GetSetFlashcardController controller;
    private List<SetFlashcard> currentListSet;

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
        ibSearchSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog.getInstance().show(getContext());
                String keyword = etSearchSet.getText().toString();
                controller.getOnlineSet(keyword);
            }
        });
    }

    private void loadYourOnlineSet() {
        if (InternetCheckingController.isOnline(getContext())) {
            ProgressDialog.getInstance().show(getContext());
            controller.getUserOnlineSet(currentUser);
            ibNextPage.setVisibility(View.GONE);
            ibPrePage.setVisibility(View.GONE);
        } else {
            Toast.makeText(getContext(), "There is no internet, online set may not function", Toast.LENGTH_LONG).show();
        }
    }

    private void setupReferences(View inflate) {
        etSearchSet = inflate.findViewById(R.id.etSearchSet);
        ibSearchSet = inflate.findViewById(R.id.ibSearchSet);
        ibPrePage = inflate.findViewById(R.id.ibPrePage);
        ibNextPage = inflate.findViewById(R.id.ibNextPage);
        lvOnlineNote = inflate.findViewById(R.id.lvOnlineNote);
        currentUser = LMemoDatabase.getInstance(getContext()).userDAO().getLocalUser()[0];
        controller = new GetSetFlashcardController(this);
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
                    ProgressDialog.getInstance().show(getContext());
                    String keyword = etSearchSet.getText().toString();
                    controller.getMoreOnlineSet(keyword, currentListSet);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadYourOnlineSet();
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
