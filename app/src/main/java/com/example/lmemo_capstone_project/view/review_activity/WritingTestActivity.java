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
import com.example.lmemo_capstone_project.controller.TestController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class WritingTestActivity extends AppCompatActivity {

    private static final int LOADING = 1;
    private static final int TEST = 2;
    private static final int MEANING_KANJI = 1;
    private static final int MEANING_KANA = 2;
    private static final int MEANING_CHARACTER_LIMIT = 60;
    private int numberOfQuestion;
    private List<Word> words;
    private Word currentWord;
    private Date startTime;

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

    /**
     * @param v ビューオブジェクト。ユーザーの答えを持っているボタン
     *          この関数はユーザーが選んだボタンにより、練習情報をデータベースに更新します。
     */
    private void saveNewValueOfFlashcard(View v) {
        FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO();
        Flashcard flashcard = flashcardDAO.getFlashCardByID(currentWord.getWordID())[0];
        flashcard.setSpeedPerCharacter(getSpeedPerCharacterBasedOnAnswer(flashcard, v));
        flashcard.setAccuracy(getAccuracyBasedOnAnswer(flashcard, v));
        flashcardDAO.updateFlashcard(flashcard);
        Log.i("FC", "\n{\n\t" + flashcard.getFlashcardID() + "\n\t" + flashcard.getAccuracy() + "\n\t" + flashcard.getSpeedPerCharacter() + "\n\t" + flashcard.getLastState() + "\n}");
        Log.i("SAVE_VALUE", "Success");
    }

    /**
     * @param flashcard 聞いているフラッシュカード
     * @param v         ビューオブジェクト。ユーザーの答えを持っているボタン
     * @return ユーザーがその答えで受けることができる最も高い精度
     */
    private double getAccuracyBasedOnAnswer(Flashcard flashcard, View v) {
        double accuracy = calculateBestAccuracyForTheAnswer() * 0.5 + flashcard.getAccuracy() * 0.5;
        return accuracy > 100 ? 100 : accuracy;
    }

    /**
     * @return ユーザーがその答えで受けることができる最も高い精度
     * この関数は正しい答えを取リ、ユーザーが入力した答えと比べます。
     */
    private double calculateBestAccuracyForTheAnswer() {
        EditText etAnswer = findViewById(R.id.etAnswer);
        String[] correctAnswer = (etAnswer.getHint().toString()
                .equals(getString(R.string.kana_test_hint)) ? currentWord.getKana() : currentWord.getKanjiWriting()).split("/");
        String[] answer = etAnswer.getText().toString().split("/");
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

    /**
     * @param a １つ目の文字列
     * @param b ２つ目の文字列
     * @return aとbは何パーセント同じか返します。
     */
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

    /**
     * @param flashcard 聞いている言葉に相当するフラッシュカード
     * @param v         ビューオブジェクト。ユーザーの答えを持っているボタン
     * @return このフラッシュカードでは、1つの字を読む時間は何時間かを返します。
     * ユーザーが選ぶための時間を割り出し、文字が何個あるを数えり、時間を文字数で割ります。
     */
    private double getSpeedPerCharacterBasedOnAnswer(Flashcard flashcard, View v) {
        String question = ((TextView) findViewById(R.id.tvMeaning)).getText().toString();
        double timeToAnswer = new Date().getTime() - startTime.getTime();
        String answer = ((EditText) findViewById(R.id.etAnswer)).getText().toString();

        int totalCharacter = 0;
        totalCharacter += calculateTotalCharacter(question);
        totalCharacter += answer.length();
        Log.i("Total_Character", totalCharacter + "");
        return ((timeToAnswer / 1000 / totalCharacter) * 0.5 + flashcard.getSpeedPerCharacter()) * 0.5;
    }

    /**
     * @param source 文字を数える文字列
     * @return 文字数
     * ユーザーはあまりすべてを読まないので、２，３パートを数えるだけです。２，３パートに別れ、それぞれ数えます。
     * 入力速度を計るためにユーザーの答えをすべて数えます。
     */
    private int calculateTotalCharacter(String source) {
        String[] partOfSource = source.split("/");
        int counter = 0;
        int totalCharacter = 0;
        for (String part : partOfSource) {
            if (counter++ > 2)
                break;
            totalCharacter += getTotalCharacter(part.trim());
        }
        return totalCharacter;
    }

    /**
     * @param part 文字を数える文字列
     * @return 文字数
     * 英語の文字を読むための時間は日本の文字を読むための時間に比べて短いので、割り出し方は文字を数える文字列によリ違います。
     * 英語なら、単語数を数えます。日本語なら、文字数を数える。
     * 英語の言葉は長い説明の場合、４個だけ返します。
     */
    private int getTotalCharacter(String part) {
        int totalCharacter = 0;
        if (part.length() != 0) {
            if (Character.isLetter(part.charAt(0)) || Character.isLetter(part.charAt(1))) {
                int length = part.split("\\s+").length;
                totalCharacter += length > 4 ? 4 : length;
            } else {
                totalCharacter += part.trim().length();
                Log.i("Check_Part", part);
            }
        }
        return totalCharacter;
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
                LMemoDatabase database = LMemoDatabase.getInstance(getApplicationContext());
                TestController testController = new TestController(database.wordDAO(), database.flashcardDAO());
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
