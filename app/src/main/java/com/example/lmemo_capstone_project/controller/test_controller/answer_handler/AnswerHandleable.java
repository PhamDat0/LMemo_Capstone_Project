package com.example.lmemo_capstone_project.controller.test_controller.answer_handler;

import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import java.util.Date;

public interface AnswerHandleable {
    public void updateFlashcard(String answer, Word currentWord, Date startTime, String question);
}
