package com.example.lmemo_capstone_project.controller.test_controller;

import android.view.View;

import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import java.util.Date;

public interface AnswerHandleable {
    public void updateFlashcard(View answerContainer, Word currentWord, Date startTime, String question);

    public int getRandomMode(Word currentWord);
}
