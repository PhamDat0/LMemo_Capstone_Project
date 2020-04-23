package com.example.lmemo_capstone_project.controller.search_controller;

import android.database.MatrixCursor;

import com.example.lmemo_capstone_project.controller.StringProcessUtilities;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.KanjiDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Kanji;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import java.util.ArrayList;
import java.util.List;

public class SearchController {
    private KanjiDAO kanjiDAO;
    private WordDAO wordDAO;
    private FlashcardDAO flashcardDAO;

    public SearchController(WordDAO wordDAO, FlashcardDAO flashcardDAO) {
        this.wordDAO = wordDAO;
        this.flashcardDAO = flashcardDAO;
    }

    public SearchController(KanjiDAO kanjiDAO) {
        this.kanjiDAO = kanjiDAO;
    }

    /**
     * @param constraint 入力した文字列
     * @param c          結果の持つためのカーソル
     * @return 結果の持つためのカーソル
     */
    public MatrixCursor insertSuggestion(CharSequence constraint, MatrixCursor c) {
        String constrain = constraint.toString();
        constrain = constrain.replace("*", "%");
        constrain = constrain.replace("?", "_");
        if ((!constrain.contains("%")) && (!constrain.contains("_"))) {
            constrain = constrain + "%";
        }
        constrain = constrain.toUpperCase();
        if (constraint == null) {
            return null;
        }
        try {
            //when a list item contains the user input, add that to the Matrix Cursor
            //this matrix cursor will be returned and the contents will be displayed
            Word[] kanji = wordDAO.getSuggestion(constrain);
            for (int i = 0; i < kanji.length; i++) {
                c.newRow().add(i).add(kanji[i].getKanjiWriting().length() == 0 ? kanji[i].getKana() : kanji[i].getKanjiWriting());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * この関数は言葉でSQLiteデータベースを検索します
     */
    public Word performSearch(String searchWord) throws WordNotFoundException {
        Word word;
        if (StringProcessUtilities.isEmpty(searchWord))
            throw new WordNotFoundException("You didn't enter anything.");
        try {
            Word[] words = wordDAO.getWords(searchWord);
            word = bestFit(words, searchWord);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new WordNotFoundException("That word does not exist.");
        }
        return word;
    }

    private Word bestFit(Word[] words, String searchWord) {
        Word result = words[0];

        for (Word word : words) {
            if (checkForBestFit(word, searchWord)) {
                result = word;
                break;
            }
        }
        return result;
    }

    private boolean checkForBestFit(Word word, String searchWord) {
        String[] kanjiWritings = word.getKanjiWriting().split("/");
        String[] kanaWritings = word.getKana().split("/");
        String[] meanings = word.getMeaning().split("/");
        for (String kanjiWriting : kanjiWritings) {
            if (searchWord.equals(kanjiWriting.trim()))
                return true;
        }
        for (String kanaWriting : kanaWritings) {
            if (searchWord.equals(kanaWriting.trim()))
                return true;
        }
        for (String meaning : meanings) {
            if (searchWord.equals(meaning.trim()))
                return true;
        }
        return false;
    }


    /**
     * @param word 追加する言葉IDを持っているオブジェクト
     * この関数はフラッシュカードをSQLiteに追加します
     */
    public void addWordToFlashcard(Word word) {
        Flashcard flashcard = new Flashcard();
        if (word.getWordID() != -1) {
            Flashcard[] checkingID = flashcardDAO.getFlashCardByID(word.getWordID());
            if (checkingID.length == 0) {
                flashcard.setFlashcardID(word.getWordID());
                flashcard.setAccuracy(0);
                flashcard.setSpeedPerCharacter(10);
                flashcard.setLastState(1);
                flashcard.setKanaLength(word.getKana().split("/")[0].trim().length());
                flashcardDAO.insertFlashcard(flashcard);
            } else if (checkingID[0].getLastState() == 99) {
                flashcard.setFlashcardID(checkingID[0].getFlashcardID());
                flashcard.setAccuracy(checkingID[0].getAccuracy());
                flashcard.setSpeedPerCharacter(checkingID[0].getSpeedPerCharacter());
                flashcard.setKanaLength(word.getKana().split("/")[0].trim().length());
                flashcard.setLastState(1);
                flashcardDAO.updateFlashcard(flashcard);
            }
        }
    }

    /**
     * @param enteredWord A string contains the kanji that the application needs to search
     *                    検索する漢字を持っている文字列です。
     * この関数は漢字を検索し、結果のリストに漢字の情報を追加します。
     */
    public List<Kanji> searchForKanji(String enteredWord) {
        List<Kanji> listKanji = new ArrayList<>();
        for (int i = 0; i < enteredWord.length(); i++) {
            Kanji[] kanji = kanjiDAO.getKanji(enteredWord.charAt(i) + "");
            if (kanji.length != 0 && !alreadyHasThatKanji(listKanji, kanji[0])) {
                listKanji.add(kanji[0]);
            }
        }
        return listKanji;
    }

    /**
     * @param listKanji 現在のリスト
     * @param kanji     あるかどうか確認する漢字
     * @return 現在のリストの中にはその漢字があれば、trueを返します。
     */
    private boolean alreadyHasThatKanji(List<Kanji> listKanji, Kanji kanji) {
        for (Kanji k : listKanji) {
            if (k.getKanji().equalsIgnoreCase(kanji.getKanji())) {
                return true;
            }
        }
        return false;
    }
}

