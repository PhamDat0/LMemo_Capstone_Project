package com.example.lmemo_capstone_project.view.home_activity.comment_view;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.CannotPerformFirebaseRequest;
import com.example.lmemo_capstone_project.controller.comment_controller.CommentController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;

import java.util.List;

public class CommentAdapter extends BaseAdapter {

    private List<Comment> commentList;
    private Activity aContext;
    private static final int UPVOTE = 1;
    private static final int DOWNVOTE = 2;

    public CommentAdapter(List<Comment> commentList, Activity aContext) {
        this.commentList = commentList;
        this.aContext = aContext;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(aContext).inflate(R.layout.activity_note_list_adapter, null);
//            holder = getHolder(convertView);
//            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return null;
    }

    private static class ViewHolder {

    }
    private void voteComment(int mode, int position){
        User currentUser = LMemoDatabase.getInstance(aContext).userDAO().getLocalUser()[0];
        if (InternetCheckingController.isOnline(aContext)) {
            Log.i("Vote_success", "Has internet");
            CommentController commentController = new CommentController(aContext);
            Comment comment = null;
            ProgressDialog instance = ProgressDialog.getInstance();
            switch (mode){
                case UPVOTE:
                    if (comment.getUpvoters().contains(currentUser.getUserID())){
                        instance.show(aContext);
                        try {
                            Log.i("Vote_success", "Start perform");
                            commentController.upvoteComment(comment);
                        } catch (Exception e) {
                            Toast.makeText(aContext, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case DOWNVOTE:
                    if (!comment.getDownvoters().contains(currentUser.getUserID())) {
                        instance.show(aContext);
                        try {
                            Log.i("Vote_success", "Start perform");
                            commentController.downvoteComment(comment);
                        } catch (Exception e) {
                            Toast.makeText(aContext, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("There is no such mode");
            }
        }
        else {
            Toast.makeText(aContext, "There is no internet", Toast.LENGTH_LONG).show();
        }
    }
}
