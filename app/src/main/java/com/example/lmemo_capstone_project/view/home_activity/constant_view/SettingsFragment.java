package com.example.lmemo_capstone_project.view.home_activity.constant_view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.SharedPreferencesController;
import com.example.lmemo_capstone_project.controller.flashcard_reminder_controller.FlashcardReminderController;
import com.example.lmemo_capstone_project.controller.setting_controller.SettingController;
import com.example.lmemo_capstone_project.controller.word_of_day_controller.WordOfTheDayController;

import java.util.ArrayList;
import java.util.Date;

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
    WordOfTheDayController wordOfTheDayController;
    FlashcardReminderController flashcardReminderController;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setRef(view);
        final SettingController settingController = new SettingController();
        setDailyWordSwitch(getContext(), dailyWordSwitch);
        setReminderSwitch(getContext(), reminderSwitch);
        addValueToTimeSpinner(dailyWordHour, dailyWordMin, getContext());
        addValueToTimeSpinner(reminderHour, reminderMin, getContext());
        setDailyWordTimeSpinner(getContext(), dailyWordHour, dailyWordMin);
        setReminderTimeSpinner(getContext(), reminderHour, reminderMin);
        setDailyWordDismissSwitch(getContext(), dailyWordDismissSwitch);
        setReminderDismissSwitch(getContext(), reminderDismissSwitch);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingController.saveSettingToSharePreferences(getContext(), dailyWordSwitch.isChecked(),
                        dailyWordDismissSwitch.isChecked(), dailyWordHour.getSelectedItem().toString(),
                        dailyWordMin.getSelectedItem().toString(), reminderSwitch.isChecked(),
                        reminderDismissSwitch.isChecked(), reminderHour.getSelectedItem().toString(),
                        reminderMin.getSelectedItem().toString());
                Toast.makeText(getContext(), "Save Setting Successful!", Toast.LENGTH_LONG).show();
                flashcardReminderController = new FlashcardReminderController();
                flashcardReminderController.createNotificationChannel(getActivity());
                flashcardReminderController.startAlarm(SharedPreferencesController.reminderIsOn(getActivity().getApplicationContext()), true, getActivity(),
                        SharedPreferencesController.getReminderTime(getActivity().getApplicationContext()));
                wordOfTheDayController = new WordOfTheDayController();
//                wordOfTheDayController.createNotificationChannel(getActivity());
                WordOfTheDayController.startAlarm(
                        SharedPreferencesController.dailyWordIsOn(getActivity().getApplicationContext()), true, getActivity(),
                        SharedPreferencesController.getDailyWordTime(getActivity().getApplicationContext()));
            }
        });
        return view;
    }

    private void setReminderDismissSwitch(Context context, Switch reminderDismissSwitch) {
        // set reminder switch  dismiss is checked or not when loading UI
        reminderDismissSwitch.setChecked(SharedPreferencesController.dismissReminderIsOn(context));
    }

    private void setDailyWordDismissSwitch(Context context, Switch dailyWordDismissSwitch) {
        //set daily word switch dismiss is checked or not when load UI
        dailyWordDismissSwitch.setChecked(SharedPreferencesController.dismissDailyWordIsOn(context));
    }

    private void setReminderTimeSpinner(Context context, Spinner reminderHour, Spinner reminderMin) {
        //set time of reminder time spinner when loading UI
        long time = SharedPreferencesController.getReminderTime(context);
        Date date = new Date(time);
        int hour = date.getHours();
        int minute = date.getMinutes();
        reminderHour.setSelection(((ArrayAdapter<String>) reminderHour.getAdapter()).getPosition("" + hour));
        reminderMin.setSelection(((ArrayAdapter<String>) reminderMin.getAdapter()).getPosition("" + minute));
    }

    private void setDailyWordTimeSpinner(Context context, Spinner dailyWordHour, Spinner dailyWordMin) {
        // set time of daily word time spinner when loading UI
        long time = SharedPreferencesController.getDailyWordTime(context);
        Date date = new Date(time);
        int hour = date.getHours();
        int minute = date.getMinutes();
        dailyWordHour.setSelection(((ArrayAdapter<String>) dailyWordHour.getAdapter()).getPosition("" + hour));
        dailyWordMin.setSelection(((ArrayAdapter<String>) dailyWordMin.getAdapter()).getPosition("" + minute));
    }

    private void addValueToTimeSpinner(Spinner hourSpinner, Spinner minuteSpinner, Context context) {
        //add value to time spinner
        ArrayList<String> listHour = new ArrayList<>();
        ArrayList<String> listMinute = new ArrayList<>();
        if (listHour.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                listHour.add("" + i);
            }
        }
        if (listMinute.isEmpty()) {
            for (int i = 0; i < 60; i++) {
                listMinute.add("" + i);
            }
        }
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, listHour);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, listMinute);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteSpinner.setAdapter(minuteAdapter);
    }

    private void setReminderSwitch(Context context, Switch reminderSwitch) {
        // set reminder switch is checked or not when loading UI
        reminderSwitch.setChecked(SharedPreferencesController.reminderIsOn(context));
    }

    private void setDailyWordSwitch(Context context, Switch dailyWordSwitch) {
        //set daily word switch is checked or not when load UI
        dailyWordSwitch.setChecked(SharedPreferencesController.dailyWordIsOn(context));
    }

    private void setRef(View view) {
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}
