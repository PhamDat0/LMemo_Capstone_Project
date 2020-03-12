package com.example.lmemo_capstone_project.view.review_activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.controller.test_controller.TestController;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WritingTestActivity extends AppCompatActivity {

    private static final int LOADING = 1;
    private static final int TEST = 2;
    public static final int MEANING_KANA = 1;
    public static final int MEANING_KANJI = 2;
    private static final int MEANING_CHARACTER_LIMIT = 60;
    private int numberOfQuestion;
    private List<Word> words;
    private Word currentWord;
    private Date startTime;
    private TestController testController;
    private LinearLayout belowLayout;
    private CardView aboveLayout, btAnswerCheck;
    private int height;
    private boolean isFlipped = false;
    private OvershootInterpolator interpolator = new OvershootInterpolator();
    private int duration = 300;
    private TextView txname;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_test);
        Intent intent = getIntent();
        numberOfQuestion = intent.getIntExtra(getString(R.string.number_of_questions), 0);
        findViewById(R.id.btAnswerCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer(v);
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.JAPAN);
                }
            }
        });
        WordDAO wordDAO = LMemoDatabase.getInstance(getApplicationContext()).wordDAO();
        FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO();
        testController = new TestController(wordDAO, flashcardDAO, TestController.WRITING_MODE);
        belowLayout = findViewById(R.id.belowLayout);
        aboveLayout = findViewById(R.id.aboveLayout);
        btAnswerCheck = findViewById(R.id.btAnswerCheck);
        txname = findViewById(R.id.txname);

        aboveLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //fetch height
                height = (aboveLayout.getHeight()) / 2;
            }
        });

        flipAnimation();
        loadQuestion();
    }

    //Question and answer flip
    private void flipAnimation() {
        btAnswerCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                aboveLayout.animate().setDuration(duration).setInterpolator(interpolator).translationY(height).start();
                belowLayout.animate().setDuration(duration).setInterpolator(interpolator).translationY(-1 * height)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                if (!isFlipped) {
                                    aboveLayout.setTranslationZ(-50);
                                    belowLayout.setTranslationZ(0);
                                    answer(v);
                                    txname.setText("Next");
                                } else {
                                    aboveLayout.setTranslationZ(0);
                                    belowLayout.setTranslationZ(-50);
                                    txname.setText("Check Answer");
                                }

                                aboveLayout.animate().setDuration(duration).setInterpolator(interpolator).translationY(0).start();
                                belowLayout.animate().setDuration(duration).setInterpolator(interpolator).translationY(0).start();
                                btAnswerCheck.animate().setInterpolator(interpolator).translationY(0).start();


                                isFlipped = !isFlipped;
                            }
                        })
                        .start();
            }
        });

    }

    /**
     * @param v ビューオブジェクト。ユーザーの答えを持っているボタン
     *          この関数はフラッシュカードの練習情報を更新し、言葉情報を表示します。
     */
    private void answer(View v) {
        saveNewValueOfFlashcard(v);
        showResultDialog();
    }

    /**
     * この関数は質問のリストからの一つの質問を取り、UIを更新します。質問を取れなければ、テストを終わります。
     */
    private void endCurrentQuestion() {
        ((EditText) findViewById(R.id.etAnswer)).setText("");
        if (!setupQuestion()) {
            setResult(HomeActivity.TEST_FLASHCARD_REQUEST_CODE);
            finish();
        }
    }

    /**
     * この関数は聞いている言葉の情報をダイアログに表示します。
     */
    private void showResultDialog() {
        final Dialog container = new Dialog(WritingTestActivity.this);
        container.setContentView(R.layout.fragment_word_searching);
        container.setTitle("Word information:");
        Button btPronunciation = container.findViewById(R.id.btPronunciation);
        btPronunciation.setVisibility(View.VISIBLE);
        btPronunciation.findViewById(R.id.btPronunciation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(currentWord.getKana().split("/")[0].trim(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
        textToSpeech.speak(currentWord.getKana().split("/")[0].trim(), TextToSpeech.QUEUE_FLUSH, null, null);
        ((TextView) container.findViewById(R.id.tvKana)).setText("[ " + currentWord.getKana() + " ]");
        ((TextView) container.findViewById(R.id.tvKanji)).setText("  " + currentWord.getKanjiWriting());
        ((TextView) container.findViewById(R.id.tvMeaning)).setText(" . " + currentWord.getMeaning().replace("\n", "\n . "));
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

    /**
     * @param v ビューオブジェクト。ユーザーの答えを持っているボタン
     *          この関数はユーザーが選んだボタンにより、練習情報をデータベースに更新します。
     */
    private void saveNewValueOfFlashcard(View v) {
        String question = ((TextView) findViewById(R.id.tvMeaning)).getText().toString();
        testController.updateFlashcard(findViewById(R.id.etAnswer), currentWord, startTime, question);
    }



    /**
     * この関数はUIをローディング画面にし、KMeansで分類しながらプログレスバーを更新します。KMeansで分類し
     * たら、質問を選び、テストを始めます。
     */
    private void loadQuestion() {
        setVisibleMode(LOADING);
        final Thread loadQuestionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                words = testController.prepareTest(numberOfQuestion);
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

    /**
     * @param mode UIの制度; LOADING or TEST
     *             制度により、UIを変更します。
     */
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

    /**
     * テストの制度にUIを変更し、一つ目の質問を聞きます。
     */
    private void startTest() {
        setVisibleMode(TEST);
        Flashcard[] allVisibleFlashcard = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO().getAllVisibleFlashcard();
        for (Flashcard fc : allVisibleFlashcard) {
            Log.i("FC", "\n{\n\t" + fc.getFlashcardID() + "\n\t" + fc.getAccuracy() + "\n\t" + fc.getSpeedPerCharacter() + "\n\t" + fc.getLastState() + "\n}");
        }
        Log.i("NUMBER_OF_QUIZ", words.size() + "");
        setupQuestion();
    }

    /**
     * @return 質問がなくなった場合はfalseを返します。
     * この関数は質問のリストの言葉数を数え、ゼロならfalseを返します。他の場合は、入力速度を割り出すために
     * 始める時間を保存します。それから、一つの言葉を選び、UIを更新し、その言葉をリストから削除し、trueを返します。
     */
    private boolean setupQuestion() {
        if (words.size() != 0) {
            startTime = new Date();
            currentWord = words.get(0);
            words.remove(0);
            Flashcard fc = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO().getFlashCardByID(currentWord.getWordID())[0];
            Log.i("FC", "\n{\n\t" + fc.getFlashcardID() + "\n\t" + fc.getAccuracy() + "\n\t" + fc.getSpeedPerCharacter() + "\n\t" + fc.getLastState() + "\n}");
            int mode = testController.getRandomMode(currentWord);
            switch (mode) {
                case MEANING_KANA:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getMeaning());
                    ((EditText) findViewById(R.id.etAnswer)).setHint(R.string.kana_test_hint);
                    break;
                case MEANING_KANJI:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getMeaning());
                    ((EditText) findViewById(R.id.etAnswer)).setHint(R.string.kanji_test_hint);
                    break;
            }
            return true;
        } else {
            return false;
        }
    }
}
