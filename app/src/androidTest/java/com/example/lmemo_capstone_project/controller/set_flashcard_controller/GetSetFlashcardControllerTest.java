package com.example.lmemo_capstone_project.controller.set_flashcard_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GetSetFlashcardControllerTest {
    private final int NUMBER_OF_SET_EXPECTED = 2;
    private final String NAME_EXPECTED_1 = "dat's set";
    private final String NAME_EXPECTED_2 = "聞く";
    private final String USER_ID_EXPECTED_1 = "J4seeIS7j7d7wmbi1rfuuB6AyYv2";
    private final String USER_ID_EXPECTED_2 = "WRNc0f75K9hBWiNEPeAO6oEF6n02";
    private final int NUMBER_OF_FC_EXPECTED_1 = 11;
    private final int NUMBER_OF_FC_EXPECTED_2 = 1;

    private GetSetFlashcardController getSetFlashcardController;

    @Before
    public void initTestController() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        getSetFlashcardController = new GetSetFlashcardController(appContext);
    }

    @Test
    public void getOfflineSet() {
        List<SetFlashcard> offlineSet = getSetFlashcardController.getOfflineSet();
        SetFlashcard setFlashcard1 = new SetFlashcard();
        SetFlashcard setFlashcard2 = new SetFlashcard();
        try {
            assertEquals(offlineSet.size(), NUMBER_OF_SET_EXPECTED);
            setFlashcard1 = offlineSet.get(0);
            setFlashcard2 = offlineSet.get(1);
        } catch (Exception e) {
            fail();
        }
        assertEquals(setFlashcard1.getSetName(), NAME_EXPECTED_1);
        assertEquals(setFlashcard2.getSetName(), NAME_EXPECTED_2);
        assertEquals(setFlashcard1.getCreatorID(), USER_ID_EXPECTED_1);
        assertEquals(setFlashcard2.getCreatorID(), USER_ID_EXPECTED_2);
        assertEquals(setFlashcard1.getWordID().size(), NUMBER_OF_FC_EXPECTED_1);
        assertEquals(setFlashcard2.getWordID().size(), NUMBER_OF_FC_EXPECTED_2);
    }
}