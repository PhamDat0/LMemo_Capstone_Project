package com.example.lmemo_capstone_project.controller.note_controller;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class GetNoteControllerTest {
    private final String CONTENT_EXPECTED1 = "漢字が得意です";
    private final String CONTENT_EXPECTED2 = "どこだ";
    private final long WORD_ASSOCIATE1_ID = 1213170;
    private final long WORD_ASSOCIATE2_ID = 1473460;
    private String USER_ID_EXPECTED;
    private GetNoteController getNoteController;
    private UserDAO userDAO;

    @Before
    public void initTestController() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LMemoDatabase instance = LMemoDatabase.getInstance(appContext);
        getNoteController = new GetNoteController(instance.noteDAO(), instance.noteOfWordDAO());
        userDAO = instance.userDAO();
        USER_ID_EXPECTED = userDAO.getLocalUser()[0].getUserID();
    }

    /**
     * Precondition:
     * The current user has 2 notes
     * In the 1st note, there is 1 word with ID WORD_ASSOCIATE1_ID and the note content is CONTENT_EXPECTED1
     * In the 2nd note, there is 2 words with ID WORD_ASSOCIATE1_ID and WORD_ASSOCIATE2_ID, and the note content is CONTENT_EXPECTED2
     * Both note should be private
     */
    @Test
    public void getOfflineNote() {
        List<Note> offlineNote = getNoteController.getOfflineNote(GetNoteController.GET_OFFLINE_NOTE_PRIVATE, userDAO.getLocalUser()[0].getUserID());
        Note note1 = new Note(), note2 = new Note();
        try {
            assertEquals(offlineNote.size(), 2);
            note1 = offlineNote.get(0);
            note2 = offlineNote.get(1);
        } catch (Exception e) {
            fail();
        }
        assertEquals(note1.getCreatorUserID(), USER_ID_EXPECTED);
        assertEquals(note2.getCreatorUserID(), USER_ID_EXPECTED);
        assertFalse(note1.isPublic());
        assertFalse(note2.isPublic());
        assertEquals(note1.getNoteContent(), CONTENT_EXPECTED1);
        assertEquals(note2.getNoteContent(), CONTENT_EXPECTED2);
        assertEquals(note1.getWordList().size(), 1);
        assertEquals((long) note1.getWordList().get(0), WORD_ASSOCIATE1_ID);
        assertEquals(note2.getWordList().size(), 2);
        assertEquals((long) note2.getWordList().get(0), WORD_ASSOCIATE1_ID);
        assertEquals((long) note2.getWordList().get(1), WORD_ASSOCIATE2_ID);
    }
}