package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.annotation.SuppressLint;
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
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
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
public class SetFlashCardOfflineTab extends Fragment implements UIUpdatable {

    private Button btAddSet;
    private ListView lvSetList;
    private GetSetFlashcardController getSetFlashcardController;
    private SetFlashcardController setFlashcardController;

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

    @SuppressLint("RestrictedApi")
    @Override
    public void onResume() {
        super.onResume();
        Log.i("RESUME", "OFFLINE1");

        Log.i("RESUME", "OFFLINE2");
        if (InternetCheckingController.isOnline(getContext())) {
            Log.i("RESUME", "OFFLINE3");
            ProgressDialog.getInstance().show(getContext());
            User user = LMemoDatabase.getInstance(getContext()).userDAO().getLocalUser()[0];
            if (!user.isGuest()) {
                getAllPublicSetFlashcard(user);
//                    getAllPublicNotes(user);
            }
        } else {
            loadOfflineSet();
        }

    }

    private void getAllPublicSetFlashcard(User user) {
        setFlashcardController.getUserOnlineSet(user, this);
    }

    private void setupReferences(View view) {
        btAddSet = view.findViewById(R.id.btAddSet);
        lvSetList = view.findViewById(R.id.lvSetList);
        getSetFlashcardController = new GetSetFlashcardController(new SetFlashCardOnlineTab());
        setFlashcardController = new SetFlashcardController(getContext());
    }

    @Override
    public void updateUI() {
        loadOfflineSet();
    }


//    //Refresh when change tab
//    private MyReceiver r;
//    public void refresh() {
//        loadOfflineSet();
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
//        loadOfflineSet();
//        r = new MyReceiver();
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(r,
//                new IntentFilter("TAG_REFRESH"));
//    }
//
//    private class MyReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            SetFlashCardOfflineTab.this.refresh();
//        }
//    }
//
//    @NonNull
//    @Override
//    public String toString() {
//        return "Offline";
//    }
}
