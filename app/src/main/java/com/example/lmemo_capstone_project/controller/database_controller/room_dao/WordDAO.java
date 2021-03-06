package com.example.lmemo_capstone_project.controller.database_controller.room_dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.WordWithNotes;

import java.util.List;

@Dao
public interface WordDAO {
    @Insert
    void insertWord(Word word);

    @Update
    void updateWord(Word word);

    @Query("SELECT WordID FROM Word")
    Word[] getAllWordID();

    @Query("DELETE FROM Word")
    void deleteAllWords();

    @Query("SELECT *  FROM Word w WHERE NOT EXISTS(SELECT * FROM Flashcard WHERE FlashcardID = w.WordID) ORDER BY random() LIMIT 1")
    Word[] getDailyWord();

    @Query("SELECT * From Word Where WordID = :WordID")
    Word getOneWord(int WordID);

    @Query("SELECT * FROM Word WHERE Kana LIKE '%' || :keyword || '%' OR Kanji LIKE '%' || :keyword || '%' OR Meaning LIKE '%' || :keyword || '%' order by WordID asc")
    Word[] getWords(String keyword);

    @Query("SELECT * From Word WHERE Kana LIKE :keyword OR Kanji LIKE :keyword OR Meaning LIKE :keyword")
    Word[] getSuggestion(String keyword);

    @Query("SELECT * FROM Word WHERE WordID LIKE :keyword")
    Word[] getAWords(int keyword);

    @Query("SELECT * FROM Word order by WordID asc")
    Word[] getAllWords();

    @Query("SELECT WordID, Kana, Kanji, Meaning, PartOfSpeech FROM Word as W,Flashcard as F WHERE F.FlashcardID = W.WordID and F.LastState!=99")
    Word[] getAllFlashcard();

    @Transaction
    @Query("SELECT * FROM Word")
    List<WordWithNotes> getWordsWithNotes();

    @Query("SELECT WordID, Kana, Kanji, Meaning, PartOfSpeech FROM Word WHERE WordID=:flashcardID")
    Word[] getWordWithID(int flashcardID);

    @Query("SELECT * FROM (SELECT * FROM (SELECT WordID, Kana, Kanji, Meaning, PartOfSpeech FROM Word, Flashcard " +
            "WHERE WordID=FlashcardID AND WordID <> :correctID ORDER BY random() LIMIT 3) UNION ALL " +
            "SELECT * FROM (SELECT WordID, Kana, Kanji, Meaning, PartOfSpeech FROM Word, Flashcard " +
            "WHERE WordID=FlashcardID AND WordID = :correctID)) " +
            "ORDER BY random() LIMIT 4")
    Word[] getRandomWord(int correctID);

    @Query("SELECT * From Word WHERE Kana LIKE :s1 AND Kanji LIKE :s")
    Word[] getWords(String s, String s1);
}
