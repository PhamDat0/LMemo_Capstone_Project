package com.example.lmemo_capstone_project.controller.test_controller;

import android.util.Log;

import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.controller.test_controller.answer_handler.AnswerHandleable;
import com.example.lmemo_capstone_project.controller.test_controller.answer_handler.MultipleChoiceAnswerHandler;
import com.example.lmemo_capstone_project.controller.test_controller.answer_handler.WritingAnswerHandler;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.flashcard_view.review_activity.MultipleChoiceTestActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TestController {
    public static final int MULTIPLE_CHOICE_MODE = 1;
    public static final int WRITING_MODE = 2;
    private AnswerHandleable answerHandler;
    private List<Flashcard> allFlashcards;
    private List<List<Word>> listOfListFlashcard;
    private WordDAO wordDAO;
    private FlashcardDAO flashcardDAO;

    public TestController(WordDAO wordDAO, FlashcardDAO flashcardDAO, int handlerMode) {
        this.wordDAO = wordDAO;
        this.flashcardDAO = flashcardDAO;
        allFlashcards = Arrays.asList(flashcardDAO.getAllVisibleFlashcard());
        listOfListFlashcard = new ArrayList<>();
        listOfListFlashcard.add(new ArrayList<Word>());
        listOfListFlashcard.add(new ArrayList<Word>());
        listOfListFlashcard.add(new ArrayList<Word>());
        listOfListFlashcard.add(new ArrayList<Word>());
        switch (handlerMode) {
            case MULTIPLE_CHOICE_MODE:
                answerHandler = new MultipleChoiceAnswerHandler(wordDAO, flashcardDAO);
                break;
            case WRITING_MODE:
                answerHandler = new WritingAnswerHandler(wordDAO, flashcardDAO);
        }
    }

    /**
     * @param numberOfQuestion 質問数
     * @return 聞く言葉のリスト
     * この関数はテストの準備します。
     * 1. フラッシュカードをKMeanで分類します。
     * 2. 質問数がゼロの場合はそれを１にします。
     * 3. テストの質問リストを作ります。フラッシュカードのLastStateが高ければ高いほど選ばれる確率が低くなります。
     */
    public List<Word> prepareTest(int numberOfQuestion) {
        restateFlashcard();

        List<Word> result = new ArrayList<>();

        if (numberOfQuestion <= 0) {
            numberOfQuestion = 1;
        }

        int size = 0;
        for (List list : listOfListFlashcard) {
            size += list.size();
        }

        if (numberOfQuestion > size) {
            for (List list : listOfListFlashcard) {
                result.addAll(list);
            }
        } else {
            Random random = new Random();
            while (result.size() < numberOfQuestion) {
                int type = random.nextInt(25375) + 1;
                try {
                    if (type < 15626) {
                        Log.i("GROUP_1", listOfListFlashcard.get(0).size() + "");
                        result.add(getWordFrom(listOfListFlashcard.get(0)));
                    } else if (type < 21876) {
                        Log.i("GROUP_2", listOfListFlashcard.get(1).size() + "");
                        result.add(getWordFrom(listOfListFlashcard.get(1)));
                    } else if (type < 24375) {
                        Log.i("GROUP_3", listOfListFlashcard.get(2).size() + "");
                        result.add(getWordFrom(listOfListFlashcard.get(2)));
                    } else {
                        Log.i("GROUP_4", listOfListFlashcard.get(3).size() + "");
                        result.add(getWordFrom(listOfListFlashcard.get(3)));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    //この例外はそのステートにカードがないときに起こるので、何かする必要がない。
                }
            }
        }
        return result;
    }

    /**
     * 1. 例の重心のリストを作ります。
     * 2. フラッシュカードをKMeanで分類します。
     * 3. フラッシュカードをLastStateに相当するリストに追加します。
     */
    private void restateFlashcard() {
        List<Flashcard> featuredFlashcard = new ArrayList<>();
        setupFeaturedFlashcard(featuredFlashcard);
        List<List<Flashcard>> classifiedFlashcard = calculate(featuredFlashcard);
        int state = 1;
        for (List<Flashcard> flashcardList : classifiedFlashcard) {
            List<Word> tmp = listOfListFlashcard.get(state - 1);
            for (Flashcard flashcard : flashcardList) {
                flashcard.setLastState(state);
                flashcardDAO.updateFlashcard(flashcard);
                tmp.add(wordDAO.getWordWithID(flashcard.getFlashcardID())[0]);
            }
            state++;
        }
    }

    /**
     * @param featuredFlashcard 例の重心のリスト
     *                          例の重心のリストを作ります。
     */
    private void setupFeaturedFlashcard(List<Flashcard> featuredFlashcard) {
        Flashcard f1 = new Flashcard();
        f1.setKanaLength(7);
        f1.setSpeedPerCharacter(4);
        f1.setAccuracy(0);
        f1.setLastState(1);
        featuredFlashcard.add(f1);
        Flashcard f2 = new Flashcard();
        f2.setKanaLength(7);
        f2.setSpeedPerCharacter(2.83);
        f2.setAccuracy(33.33);
        f2.setLastState(2);
        featuredFlashcard.add(f2);
        Flashcard f3 = new Flashcard();
        f3.setKanaLength(7);
        f3.setSpeedPerCharacter(1.67);
        f3.setAccuracy(66.67);
        f3.setLastState(3);
        featuredFlashcard.add(f3);
        Flashcard f4 = new Flashcard();
        f4.setKanaLength(7);
        f4.setSpeedPerCharacter(0.5);
        f4.setAccuracy(100);
        f4.setLastState(4);
        featuredFlashcard.add(f4);
    }

    /**
     * @param words 同じステートであるフラッシュカードのリスト
     * @return リストの中での無作為なフラッシュカード
     * @throws ArrayIndexOutOfBoundsException リストの中にフラッシュカードがない。
     * この関数は同じステートであるフラッシュカードのリストから無作為にフラッシュカードを１つを取り、
     * そのフラッシュカードをリストから削除します。
     */
    private Word getWordFrom(List<Word> words) throws ArrayIndexOutOfBoundsException {
        if (words.size() == 0)
            throw new ArrayIndexOutOfBoundsException();
        Random random = new Random();
        int selectedWordPosition = random.nextInt(words.size());
        Word result = words.get(selectedWordPosition);
        words.remove(selectedWordPosition);
        return result;
    }

    /**
     * @param featuredFlashcard 例の重心のリスト
     * @return ４つの同じステートであるフラッシュカードのリストを持っているリスト
     * KMeanの実現
     */
    private List<List<Flashcard>> calculate(List<Flashcard> featuredFlashcard) {
        List previousFeaturedCard;
        List<List<Flashcard>> classifiedFlashcard;
        do {
            classifiedFlashcard = classify(featuredFlashcard, allFlashcards);
            previousFeaturedCard = featuredFlashcard;
            featuredFlashcard = adjust(featuredFlashcard, classifiedFlashcard);
        } while (compareTwoList(previousFeaturedCard, featuredFlashcard));
        return classifiedFlashcard;
    }

    private List classify(List<Flashcard> featuredFlashcard, List<Flashcard> flashcards) {
        List<List<Flashcard>> result = new ArrayList<>();
        for (int i = 0; i < featuredFlashcard.size(); i++) {
            result.add(new ArrayList());
        }
        for (Flashcard f : flashcards) {
            int index = findTheGroup(f, featuredFlashcard);
            result.get(index).add(f);
        }
        return result;
    }

    private int findTheGroup(Flashcard f, List<Flashcard> featuredFlashcard) {
        double min = Double.MAX_VALUE;
        int count = 0;
        int index = 0;
        for (Flashcard featuredCard : featuredFlashcard) {
            double distance = calDistance(f, featuredCard);
            if (distance < min) {
                min = distance;
                index = count;
            }
            count++;
        }
        return index;
    }

    private double calDistance(Flashcard f, Flashcard featuredCard) {
        return Math.pow(f.getAccuracy()/25 - featuredCard.getAccuracy()/25, 2)
                + Math.pow(f.getLastState() - featuredCard.getLastState(), 2)
//                + Math.pow(f.getKanaLength() / 1.75 - featuredCard.getKanaLength() / 1.75, 2)
                + Math.pow(f.getSpeedPerCharacter() - featuredCard.getSpeedPerCharacter(), 2);
    }

    private List<Flashcard> adjust(List<Flashcard> featureCard, List<List<Flashcard>> clasifiedFlashcard) {
        List<Flashcard> result = new ArrayList<>();
        int type = 0;
        for (List<Flashcard> fcs : clasifiedFlashcard) {
            Flashcard newFC = new Flashcard();
            int fcsSize = fcs.size();
            for (Flashcard fc : fcs) {
                newFC.setAccuracy(newFC.getAccuracy() + fc.getAccuracy());
                newFC.setLastState(newFC.getLastState() + fc.getLastState());
                newFC.setKanaLength(newFC.getKanaLength() + fc.getKanaLength());
                newFC.setSpeedPerCharacter(newFC.getSpeedPerCharacter() + fc.getSpeedPerCharacter());
            }
            if (fcsSize != 0) {
                newFC.setAccuracy(newFC.getAccuracy() / fcsSize);
                newFC.setLastState(newFC.getLastState() / fcsSize);
                newFC.setKanaLength(newFC.getKanaLength() / fcsSize);
                newFC.setSpeedPerCharacter(newFC.getSpeedPerCharacter() / fcsSize);
            } else {
                newFC = featureCard.get(type);
            }
            result.add(newFC);
            type++;
        }
        return result;
    }

    private boolean compareTwoList(List<Flashcard> previousFeaturedCard, List<Flashcard> featuredFlashcard) {
        if (previousFeaturedCard.size() != featuredFlashcard.size()) {
            return false;
        }
        for (int i = 0; i < previousFeaturedCard.size(); i++) {
            if (!previousFeaturedCard.get(i).equals(featuredFlashcard.get(i)))
                return false;
        }
        return true;
    }

    public Word[] getWordsForMultipleChoiceSelection(int wordID) {
        return wordDAO.getRandomWord(wordID);
    }


    public int getRandomMode(Word currentWord) {
        Random r = new Random();
        int mode = r.nextInt(6) + 1;
        if (currentWord.getKanjiWriting() == null || currentWord.getKanjiWriting().length() == 0) {
            mode = r.nextInt(2) == 0 ? MultipleChoiceTestActivity.KANA_MEANING : MultipleChoiceTestActivity.MEANING_KANA;
        }
        return mode;
    }

    /**
     * @param answer      ユーザーの答え
     * @param currentWord 正しい答えを持っているWordオブジェクト
     * @param startTime   質問の始まる時間
     * @param question    質問を持っている文字列
     * この関数は答えに応じ、フラッシュカードのスタッツを更新します。
     */
    public void updateFlashcard(String answer, Word currentWord, Date startTime, String question) {
        answerHandler.updateFlashcard(answer, currentWord, startTime, question);
    }
}
