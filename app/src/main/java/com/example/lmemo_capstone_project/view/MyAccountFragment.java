package com.example.lmemo_capstone_project.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.dao.RewardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.example.lmemo_capstone_project.model.room_db_entity.User;


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
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        updateInformationToUI(container);
        return view;
    }

    private void updateInformationToUI(ViewGroup container) {
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
        ((TextView) container.findViewById(R.id.tvReward)).setText("" + nextReward.getRewardName());
    }

}
