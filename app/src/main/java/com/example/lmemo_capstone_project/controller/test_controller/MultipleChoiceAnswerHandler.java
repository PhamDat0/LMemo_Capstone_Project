package com.example.lmemo_capstone_project.controller.test_controller;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.review_activity.MultipleChoiceTestActivity;

import java.util.Date;
import java.util.Random;

class MultipleChoiceAnswerHandler implements AnswerHandleable {
    private static final double SLOPE_LREGRESS = 1.691;
    private static final double INTERCEPT_LREGRESS = 0.3510;
    private static final double ACCURACY_CHANGE = 15;
    private final WordDAO wordDAO;
    private final FlashcardDAO flashcardDAO;
    private Word currentWord;
    private String question;
    private Date startTime;

    public MultipleChoiceAnswerHandler(WordDAO wordDAO, FlashcardDAO flashcardDAO) {
        this.wordDAO = wordDAO;
        this.flashcardDAO = flashcardDAO;
    }

    @Override
    public void updateFlashcard(View answerContainer, Word currentWord, Date startTime, String question) {
        this.currentWord = currentWord;
        this.question = question;
        this.startTime = startTime;
        Flashcard flashcard = flashcardDAO.getFlashCardByID(currentWord.getWordID())[0];
        flashcard.setSpeedPerCharacter(getSpeedPerCharacterBasedOnAnswer(flashcard, answerContainer));
        flashcard.setAccuracy(getAccuracyBasedOnAnswer(flashcard, answerContainer));
        Log.i("FC_AFTER", "\n{\n\t" + flashcard.getFlashcardID() + "\n\t" + flashcard.getAccuracy() + "\n\t" + flashcard.getSpeedPerCharacter() + "\n\t" + flashcard.getLastState() + "\n}");
        flashcardDAO.updateFlashcard(flashcard);
        Log.i("SAVE_VALUE", "Success");
    }

    @Override
    public int getRandomMode(Word currentWord) {
        Random r = new Random();
        int mode = r.nextInt(6) + 1;
        if (currentWord.getKanjiWriting() == null || currentWord.getKanjiWriting().length() == 0) {
            mode = r.nextInt(2) == 0 ? MultipleChoiceTestActivity.KANA_MEANING : MultipleChoiceTestActivity.MEANING_KANA;
        }
        return mode;
    }

    /**
     * @param flashcard 聞いている言葉に相当するフラッシュカード
     * @param v         ビューオブジェクト。ユーザーの答えを持っているボタン
     * @return フラッシュカードの精度
     * この関数はユーザーの答えが正しいか確認し、新しい精度を返します。
     */
    private double getAccuracyBasedOnAnswer(Flashcard flashcard, View v) {
        double accuracy;
        if (isRightAnswer(v)) {
            accuracy = flashcard.getAccuracy() + ACCURACY_CHANGE;
            return accuracy > 100 ? 100 : accuracy;
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
                || currentWord.getMeaning().contains(answer);
    }

    /**
     * @param flashcard 聞いている言葉に相当するフラッシュカード
     * @param v         ビューオブジェクト。ユーザーの答えを持っているボタン
     * @return このフラッシュカードでは、1つの字を読む時間は何時間かを返します。
     * ユーザーが選ぶための時間を割り出し、文字が何個あるを数えり、時間を文字数で割ります。
     */
    private double getSpeedPerCharacterBasedOnAnswer(Flashcard flashcard, View v) {
        double timeToAnswer = new Date().getTime() - startTime.getTime();
        String answer = ((Button) v).getText().toString();
        int totalCharacter = 0;
        totalCharacter += calculateTotalCharacter(question);
        totalCharacter += calculateTotalCharacter(answer);
        Log.i("TOTAL_CHARACTER", "" + totalCharacter);
        double result = ((timeToAnswer / 1000 / totalCharacter) * SLOPE_LREGRESS + INTERCEPT_LREGRESS + flashcard.getSpeedPerCharacter()) / 2;
        Log.i("SPEED_PER_CHAR", "" + ((timeToAnswer / 1000 / totalCharacter)) + "s");
        return result;
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
            part = part.trim();
            if (isEnglishCharacter(part.charAt(0))) {
                if (counter++ > 1)
                    break;
                if (part.contains("\n")) {
                    part = part.substring(0, part.indexOf('\n')).trim();
                }
                totalCharacter += getTotalCharacter(part);
            } else {
                totalCharacter += getTotalCharacter(part);
                break;
            }

        }
        return totalCharacter;
    }

    /**
     * @param part 文字を数える文字列
     * @return 文字数
     * 英語の文字を読むための時間は日本の文字を読むための時間に比べて短いので、割り出し方は文字を数える文字列によリ違います。
     * 英語なら、単語数を数えます。日本語なら、文字数を数える。
     * 英語の言葉は長い説明の場合、４個だけ返します。
     * 日本語の長い言葉もよくすべて読まないので、5個だけ返します。
     */
    private int getTotalCharacter(String part) {
        int totalCharacter = 0;
        if (part.length() != 0) {
            if (isEnglishCharacter(part.charAt(0))) {
                int length = part.split("\\s+").length;
                totalCharacter += length > 2 ? 2 : length;
                Log.i("Check_Part_LATIN", part + " " + part.charAt(0));
            } else {
                totalCharacter += part.trim().length();
                totalCharacter = totalCharacter > 5 ? 5 : totalCharacter;
                Log.i("Check_Part_JAPAN", part + " " + part.charAt(0));
            }
        }
        return totalCharacter;
    }

    private boolean isEnglishCharacter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
}
