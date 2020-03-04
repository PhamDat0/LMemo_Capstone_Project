package com.example.lmemo_capstone_project.view.review_activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.TestController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MultipleChoiceTestActivity extends AppCompatActivity {

    private static final int LOADING = 1;
    private static final int TEST = 2;
    private static final int KANJI_KANA = 1;
    private static final int KANJI_MEANING = 2;
    private static final int KANA_KANJI = 3;
    private static final int KANA_MEANING = 4;
    private static final int MEANING_KANA = 5;
    private static final int MEANING_KANJI = 6;
    private static final double ACCURACY_CHANGE = 15;
    private int number_of_question;
    private List<Word> words;
    private List<Button> answerButton;
    private Word currentWord;
    private Date startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_test);
        Intent intent = getIntent();
        number_of_question = intent.getIntExtra(getString(R.string.number_of_questions), 0);
        answerButton = new ArrayList<>();
        answerButton.add((Button) findViewById(R.id.btAnswer1));
        answerButton.add((Button) findViewById(R.id.btAnswer2));
        answerButton.add((Button) findViewById(R.id.btAnswer3));
        answerButton.add((Button) findViewById(R.id.btAnswer4));
        for (Button b : answerButton) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answer(v);
                }
            });
        }
        loadQuestion();
    }

    private void answer(View v) {
        saveNewValueOfFlashcard(v);
        showResultDialog();
    }

    private void endCurrentQuestion() {
        if (!setupQuestion()) {
            setResult(HomeActivity.TEST_FLASHCARD_REQUEST_CODE);
            finish();
        }
    }

    private void showResultDialog() {
        final Dialog container = new Dialog(MultipleChoiceTestActivity.this);
        container.setContentView(R.layout.fragment_word_searching);
        container.setTitle("Word information:");
        ((TextView) container.findViewById(R.id.tvKana)).setText("[ " + currentWord.getKana() + " ]");
        ((TextView) container.findViewById(R.id.tvKanji)).setText("  " + currentWord.getKanjiWriting());
        ((TextView) container.findViewById(R.id.tvMeaning)).setText(" . " + currentWord.getMeaning());
        ((TextView) container.findViewById(R.id.tvPartOfSpeech)).setText(" * " + currentWord.getPartOfSpeech());
        container.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                endCurrentQuestion();
            }
        });
        Log.i("SET_UP_DIALOG", "Success");
        container.show();
    }

    private void saveNewValueOfFlashcard(View v) {
        FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO();
        Flashcard flashcard = flashcardDAO.getFlashCardByID(currentWord.getWordID())[0];
        flashcard.setSpeedPerCharacter(getSpeedPerCharacterBasedOnAnswer(flashcard, v));
        flashcard.setAccuracy(getAccuracyBasedOnAnswer(flashcard, v));
        flashcardDAO.updateFlashcard(flashcard);
        Log.i("SAVE_VALUE", "Success");
    }

    private double getAccuracyBasedOnAnswer(Flashcard flashcard, View v) {
        double accuracy;
        if (isRightAnswer(v)) {
            accuracy = flashcard.getAccuracy() + ACCURACY_CHANGE;
            return accuracy>100?100:accuracy;
        } else {
            accuracy = flashcard.getAccuracy() - ACCURACY_CHANGE * 2;
            return accuracy < 0 ? 0 : accuracy;
        }
    }

    private boolean isRightAnswer(View v) {
        Button btAnswer = (Button) v;
        String answer = btAnswer.getText().toString();
        return answer.equals(currentWord.getKana())
                || answer.equals(currentWord.getKanjiWriting())
                || answer.equals(currentWord.getMeaning());
    }

    private double getSpeedPerCharacterBasedOnAnswer(Flashcard flashcard, View v) {
        TextView tvQuestion = findViewById(R.id.tvMeaning);
        String question = tvQuestion.getText().toString();
        Date endTime = new Date();
        double timeToAnswer = endTime.getTime() - startTime.getTime();
        Button btAnswer = (Button) v;
        String answer = btAnswer.getText().toString();
        String[] partOfAnswer = answer.split("/");
        String[] partOfQuestion = question.split("/");

        int totalCharacter = 0;
        totalCharacter+=calculateTotalCharacter(partOfQuestion);
        totalCharacter+=calculateTotalCharacter(partOfAnswer);

        return ((timeToAnswer / 1000 / totalCharacter) + flashcard.getSpeedPerCharacter())/2;
    }

    private int calculateTotalCharacter(String[] source) {
        int counter = 0;
        int totalCharacter = 0;
        for (String part : source) {
            if (counter++ > 2)
                break;
            totalCharacter += getTotalCharacter(part);
        }
        return totalCharacter;
    }

    private int getTotalCharacter(String part) {
        int totalCharacter = 0;
        if (Character.isLetter(part.charAt(0)) || Character.isLetter(part.charAt(1)) || Character.isLetter(part.charAt(2))) {
            int length = part.split("\\s+").length;
            totalCharacter += length>4?4:length;
        } else {
            totalCharacter += part.trim().length();
            Log.i("Check_Part", part);
        }
        return totalCharacter;
    }

    private void loadQuestion() {
        setVisibleMode(LOADING);
        final Thread loadQuestionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                LMemoDatabase database = LMemoDatabase.getInstance(getApplicationContext());
                TestController testController = new TestController(database.wordDAO(), database.flashcardDAO());
                words = testController.prepareTest(number_of_question);
            }
        });
        loadQuestionThread.start();
        Thread updateProgressbarThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (loadQuestionThread.isAlive()) {
                    ProgressBar progressBar = findViewById(R.id.pbLoadQuestion);
                    progressBar.setProgress(progressBar.getProgress() + 1);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startTest();
                    }
                });
            }
        });
        updateProgressbarThread.start();
    }

    private void setVisibleMode(int mode) {
        switch (mode) {
            case LOADING:
                findViewById(R.id.tvMeaning).setVisibility(View.INVISIBLE);
                for (Button b : answerButton) {
                    b.setVisibility(View.INVISIBLE);
                }
                findViewById(R.id.pbLoadQuestion).setVisibility(View.VISIBLE);
                break;
            case TEST:
                findViewById(R.id.tvMeaning).setVisibility(View.VISIBLE);
                for (Button b : answerButton) {
                    b.setVisibility(View.VISIBLE);
                }
                findViewById(R.id.pbLoadQuestion).setVisibility(View.INVISIBLE);
        }
    }

    private void startTest() {
        setVisibleMode(TEST);
//        Flashcard[] allVisibleFlashcard = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO().getAllVisibleFlashcard();
//        for (Flashcard fc : allVisibleFlashcard) {
//            Log.i("FC", "\n{\n\t" + fc.getFlashcardID() + "\n\t" + fc.getAccuracy() + "\n\t" + fc.getSpeedPerCharacter() + "\n\t" + fc.getLastState() + "\n}");
//        }
        Log.i("NUMBER_OF_QUIZ", words.size() + "");
        setupQuestion();
    }

    private boolean setupQuestion() {
        if (words.size() != 0) {
            startTime = new Date();
            currentWord = words.get(0);
            Flashcard fc = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO().getFlashCardByID(currentWord.getWordID())[0];
            Log.i("FC", "\n{\n\t" + fc.getFlashcardID() + "\n\t" + fc.getAccuracy() + "\n\t" + fc.getSpeedPerCharacter() + "\n\t" + fc.getLastState() + "\n}");
            words.remove(0);
            Random r = new Random();
            int mode = r.nextInt(6) + 1;
            int position;
            switch (mode) {
                case KANJI_KANA:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKanjiWriting());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButton.get(i).setText(currentWord.getKana());
                        } else {
                            answerButton.get(i).setText(LMemoDatabase.getInstance(getApplicationContext())
                                    .wordDAO().getRandomWord(currentWord.getWordID())[0].getKana());
                        }
                    }
                    break;
                case KANJI_MEANING:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKanjiWriting());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButton.get(i).setText(currentWord.getMeaning());
                        } else {
                            answerButton.get(i).setText(LMemoDatabase.getInstance(getApplicationContext())
                                    .wordDAO().getRandomWord(currentWord.getWordID())[0].getMeaning());
                        }
                    }
                    break;
                case KANA_KANJI:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKana());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButton.get(i).setText(currentWord.getKanjiWriting());
                        } else {
                            answerButton.get(i).setText(LMemoDatabase.getInstance(getApplicationContext())
                                    .wordDAO().getRandomWord(currentWord.getWordID())[0].getKanjiWriting());
                        }
                    }
                    break;
                case KANA_MEANING:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKana());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButton.get(i).setText(currentWord.getMeaning());
                        } else {
                            answerButton.get(i).setText(LMemoDatabase.getInstance(getApplicationContext())
                                    .wordDAO().getRandomWord(currentWord.getWordID())[0].getMeaning());
                        }
                    }
                    break;
                case MEANING_KANA:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getMeaning());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButton.get(i).setText(currentWord.getKana());
                        } else {
                            answerButton.get(i).setText(LMemoDatabase.getInstance(getApplicationContext())
                                    .wordDAO().getRandomWord(currentWord.getWordID())[0].getKana());
                        }
                    }
                    break;
                case MEANING_KANJI:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getMeaning());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButton.get(i).setText(currentWord.getKanjiWriting());
                        } else {
                            answerButton.get(i).setText(LMemoDatabase.getInstance(getApplicationContext())
                                    .wordDAO().getRandomWord(currentWord.getWordID())[0].getKanjiWriting());
                        }
                    }
                    break;
            }
            return true;
        } else {
            return false;
        }
    }
}
