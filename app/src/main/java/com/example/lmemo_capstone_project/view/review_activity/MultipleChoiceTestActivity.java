package com.example.lmemo_capstone_project.view.review_activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;
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
    private int numberOfQuestion;
    private List<Word> words;
    private List<Button> answerButtons;
    private Word currentWord;
    private Date startTime;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_test);
        Intent intent = getIntent();
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
        ((TextView) container.findViewById(R.id.tvMeaning)).setText(" . " + currentWord.getMeaning());
        ((TextView) container.findViewById(R.id.tvPartOfSpeech)).setText(" * " + currentWord.getPartOfSpeech());
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
     * この関数はユーザーが選んだボタンにより、練習情報をデータベースに更新します。
     */
    private void saveNewValueOfFlashcard(View v) {
        FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(getApplicationContext()).flashcardDAO();
        Flashcard flashcard = flashcardDAO.getFlashCardByID(currentWord.getWordID())[0];
        flashcard.setSpeedPerCharacter(getSpeedPerCharacterBasedOnAnswer(flashcard, v));
        flashcard.setAccuracy(getAccuracyBasedOnAnswer(flashcard, v));
        flashcardDAO.updateFlashcard(flashcard);
        Log.i("SAVE_VALUE", "Success");
    }

    /**
     * @param flashcard 聞いている言葉に相当するフラッシュカード
     * @param v ビューオブジェクト。ユーザーの答えを持っているボタン
     * @return フラッシュカードの精度
     * この関数はユーザーの答えが正しいか確認し、新しい精度を返します。
     */
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

    /**
     * @param v ビューオブジェクト。ユーザーの答えを持っているボタン
     * @return ユーザーの答えは正しかったら、trueを返します。
     */
    private boolean isRightAnswer(View v) {
        Button btAnswer = (Button) v;
        String answer = btAnswer.getText().toString();
        return answer.equals(currentWord.getKana())
                || answer.equals(currentWord.getKanjiWriting())
                || answer.equals(currentWord.getMeaning());
    }

    /**
     * @param flashcard 聞いている言葉に相当するフラッシュカード
     * @param v ビューオブジェクト。ユーザーの答えを持っているボタン
     * @return このフラッシュカードでは、1つの字を読む時間は何時間かを返します。
     * ユーザーが選ぶための時間を割り出し、文字が何個あるを数えり、時間を文字数で割ります。
     */
    private double getSpeedPerCharacterBasedOnAnswer(Flashcard flashcard, View v) {
        double timeToAnswer = new Date().getTime() - startTime.getTime();
        String question = ((TextView) findViewById(R.id.tvMeaning)).getText().toString();
        String answer = ((Button) v).getText().toString();

        int totalCharacter = 0;
        totalCharacter += calculateTotalCharacter(question);
        totalCharacter += calculateTotalCharacter(answer);

        return ((timeToAnswer / 1000 / totalCharacter) + flashcard.getSpeedPerCharacter())/2;
    }

    /**
     * @param source 文字を数える文字列
     * @return 文字数
     * ユーザーはあまりすべてを読まないので、２，３パートを数えるだけです。２，３パートに別れ、それぞれ数えます。
     */
    private int calculateTotalCharacter(String source) {
        String[] partOfSource = source.split("/");
        int counter = 0;
        int totalCharacter = 0;
        for (String part : partOfSource) {
            if (counter++ > 2)
                break;
            totalCharacter += getTotalCharacter(part);
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
        if (Character.isLetter(part.charAt(0)) || Character.isLetter(part.charAt(1)) || Character.isLetter(part.charAt(2))) {
            int length = part.split("\\s+").length;
            totalCharacter += length>4?4:length;
        } else {
            totalCharacter += part.trim().length();
            Log.i("Check_Part", part);
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
     * 制度により、UIを変更します。
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
        Log.i("NUMBER_OF_QUIZ", words.size() + "");
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
            Random r = new Random();
            int mode = r.nextInt(6) + 1;
            if (currentWord.getKanjiWriting() == null || currentWord.getKanjiWriting().length() == 0) {
                mode = r.nextInt(2) == 0 ? KANA_MEANING : MEANING_KANA;
            }
            Log.i("KANJI_TEST", currentWord.getKanjiWriting() == null ? "null" : currentWord.getKanjiWriting());
            int position;
            Word[] selection = LMemoDatabase.getInstance(getApplicationContext())
                    .wordDAO().getRandomWord(currentWord.getWordID());
            switch (mode) {
                case KANJI_KANA:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKanjiWriting());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButtons.get(i).setText(currentWord.getKana());
                        } else {
                            answerButtons.get(i).setText(selection[i].getKana());
                        }
                    }
                    break;
                case KANJI_MEANING:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKanjiWriting());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButtons.get(i).setText(currentWord.getMeaning());
                        } else {
                            answerButtons.get(i).setText(selection[i].getMeaning());
                        }
                    }
                    break;
                case KANA_KANJI:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKana());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButtons.get(i).setText(currentWord.getKanjiWriting());
                        } else {
                            answerButtons.get(i).setText((selection[i].getKanjiWriting() == null || selection[i].getKanjiWriting().length() == 0) ? "No Kanji" : selection[i].getKanjiWriting());
                        }
                    }
                    break;
                case KANA_MEANING:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getKana());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButtons.get(i).setText(currentWord.getMeaning());
                        } else {
                            answerButtons.get(i).setText(selection[i].getMeaning());
                        }
                    }
                    break;
                case MEANING_KANA:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getMeaning());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButtons.get(i).setText(currentWord.getKana());
                        } else {
                            answerButtons.get(i).setText(selection[i].getKana());
                        }
                    }
                    break;
                case MEANING_KANJI:
                    ((TextView) findViewById(R.id.tvMeaning)).setText(currentWord.getMeaning());
                    position = r.nextInt(4);
                    for (int i = 0; i < 4; i++) {
                        if (i == position) {
                            answerButtons.get(i).setText(currentWord.getKanjiWriting());
                        } else {
                            answerButtons.get(i).setText((selection[i].getKanjiWriting() == null || selection[i].getKanjiWriting().length() == 0) ? "No Kanji" : selection[i].getKanjiWriting());
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
