package com.example.lmemo_capstone_project.view.home_activity.constant_view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.SharedPreferencesController;
import com.example.lmemo_capstone_project.controller.setting_controller.SettingController;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Switch dailyWordSwitch;
    private Switch dailyWordDismissSwitch;
    private Spinner dailyWordHour;
    private Spinner dailyWordMin;
    private Switch reminderSwitch;
    private Switch reminderDismissSwitch;
    private Spinner reminderHour;
    private Spinner reminderMin;
    private Button btnSave;
    private Button btnCancel;
    public SettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        dailyWordSwitch = view.findViewById(R.id.dailyWordIsOn);
        dailyWordHour = view.findViewById(R.id.dailyWordHour);
        dailyWordMin = view.findViewById(R.id.dailyWordMinute);
        reminderSwitch = view.findViewById(R.id.reminderIsOn);
        reminderHour = view.findViewById(R.id.reminderHour);
        reminderMin = view.findViewById(R.id.reminderMinute);
        btnSave = view.findViewById(R.id.btnSaveSetting);
        btnCancel = view.findViewById(R.id.btnCancelSetting);
        dailyWordDismissSwitch = view.findViewById(R.id.dailyWordDisMissIsOn);
        reminderDismissSwitch = view.findViewById(R.id.reminderDisMissIsOn);
        final SettingController settingController = new SettingController();
        settingController.setDailyWordSwitch(getContext(), dailyWordSwitch);
        settingController.setReminderSwitch(getContext(), reminderSwitch);
        settingController.addValueToTimeSpinner(dailyWordHour,dailyWordMin,getContext());
        settingController.addValueToTimeSpinner(reminderHour,reminderMin,getContext());
        settingController.setDailyWordTimeSpinner(getContext(),dailyWordHour,dailyWordMin);
        settingController.setReminderTimeSpinner(getContext(),reminderHour,reminderMin);
        settingController.setDailyWordDismissSwitch(getContext(),dailyWordDismissSwitch);
        settingController.setReminderDismissSwitch(getContext(),reminderDismissSwitch);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    settingController.controlOnClickEvent(getContext(),dailyWordSwitch,dailyWordDismissSwitch,dailyWordHour,dailyWordMin,
                            reminderSwitch,reminderDismissSwitch,reminderHour,reminderMin);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}
