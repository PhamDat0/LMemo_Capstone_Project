package com.example.lmemo_capstone_project.controller.test_controller.answer_handler;

import android.util.Log;

import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import java.util.Date;

public class WritingAnswerHandler implements AnswerHandleable {
    private final WordDAO wordDAO;
    private final FlashcardDAO flashcardDAO;
    private String question;
    private String answer;
    private Word currentWord;
    private Date startTime;

    public WritingAnswerHandler(WordDAO wordDAO, FlashcardDAO flashcardDAO) {
        this.wordDAO = wordDAO;
        this.flashcardDAO = flashcardDAO;
    }

    @Override
    public void updateFlashcard(String answer, Word currentWord, Date startTime, String question) {
        this.question = question;
        this.answer = answer;
        this.currentWord = currentWord;
        this.startTime = startTime;
        Flashcard flashcard = flashcardDAO.getFlashCardByID(currentWord.getWordID())[0];
        flashcard.setSpeedPerCharacter(getSpeedPerCharacterBasedOnAnswer(flashcard, answer));
        flashcard.setAccuracy(getAccuracyBasedOnAnswer(flashcard));
        flashcardDAO.updateFlashcard(flashcard);
        Log.i("FC", "\n{\n\t" + flashcard.getFlashcardID() + "\n\t" + flashcard.getAccuracy() + "\n\t" + flashcard.getSpeedPerCharacter() + "\n\t" + flashcard.getLastState() + "\n}");
        Log.i("SAVE_VALUE", "Success");
    }

    /**
     * @param flashcard 聞いているフラッシュカード
     * @return ユーザーがその答えで受けることができる最も高い精度
     */
    private double getAccuracyBasedOnAnswer(Flashcard flashcard) {
        double accuracy = calculateBestAccuracyForTheAnswer() * 0.5 + flashcard.getAccuracy() * 0.5;
        return accuracy > 100 ? 100 : accuracy;
    }

    /**
     * @return ユーザーがその答えで受けることができる最も高い精度
     * この関数は正しい答えを取リ、ユーザーが入力した答えと比べます。
     */
    private double calculateBestAccuracyForTheAnswer() {
        String[] correctAnswer = (currentWord.getKana() + " / " + currentWord.getKanjiWriting()).split("/");
        String[] answerPart = answer.split("/");
        double bestAccuracy = 0;
        for (String partOfAnswer : answerPart) {
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
     * @param answer    ユーザーの答え
     * @return このフラッシュカードでは、1つの字を読む時間は何時間かを返します。
     * ユーザーが書くための時間を割り出し、文字が何個あるを数えり、時間を文字数で割ります。
     */
    private double getSpeedPerCharacterBasedOnAnswer(Flashcard flashcard, String answer) {
        double timeToAnswer = new Date().getTime() - startTime.getTime();
        int totalCharacter = 0;
        totalCharacter += calculateTotalCharacter(question);
        totalCharacter += answer.length();
        Log.i("Total_Character", totalCharacter + "");
        double result = ((timeToAnswer / 1000 / totalCharacter) * 0.5 + flashcard.getSpeedPerCharacter()) * 0.5;
        Log.i("SPEED_PER_CHAR", "" + (timeToAnswer / 1000 / totalCharacter) + "s");
        return result;
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
            part = part.trim();
            if (isEnglishCharacter(part.charAt(0))) {
                if (counter++ > 1)
                    break;
                totalCharacter += getTotalCharacter(part);
            } else {
                totalCharacter += getTotalCharacter(part);
                break;
            }

        }
        return totalCharacter;
    }

    private boolean isEnglishCharacter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
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
            if (isEnglishCharacter(part.charAt(0))) {
                int length = part.split("\\s+").length;
                totalCharacter += length > 2 ? 2 : length;
                Log.i("Check_Part_LATIN", part + " " + part.charAt(0));
            } else {
                totalCharacter += part.trim().length();
                Log.i("Check_Part_JAPAN", part + " " + part.charAt(0));
            }
        }
        return totalCharacter;
    }
}
