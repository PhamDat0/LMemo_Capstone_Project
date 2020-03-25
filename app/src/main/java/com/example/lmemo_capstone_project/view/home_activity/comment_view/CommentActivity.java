package com.example.lmemo_capstone_project.view.home_activity.comment_view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.CannotPerformFirebaseRequest;
import com.example.lmemo_capstone_project.controller.comment_controller.GetCommentController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.note_controller.NoteController;
import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;
import com.example.lmemo_capstone_project.view.home_activity.note_view.NoteListAdapter;

import java.util.ArrayList;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private ListView listViewComment;
    private ArrayList<Comment> listComment;
    private TextView tvUser;
    private TextView tvReward;
    private TextView tvNoteContent;
    private TextView tvLikeNumber;
    private TextView tvDislikeNumber;
    private ImageButton ibEditNote;
    private ImageButton ibDeleteNote;
    private TextView tvAddComment;

    private static final int UPVOTE = 1;
    private static final int DOWNVOTE = 2;
    private ImageButton btUpvote;
    private ImageButton btDownvote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        tvUser = findViewById(R.id.tvUser);
        tvReward = findViewById(R.id.tvReward);
        tvNoteContent = findViewById(R.id.tvNoteContent);
        tvLikeNumber = findViewById(R.id.tvlikeNumbers);
        tvDislikeNumber = findViewById(R.id.tvdislikeNumbers);
        ibDeleteNote = findViewById(R.id.ibDeleteNote);
        ibEditNote = findViewById(R.id.ibEditNote);
        btDownvote = findViewById(R.id.btDownvote);
        btUpvote = findViewById(R.id.btUpvote);
        tvAddComment = findViewById(R.id.tvAddComment);
        listViewComment = findViewById(R.id.commentListView);
        loadAChosenNoteAndComment();
        onButtonClick();
    }

    private String getOnlineNoteID() {
        Bundle extras = getIntent().getExtras();
        String noteOnlineID="0";
        if (extras != null) {
            noteOnlineID = extras.getString("noteOnlineID");
            Log.d("NOTEONLINEID",noteOnlineID);
        }
        Log.d("NOTEONLINEID_OUTSIDE",noteOnlineID);
        return noteOnlineID;
    }

    private void loadAChosenNoteAndComment() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String user = extras.getString("user");
            String reward = extras.getString("reward");
            String content = extras.getString("noteContent");
            Boolean gender = extras.getBoolean("gender");
            int upvoter = extras.getInt("like");
            int downvoter = extras.getInt("dislike");
//            Boolean isCreator = extras.getBoolean("isCreator");
//            String noteOnlineID = extras.getString("noteOnlineID");
//            Log.d("NOTEONLINEID",noteOnlineID);
            String userID = extras.getString("userID");
//            int position = extras.getInt("position");
            tvUser.setText(user);
            tvReward.setText(reward);
            tvNoteContent.setText(content);
            if(gender) {
                tvUser.setTextColor(Color.BLUE);
            } else {
                tvUser.setTextColor(Color.MAGENTA);
            }

            tvLikeNumber.setText(""+upvoter);
            tvDislikeNumber.setText(""+downvoter);
            setButtonInvisible(View.INVISIBLE);
            loadCommentToUI(getOnlineNoteID());
        }
    }

    private void onButtonClick() {
        tvAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                vote(UPVOTE);
            }
        });
        btDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onclick","Already Click");
//                vote(DOWNVOTE);
            }
        });
    }

    private void setButtonInvisible(int mode) {
        ibDeleteNote.setVisibility(mode);
        ibEditNote.setVisibility(mode);
    }

    private void loadCommentToUI(String noteID) {
        GetCommentController getCommentController = new GetCommentController(this);
        getCommentController.getAllCommentFromFirebase(noteID);
    }

    public void updateUI(ArrayList<Comment> listComment, Map<String, User> listUserMap) {
        for (Comment comment : listComment) {
            Log.i("UPVOTER_DOWNVOTER1", comment.getUpvoters() + "" + comment.getDownvoters());
        }
        CommentAdapter commentAdapter = new CommentAdapter(listComment, listUserMap, this);
        listViewComment.setAdapter(commentAdapter);
        ProgressDialog instance = ProgressDialog.getInstance();
        instance.dismiss();
    }

//    private Note getOnlineNoteFromDB() {
//        NoteDAO noteDAO = LMemoDatabase.getInstance(getApplicationContext()).noteDAO();
//        Note note;
//        try {
//            Log.d("ONLINE_NOTE_FROM_DB",noteDAO.getNotesByOnlineID(getOnlineNoteID())[0]+" ");
//            note = noteDAO.getNotesByOnlineID(getOnlineNoteID())[0];
////            note.setUpvoterList(note.getUpvoterList());
////            note.setDownvoterList(note.getDownvoterList());
////            note.setWordList(note.getWordList());
//        } catch (ArrayIndexOutOfBoundsException e) {
//            note = new Note();
//            Log.d("Error_DB",e.getMessage());
//        }
//        return note;
//    }

//    private void vote(int mode) {
//        User currentUser = LMemoDatabase.getInstance(getApplicationContext()).userDAO().getLocalUser()[0];
//        if (InternetCheckingController.isOnline(getApplicationContext())) {
//            Log.i("Vote_success", "Has internet");
//            NoteController noteController = new NoteController(getApplicationContext());
//            Note note = getOnlineNoteFromDB();
//            Log.d("ONLINE_NOTE",note.getNoteContent()+"//////");
//            ProgressDialog instance = ProgressDialog.getInstance();
//            switch (mode) {
//                case UPVOTE:
//                    if (!note.getUpvoterList().contains(currentUser.getUserID())) {
//                        instance.show(getApplicationContext());
//                        try {
//                            Log.i("Vote_success", "Start perform");
//                            noteController.upvote(note);
//                        } catch (CannotPerformFirebaseRequest cannotPerformFirebaseRequest) {
//                            Toast.makeText(getApplicationContext(), cannotPerformFirebaseRequest.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    break;
//                case DOWNVOTE:
//                    if (!note.getDownvoterList().contains(currentUser.getUserID())) {
//                        instance.show(getApplicationContext());
//                        try {
//                            Log.i("Vote_success", "Start perform");
//                            noteController.downvote(note);
//                        } catch (CannotPerformFirebaseRequest cannotPerformFirebaseRequest) {
//                            Toast.makeText(getApplicationContext(), cannotPerformFirebaseRequest.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    break;
//                default:
//                    throw new UnsupportedOperationException("There is no such mode");
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "There is no internet", Toast.LENGTH_LONG).show();
//        }
//    }
}
