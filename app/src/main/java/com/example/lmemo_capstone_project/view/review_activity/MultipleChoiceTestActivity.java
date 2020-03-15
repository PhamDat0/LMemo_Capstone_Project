package com.example.lmemo_capstone_project.view.review_activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.controller.test_controller.TestController;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MultipleChoiceTestActivity extends AppCompatActivity {

    private static final int LOADING = 1;
    private static final int TEST = 2;
    public static final int KANJI_KANA = 1;
    public static final int KANJI_MEANING = 2;
    public static final int KANA_KANJI = 3;
    public static final int KANA_MEANING = 4;
    public static final int MEANING_KANA = 5;
    public static final int MEANING_KANJI = 6;
    private int numberOfQuestion;
    private List<Word> words;
    private List<Button> answerButtons;
    private Word currentWord;
    private Date startTime;
    private TextToSpeech textToSpeech;
    private int MEANING_CHARACTER_LIMIT = 60;
    private TestController testController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_test);
        Intent intent = getIntent();
        WordDAO wordDAO = LMemoDatabase.getInstance(getApplicationContext()).wordDAO();
        FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO();
        testController = new TestController(wordDAO, flashcardDAO, TestController.MULTIPLE_CHOICE_MODE);
        numberOfQuestion = intent.getIntExtra(getString(R.string.number_of_questions), 0);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.JAPAN);
                }
            }
        });
        answerButtons = new ArrayList<>();
        answerButtons.add((Button) findViewById(R.id.btAnswer1));
        answerButtons.add((Button) findViewById(R.id.btAnswer2));
        answerButtons.add((Button) findViewById(R.id.btAnswer3));
        answerButtons.add((Button) findViewById(R.id.btAnswer4));
        for (Button b : answerButtons) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answer(v);
                }
            });
        }
        loadQuestion();
    }

    /**
     * @param v ビューオブジェクト。ユーザーの答えを持っているボタン
     *          この関数はフラッシュカードの練習情報を更新し、言葉情報を表示します。
     */
    private void answer(View v) {
        Log.i("USER_ANSWER", ((Button) v).getText().toString());
        saveNewValueOfFlashcard(v);
        showResultDialog();
    }

    /**
     * この関数は質問のリストからの一つの質問を取り、UIを更新します。質問を取れなければ、テストを終わります。
     */
    private void endCurrentQuestion() {
        if (!setupQuestion()) {
            setResult(HomeActivity.TEST_FLASHCARD_REQUEST_CODE);
            finish();
        }
    }

    /**
     * この関数は聞いている言葉の情報をダイアログに表示します。
     */
    private void showResultDialog() {
        final Dialog container = new Dialog(MultipleChoiceTestActivity.this);
        container.setContentView(R.layout.fragment_word_searching);
        container.setTitle("Word information:");
        textToSpeech.speak(currentWord.getKana().split("/")[0].trim(), TextToSpeech.QUEUE_FLUSH, null, null);
        ((TextView) container.findViewById(R.id.tvKana)).setText("[ " + currentWord.getKana() + " ]");
        ((TextView) container.findViewById(R.id.tvKanji)).setText("  " + currentWord.getKanjiWriting());
        ((TextView) container.findViewById(R.id.tvMeaning)).setText(" . " + currentWord.getMeaning().replace("\n", "\n . "));
        ((TextView) container.findViewById(R.id.tvPartOfSpeech)).setText(" * " + currentWord.getPartOfSpeech());
        ImageButton btPronunciation = container.findViewById(R.id.btPronunciation);
        btPronunciation.setVisibility(View.VISIBLE);
        btPronunciation.findViewById(R.id.btPronunciation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(currentWord.getKana().split("/")[0].trim(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
        //ユーザーがこのダイアログを消したら、他の質問を始めます。
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
        String answer = ((Button) v).getText().toString();
        testController.updateFlashcard(answer, currentWord, startTime, question);
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
                for (Button b : answerButtons) {
                    b.setVisibility(View.INVISIBLE);
                }
                findViewById(R.id.pbLoadQuestion).setVisibility(View.VISIBLE);
                break;
            case TEST:
                findViewById(R.id.tvMeaning).setVisibility(View.VISIBLE);
                for (Button b : answerButtons) {
                    b.setVisibility(View.VISIBLE);
                }
                findViewById(R.id.pbLoadQuestion).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * テストの制度にUIを変更し、一つ目の質問を聞きます。
     */
    private void startTest() {
        setVisibleMode(TEST);
//        Flashcard[] allVisibleFlashcard = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO().getAllVisibleFlashcard();
//        for (Flashcard fc : allVisibleFlashcard) {
//            Log.i("FC", "\n{\n\t" + fc.getFlashcardID() + "\n\t" + fc.getAccuracy() + "\n\t" + fc.getSpeedPerCharacter() + "\n\t" + fc.getLastState() + "\n}");
//        }
//        Log.i("NUMBER_OF_QUIZ", words.size()+"");
        int[] group = new int[4];
        group[0] = 0;
        group[1] = 0;
        group[2] = 0;
        group[3] = 0;
        for (Word word : words)
            group[LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO().getFlashCardByID(word.getWordID())[0].getLastState() - 1]++;
        Log.i("PERCENT_1", group[0] / (double) words.size() * 100 + "");
        Log.i("PERCENT_2", group[1] / (double) words.size() * 100 + "");
        Log.i("PERCENT_3", group[2] / (double) words.size() * 100 + "");
        Log.i("PERCENT_4", group[3] / (double) words.size() * 100 + "");
        setupQuestion();
    }

    /**
     * @return 質問がなくなった場合はfalseを返します。
     * この関数は質問のリストの言葉数を数え、ゼロならfalseを返します。他の場合は、読解速度を割り出すために
     * 始める時間を保存します。それから、一つの言葉を選び、UIを更新し、その言葉をリストから削除し、trueを返します。
     */
    private boolean setupQuestion() {
        if (words.size() != 0) {
            startTime = new Date();
            currentWord = words.get(0);
            Flashcard fc = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO().getFlashCardByID(currentWord.getWordID())[0];
            Log.i("FC", "\n{\n\t" + fc.getFlashcardID() + "\n\t" + fc.getAccuracy() + "\n\t" + fc.getSpeedPerCharacter() + "\n\t" + fc.getLastState() + "\n}");
            words.remove(0);
            int mode = testController.getRandomMode(currentWord);
            Log.i("KANJI_TEST", currentWord.getKanjiWriting() == null ? "null" : currentWord.getKanjiWriting());
            Word[] selection = testController.getWordsForMultipleChoiceSelection(currentWord.getWordID());
            switch (mode) {
                case KANJI_KANA:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKanjiWriting());
                    for (int i = 0; i < 4; i++) {
                        answerButtons.get(i).setText(selection[i].getKana());
                    }
                    break;
                case KANJI_MEANING:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKanjiWriting());
                    for (int i = 0; i < 4; i++) {
                        answerButtons.get(i).setText(selection[i].getMeaning());
                    }
                    break;
                case KANA_KANJI:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKana());
                    for (int i = 0; i < 4; i++) {
                        answerButtons.get(i).setText((selection[i].getKanjiWriting() == null || selection[i].getKanjiWriting().length() == 0) ? "No Kanji" : selection[i].getKanjiWriting());
                    }
                    break;
                case KANA_MEANING:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKana());
                    for (int i = 0; i < 4; i++) {
                        answerButtons.get(i).setText(selection[i].getMeaning());
                    }
                    break;
                case MEANING_KANA:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getMeaning());
                    for (int i = 0; i < 4; i++) {
                        answerButtons.get(i).setText(selection[i].getKana());
                    }
                    break;
                case MEANING_KANJI:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getMeaning());
                    for (int i = 0; i < 4; i++) {
                        answerButtons.get(i).setText((selection[i].getKanjiWriting() == null || selection[i].getKanjiWriting().length() == 0) ? "No Kanji" : selection[i].getKanjiWriting());
                    }
                    break;
            }
            return true;
        } else {
            return false;
        }
    }
}
