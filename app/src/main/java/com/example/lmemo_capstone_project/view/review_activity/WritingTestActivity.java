package com.example.lmemo_capstone_project.view.review_activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.TestController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class WritingTestActivity extends AppCompatActivity {

    private static final int LOADING = 1;
    private static final int TEST = 2;
    private static final int MEANING_KANJI = 1;
    private static final int MEANING_KANA = 2;
    private static final double ACCURACY_CHANGE = 15;
    private int number_of_question;
    private List<Word> words;
    private Word currentWord;
    private Date startTime;
    private LinearLayout belowLayout;
    private CardView aboveLayout,btAnswerCheck;
    private int height;
    private boolean isFlipped=false;
    private OvershootInterpolator interpolator=new OvershootInterpolator();
    private int duration=300;
    private TextView txname;
    private Boolean state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_test);
        Intent intent = getIntent();
        number_of_question = intent.getIntExtra(getString(R.string.number_of_questions), 0);
        findViewById(R.id.btAnswerCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer(v);
            }
        });

        belowLayout = findViewById(R.id.belowLayout);
        aboveLayout = findViewById(R.id.aboveLayout);
        btAnswerCheck = findViewById(R.id.btAnswerCheck);
        txname = findViewById(R.id.txname);

        aboveLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //fetch height
                height=(aboveLayout.getHeight())/2;
            }
        });

        flipAnimation();
        loadQuestion();
    }
    //Question and answer flip
    private void flipAnimation(){
        btAnswerCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                aboveLayout.animate().setDuration(duration).setInterpolator(interpolator).translationY(height).start();
                belowLayout.animate().setDuration(duration).setInterpolator(interpolator).translationY(-1*height)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                if(!isFlipped) {
                                    aboveLayout.setTranslationZ(-50);
                                    belowLayout.setTranslationZ(0);
                                    answer(v);
                                    txname.setText("Next");
                                }
                                else {
                                    aboveLayout.setTranslationZ(0);
                                    belowLayout.setTranslationZ(-50);
                                    txname.setText("Check Answer");
                                }

                                aboveLayout.animate().setDuration(duration).setInterpolator(interpolator).translationY(0).start();
                                belowLayout.animate().setDuration(duration).setInterpolator(interpolator).translationY(0).start();
                                btAnswerCheck.animate().setInterpolator(interpolator).translationY(0).start();


                                isFlipped=!isFlipped;
                            }
                        })
                        .start();
            }
        });

    }
    private void answer(View v) {
        saveNewValueOfFlashcard(v);
        showResultDialog();
    }

    private void endCurrentQuestion() {
        ((EditText)findViewById(R.id.etAnswer)).setText("");
        if (!setupQuestion()) {
            setResult(HomeActivity.TEST_FLASHCARD_REQUEST_CODE);
            finish();
        }
    }

    private void showResultDialog() {
        final Dialog container = new Dialog(WritingTestActivity.this);
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
        Log.i("FC", "\n{\n\t" + flashcard.getFlashcardID() + "\n\t" + flashcard.getAccuracy() + "\n\t" + flashcard.getSpeedPerCharacter() + "\n\t" + flashcard.getLastState() + "\n}");
        Log.i("SAVE_VALUE", "Success");
    }

    private double getAccuracyBasedOnAnswer(Flashcard flashcard, View v) {
        double accuracy = calculateBestAccuracyForTheAnswer()*0.5 + flashcard.getAccuracy() * 0.5;
        return accuracy>100?100:accuracy;
    }

    private double calculateBestAccuracyForTheAnswer() {
        EditText etAnswer = findViewById(R.id.etAnswer);
        String[] correctAnswer = (etAnswer.getHint().toString()
                .equals(getString(R.string.kana_test_hint)) ? currentWord.getKana() : currentWord.getKanjiWriting()).split("/");
        String[] answer = etAnswer.getText().toString().split("/");
        return calculateBestAccuracyForTheAnswer(answer, correctAnswer);
    }

    private double calculateBestAccuracyForTheAnswer(String[] answer, String[] correctAnswer) {
        double bestAccuracy = 0;
        for (String partOfAnswer : answer) {
            for (String partOfCorrectAnswer : correctAnswer) {
                double compareResult = stringCompare(partOfAnswer.trim(), partOfCorrectAnswer.trim());
                bestAccuracy = bestAccuracy > compareResult ? bestAccuracy : compareResult;
            }
        }
        Log.i("ACCURACY", bestAccuracy + "");
        return bestAccuracy;
    }

    double stringCompare(String a, String b) {
        if (a.equalsIgnoreCase(b)) //Same string, no iteration needed.
            return 100;
        if ((a.length() == 0) || (b.length() == 0)) //One is empty, second is not
        {
            return 0;
        }
        double maxLen = a.length() > b.length() ? a.length() : b.length();
        int minLen = a.length() < b.length() ? a.length() : b.length();
        int sameCharAtIndex = 0;
        for (int i = 0; i < minLen; i++) //Compare char by char
        {
            if (a.charAt(i) == b.charAt(i)) {
                sameCharAtIndex++;
            }
        }
        return sameCharAtIndex / maxLen * 100;
    }

    private double getSpeedPerCharacterBasedOnAnswer(Flashcard flashcard, View v) {
        TextView tvQuestion = findViewById(R.id.tvMeaning);
        String question = tvQuestion.getText().toString();
        Date endTime = new Date();
        double timeToAnswer = endTime.getTime() - startTime.getTime();
        Log.i("TIME_TO_ANSWER", timeToAnswer+"");
        String answer = ((EditText) findViewById(R.id.etAnswer)).getText().toString();
        String[] partOfAnswer = answer.split("/");
        String[] partOfQuestion = question.split("/");

        int totalCharacter = 0;
        totalCharacter += calculateTotalCharacter(partOfQuestion);
        totalCharacter += partOfAnswer.length;
        Log.i("Total_Character", totalCharacter + "");
        return ((timeToAnswer / 1000 / totalCharacter)*0.5 + flashcard.getSpeedPerCharacter())*0.5;
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
                findViewById(R.id.btAnswerCheck).setVisibility(View.INVISIBLE);
                findViewById(R.id.pbLoadQuestion).setVisibility(View.VISIBLE);
                break;
            case TEST:
                findViewById(R.id.tvMeaning).setVisibility(View.VISIBLE);
                findViewById(R.id.btAnswerCheck).setVisibility(View.VISIBLE);
                findViewById(R.id.pbLoadQuestion).setVisibility(View.INVISIBLE);
        }
    }

    private void startTest() {
        setVisibleMode(TEST);
        Flashcard[] allVisibleFlashcard = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO().getAllVisibleFlashcard();
        for (Flashcard fc : allVisibleFlashcard) {
            Log.i("FC", "\n{\n\t" + fc.getFlashcardID() + "\n\t" + fc.getAccuracy() + "\n\t" + fc.getSpeedPerCharacter() + "\n\t" + fc.getLastState() + "\n}");
        }
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
            int mode = r.nextInt(2) + 1;
            switch (mode) {
                case MEANING_KANA:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getMeaning());
                    ((EditText) findViewById(R.id.etAnswer)).setHint(R.string.kana_test_hint);
                    break;
                case MEANING_KANJI:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getMeaning());
                    if (currentWord.getKanjiWriting() == null || currentWord.getKanjiWriting().length() == 0) {
                        ((EditText) findViewById(R.id.etAnswer)).setHint(R.string.kana_test_hint);
                    } else {
                        ((EditText) findViewById(R.id.etAnswer)).setHint(R.string.kanji_test_hint);
                    }
                    break;
            }
            return true;
        } else {
            return false;
        }
    }
}
