package com.example.lmemo_capstone_project.view.home_activity.flashcard_view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;
import com.example.lmemo_capstone_project.view.home_activity.flashcard_view.review_activity.MultipleChoiceTestActivity;
import com.example.lmemo_capstone_project.view.home_activity.flashcard_view.review_activity.WritingTestActivity;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class FlashCardFragment extends Fragment {

    private static final int WRITING = 1;
    private static final int MULTIPLE_CHOICE = 2;
    private ArrayList<Word> listFlashcard;
    private ListView flashcardListView;

    public FlashCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flash_card, container, false);
        getAllFlashcard();
        flashcardListView = view.findViewById(R.id.flashcardListView);
        FlashcardListAdapter flashcardAdapter = new FlashcardListAdapter(getActivity(), listFlashcard);
        flashcardListView.setAdapter(flashcardAdapter);
//        flashcardAdapter.notifyDataSetChanged();
//        getFragmentManager().beginTransaction()
//                .add(R.id.FrameFlashcard,new FlashcardInfoFragment()).addToBackStack(null).commit();
        Button btReview = view.findViewById(R.id.btReview);
        if (listFlashcard.size() == 0) {
            btReview.setVisibility(View.INVISIBLE);
        }
        btReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfFlashcards() != 0) {
                    createTestControlDialog();
                } else {
                    Toast.makeText(getContext(), "There are no flashcards.", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    /**
     * get all flashcard from database
     * この関数はフラッシュカード（単語帳）を検索、結果のリストにフラッシュカードの情報を追加します
     */
    private void getAllFlashcard() {
        WordDAO wordDAO = LMemoDatabase.getInstance(getContext()).wordDAO();
        Word[] allFlashcard = wordDAO.getAllFlashcard();
        if (allFlashcard != null) {
            listFlashcard = new ArrayList<>(Arrays.asList(allFlashcard));
        } else {
            listFlashcard = new ArrayList<>();
        }
    }

    private int numberOfFlashcards() {
        return LMemoDatabase.getInstance(getContext()).flashcardDAO().getNumberOfVisibleFlashcards();
    }

    /**
     * この関数はテストの設定のダイアログを作成します。
     */
    private void createTestControlDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_test_control);
        dialog.setTitle("Test setting: ");
        dialog.findViewById(R.id.btStartTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etNumberOfQuestion = dialog.findViewById(R.id.etNumberOfQuestion);
                if (etNumberOfQuestion.getText().toString().length() != 0) {
                    int numberOfTest = Integer.parseInt(etNumberOfQuestion.getText().toString());
                    if (numberOfTest == 0 || numberOfTest > numberOfFlashcards()) {
                        etNumberOfQuestion.setError(numberOfTest == 0 ? "This must different than 0" : "Must smaller than " + (numberOfFlashcards() + 1));
                    } else {
                        int checkedRadioButtonId = ((RadioGroup) dialog.findViewById(R.id.rgTestMode)).getCheckedRadioButtonId();
                        int testMode = ((RadioButton) dialog.findViewById(checkedRadioButtonId)).getText().toString().equals("Writing") ? WRITING : MULTIPLE_CHOICE;
                        Intent intent = new Intent(getContext(), WritingTestActivity.class);
                        switch (testMode) {
                            case MULTIPLE_CHOICE:
                                if (numberOfFlashcards() < 4) {
                                    Toast.makeText(getContext(), "There are not enough flashcards to create a multiple-choice test. Required at least 4.", Toast.LENGTH_LONG).show();
                                } else {
                                    intent = new Intent(getContext(), MultipleChoiceTestActivity.class);
                                }
                                break;
                        }
                        intent.putExtra(getString(R.string.number_of_questions), numberOfTest);
                        getActivity().startActivityForResult(intent, HomeActivity.TEST_FLASHCARD_REQUEST_CODE);
                        dialog.dismiss();
                    }
                } else {
                    etNumberOfQuestion.setError("This is required");
                }
            }
        });
        dialog.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Log.i("Dialog", "Created");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
