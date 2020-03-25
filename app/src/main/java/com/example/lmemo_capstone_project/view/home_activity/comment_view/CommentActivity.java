package com.example.lmemo_capstone_project.view.home_activity.comment_view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.comment_controller.GetCommentController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;

import java.util.ArrayList;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    public static final int ADD_COMMENT_REQUEST_CODE = 103;
    public static final int EDIT_COMMENT_REQUEST_CODE = 104;

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
    private Note note;
    private User creator;


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
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");
        creator = (User) intent.getSerializableExtra("creator");
        loadAChosenNoteAndComment();
        onButtonClick();
    }

    private String getOnlineNoteID() {
        Bundle extras = getIntent().getExtras();
        String noteOnlineID = "0";
        if (extras != null) {
            noteOnlineID = extras.getString("noteOnlineID");
            Log.d("NOTEONLINEID", noteOnlineID);
        }
        Log.d("NOTEONLINEID_OUTSIDE", noteOnlineID);
        return noteOnlineID;
    }

    private void loadAChosenNoteAndComment() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String user = extras.getString("user");
            String reward = extras.getString("reward");
            String content = extras.getString("noteContent");
            Boolean gender = extras.getBoolean("gender");
//            int upvoter = extras.getInt("like");
//            int downvoter = extras.getInt("dislike");
//            Boolean isCreator = extras.getBoolean("isCreator");
//            String noteOnlineID = extras.getString("noteOnlineID");
            note = new Note();
            note.setOnlineID(getOnlineNoteID());
            note.setNoteContent(content);
            tvUser.setText(user);
            tvReward.setText(reward);
            tvNoteContent.setText(content);
            if (gender) {
                tvUser.setTextColor(Color.BLUE);
            } else {
                tvUser.setTextColor(Color.MAGENTA);
            }

//            tvLikeNumber.setText("" + upvoter);
//            tvDislikeNumber.setText("" + downvoter);
            setButtonInvisible(View.INVISIBLE);
            loadCommentToUI(getOnlineNoteID());
        }
    }

    private void onButtonClick() {
        tvAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasInternet()) {
                    if (!isGuest()) {
                        Intent intent = prepareIntentForCreateCommentActivity(AddCommentActivity.IN_ADDING_MODE);
                        startActivityForResult(intent, ADD_COMMENT_REQUEST_CODE);
                    } else {
                        notifyLogin();
                    }
                } else {
                    notifyNoInternet();
                }
            }
        });
    }

    private Intent prepareIntentForCreateCommentActivity(int mode) {
        Intent intent = new Intent(getApplicationContext(), AddCommentActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("creator", creator);
        intent.putExtra("mode", mode);
        return intent;
    }

    private void notifyLogin() {
        Toast.makeText(getApplicationContext(), "You must login first", Toast.LENGTH_LONG).show();
    }

    private boolean isGuest() {
        return LMemoDatabase.getInstance(getApplicationContext()).userDAO().getLocalUser()[0].isGuest();
    }

    private boolean hasInternet() {
        return InternetCheckingController.isOnline(getApplicationContext());
    }

    private void notifyNoInternet() {
        Toast.makeText(getApplicationContext(), "There is no internet", Toast.LENGTH_LONG).show();
    }

    private void setButtonInvisible(int mode) {
        ibDeleteNote.setVisibility(mode);
        ibEditNote.setVisibility(mode);
        tvDislikeNumber.setVisibility(mode);
        tvLikeNumber.setVisibility(mode);
        btDownvote.setVisibility(mode);
        btUpvote.setVisibility(mode);
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
}
