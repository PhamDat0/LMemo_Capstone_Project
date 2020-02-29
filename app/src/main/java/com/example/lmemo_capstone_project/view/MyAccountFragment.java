package com.example.lmemo_capstone_project.view;


import android.os.Bundle;
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
import com.example.lmemo_capstone_project.controller.database_controller.firebase_dao.OnlineUserDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.RewardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.exception.DublicatedMailException;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.example.lmemo_capstone_project.model.room_db_entity.User;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    public MyAccountFragment() {
        // Required empty public constructor

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
                } else {
                    notifyNoInternetConnections();
                }
            }
        });
        return view;
    }

    private void notifyNoInternetConnections() {
        Toast.makeText(getContext(), "There are no internet connections.", Toast.LENGTH_LONG);
    }

    /**
     * @return true if there is at least 1 Internet connection
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
     *                  この関数はcontainerからユーザーの情報を取って、FirebaseとSQLiteに情報を更新します。
     */
    private void updateUserInformation(View container) {
        User user = getUserInformationFromView(container);
        updateUserToFirebase(user);
        updateUserToSQLite(user);
    }

    /**
     * @param container The container contains every View Object in this fragment
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
     *             この関数はユーザーの情報をSQLiteに情報を更新します。
     */
    private void updateUserToSQLite(User user) {
        UserDAO userDAO = LMemoDatabase.getInstance(getContext()).userDAO();
        userDAO.updateUser(user);
    }

    /**
     * @param user The user's information
     *             この関数はユーザーの情報をFirebaseに情報を更新します。
     */
    private void updateUserToFirebase(User user) {
        OnlineUserDAO onlineUserDAO = new OnlineUserDAO();
        try {
            onlineUserDAO.updateUser(user);
        } catch (DublicatedMailException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    /**
     * @param container The container contains every View Object in this fragment
     *                  この関数はユーザーの情報をSQLiteから読んで、ユーザーインターフェイス に書きます。
     */
    private void updateInformationToUI(View container) {
        LMemoDatabase lMemoDatabase = LMemoDatabase.getInstance(getContext());
        UserDAO userDAO = lMemoDatabase.userDAO();
        RewardDAO rewardDAO = lMemoDatabase.rewardDAO();
        User user = userDAO.getLocalUser()[0];
        Reward currentReward = rewardDAO.getBestReward(user.getContributionPoint())[0];
        Reward nextReward = rewardDAO.getNextReward(user.getContributionPoint())[0];
        ((EditText) container.findViewById(R.id.etEmail)).setText(user.getEmail());
        ((EditText) container.findViewById(R.id.etDisplayName)).setText(user.getDisplayName());
        ((RadioButton) container.findViewById(user.isMale() ? R.id.rbMale : R.id.rbFemale)).setChecked(true);
        ((TextView) container.findViewById(R.id.tvContributtionPoint)).setText("" + user.getContributionPoint());
        ((TextView) container.findViewById(R.id.tvReward)).setText("" + currentReward.getRewardName());
        ((TextView) container.findViewById(R.id.tvNextReward)).setText("" + nextReward.getRewardName());
    }

}
