package com.example.lmemo_capstone_project.view.home_activity.account_view;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.RewardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.controller.my_account_controller.MyAccountController;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    private FirebaseAuth mAuth;

    public MyAccountFragment() {
        // Required empty public constructor
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        updateInformationToUI(view);
        view.findViewById(R.id.btSaveAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasAnInternetConnection()) {
                    updateUserInformation(view);
                    Toast.makeText(getContext(), "Save successfully.", Toast.LENGTH_LONG).show();
                } else {
                    notifyNoInternetConnections();
                }
            }
        });
        return view;
    }

    private void notifyNoInternetConnections() {
        Log.i("NO_INTERNET", "No internet");
        Toast.makeText(getContext(), "There are no internet connections.", Toast.LENGTH_LONG).show();
    }

    /**
     * @return true if there is at least 1 Internet connection
     *          インターネット接続できる場合はtrueを返します。
     * この関数はインターネットの接続を確認します。ある場合はtrueを返します。
     */
    private boolean hasAnInternetConnection() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param container The container contains every View Object in this fragment
     *                  ビューオブジェクトを持っているのコンテナオブジェクト
     * この関数はcontainerからユーザーの情報を取って、FirebaseとSQLiteに情報を更新します。
     */
    private void updateUserInformation(View container) {
        User user = getUserInformationFromView(container);
        updateUserToFirebase(user);
        updateUserToSQLite(user);
    }

    /**
     * @param container The container contains every View Object in this fragment
     *                  ビューオブジェクトを持っているのコンテナオブジェクト
     * @return The user's information
     * この関数はcontainerからユーザーの情報を取ります。
     */
    private User getUserInformationFromView(View container) {
        String email = ((EditText) container.findViewById(R.id.etEmail)).getText().toString().toLowerCase();
        String displayName = ((EditText) container.findViewById(R.id.etDisplayName)).getText().toString();
        int checkedRadioButtonId = ((RadioGroup) container.findViewById(R.id.rgGender)).getCheckedRadioButtonId();
        boolean gender = ((RadioButton) container.findViewById(checkedRadioButtonId)).getText().equals("Male");
        UserDAO userDAO = LMemoDatabase.getInstance(getContext()).userDAO();
        User currentUser = userDAO.getLocalUser()[0];
        return new User(currentUser.getUserID(), email, displayName, gender, currentUser.getContributionPoint(), currentUser.getLoginTime());
    }

    /**
     * @param user The user's information
     *             ユーザーの情報のオブジェクト
     * この関数はユーザーの情報をSQLiteに情報を更新します。
     */
    private void updateUserToSQLite(User user) {
        UserDAO userDAO = LMemoDatabase.getInstance(getContext()).userDAO();
        userDAO.updateUser(user);
    }

    /**
     * @param user The user's information
     *             ユーザーの情報のオブジェクト
     * この関数はユーザーの情報をFirebaseに情報を更新します。
     */
    private void updateUserToFirebase(User user) {
        MyAccountController myAccountController = new MyAccountController();
        myAccountController.updateUser(user);
    }

    /**
     * @param container The container contains every View Object in this fragment
     *                  ビューオブジェクトを持っているのコンテナオブジェクト
     * この関数はユーザーの情報をSQLiteから読んで、ユーザーインターフェイス に書きます。
     */
    private void updateInformationToUI(View container) {
        LMemoDatabase lMemoDatabase = LMemoDatabase.getInstance(getContext());
        UserDAO userDAO = lMemoDatabase.userDAO();
        MyAccountController myAccountController = new MyAccountController();
        User user = userDAO.getLocalUser()[0];
        if (hasAnInternetConnection()) {
            myAccountController.updateSQLWithOnlineInfoForViewInfo(user, userDAO, container, this);
        } else {
            updateUI(container);
        }
    }

    public void updateUI(View container) {
        LMemoDatabase lMemoDatabase = LMemoDatabase.getInstance(getContext());
        UserDAO userDAO = lMemoDatabase.userDAO();
        RewardDAO rewardDAO = lMemoDatabase.rewardDAO();
        User user = userDAO.getLocalUser()[0];
        Reward currentReward = rewardDAO.getBestReward(user.getContributionPoint())[0];
        Reward nextReward;
        try {
            nextReward = rewardDAO.getNextReward(user.getContributionPoint())[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            nextReward = new Reward();
            nextReward.setRewardID(-1);
            nextReward.setRewardName("Nothing more");
            nextReward.setMinimumReachPoint(999999);
        }
        ((EditText) container.findViewById(R.id.etEmail)).setText(user.getEmail());
        ((EditText) container.findViewById(R.id.etDisplayName)).setText(user.getDisplayName());
        Log.i("GENDER", "" + user.isGender());
        ((RadioButton) container.findViewById(user.isGender() ? R.id.rbMale : R.id.rbFemale)).setChecked(true);
        ((TextView) container.findViewById(R.id.tvContributtionPoint)).setText("" + user.getContributionPoint());
        ((TextView) container.findViewById(R.id.tvReward)).setText("" + currentReward.getRewardName());
        ((TextView) container.findViewById(R.id.tvNextReward)).setText("" + nextReward.getRewardName());
    }

}
