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

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.model.Comment;
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

        loadAChosenNote();
    }

    private void loadAChosenNote() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String user = extras.getString("user");
            String reward = extras.getString("reward");
            String content = extras.getString("noteContent");
            Boolean gender = extras.getBoolean("gender");
            int upvoter = extras.getInt("like");
            int downvoter = extras.getInt("dislike");
//            Boolean isCreator = extras.getBoolean("isCreator");
            String noteID = extras.getString("noteID");
            String userID = extras.getString("userID");

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
        }
    }

    private void setButtonInvisible(int mode) {
        ibDeleteNote.setVisibility(mode);
        ibEditNote.setVisibility(mode);
    }

    private void loadCommentToUI() {
//        listViewComment = findViewById(R.id.commentListView);
//        CommentAdapter commentAdapter = new CommentAdapter();

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
