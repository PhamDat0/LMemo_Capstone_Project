package com.example.lmemo_capstone_project.view.home_activity.comment_view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;

import java.util.ArrayList;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private ListView listViewComment;
    private ArrayList<Comment> listComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

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
