package com.example.lmemo_capstone_project.view.home_activity.comment_view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.comment_controller.CommentController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.User;

public class AddCommentActivity extends AppCompatActivity {
    public static final int IN_ADDING_MODE = 0;
    public static final int IN_EDITING_MODE = 1;

    private TextView tvNoteContent;
    private EditText etCommentContent;
    private Button btSaveComment;
    private Button btCancel;
    private int mode;
    private Note note;
    private User currentUser;
    private Comment comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        setupReferences();
        setupAction();
    }

    private void setupAction() {
        btSaveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetCheckingController.isOnline(getApplicationContext())) {
                    String commentContent = etCommentContent.getText().toString();
                    CommentController commentController = new CommentController(getApplicationContext());
                    if (commentContent.trim().length() != 0) {
                        switch (mode) {
                            case IN_ADDING_MODE:
                                if (!currentUser.getUserID().equalsIgnoreCase("GUEST")) {
                                    commentController.addComment(note, currentUser, commentContent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "You must logged in first.", Toast.LENGTH_LONG).show();
                                }
                                break;
                            case IN_EDITING_MODE:
                                commentController.editComment(comment, commentContent);
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), "There is no such mode.", Toast.LENGTH_LONG).show();
                        }
                        finish();
                    } else {
                        etCommentContent.setError("This must not be empty");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "There are no internet connections", Toast.LENGTH_LONG).show();
                }
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupReferences() {
        tvNoteContent = findViewById(R.id.tvNoteContent);
        etCommentContent = findViewById(R.id.etCommentContent);
        btSaveComment = findViewById(R.id.btSaveComment);
        btCancel = findViewById(R.id.btCancel);
        mode = getIntent().getIntExtra("mode", IN_ADDING_MODE);
        note = (Note) getIntent().getSerializableExtra("note");
        tvNoteContent.setText(note.getNoteContent());
        currentUser = LMemoDatabase.getInstance(getApplicationContext()).userDAO().getLocalUser()[0];
        if (mode == IN_EDITING_MODE) {
            comment = (Comment) getIntent().getSerializableExtra("comment");
            etCommentContent.setText(comment.getContent());
        }
    }
}