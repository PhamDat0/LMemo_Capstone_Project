package com.example.lmemo_capstone_project.view.home_activity.comment_view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.comment_controller.CommentController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.RewardDAO;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends BaseAdapter {

    private List<Comment> commentList;
    private Activity aContext;
    private final Map<String, User> listUserMap;
    private RewardDAO rewardDAO;

    public CommentAdapter(List<Comment> commentList, Map<String, User> listUserMap, Activity aContext) {
        this.commentList = commentList;
        this.aContext = aContext;
        this.listUserMap = listUserMap;
        rewardDAO = LMemoDatabase.getInstance(aContext).rewardDAO();
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(aContext).inflate(R.layout.activity_note_list_adapter, null);
            holder = getHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User currentUser = LMemoDatabase.getInstance(aContext).userDAO().getLocalUser()[0];
        Comment comment = commentList.get(position);
        User userComment = listUserMap.get(comment.getUserID());
        List<String> upvoterList = comment.getUpvoters() == null ? new ArrayList<String>() : comment.getUpvoters();
        List<String> downvoterList = comment.getDownvoters() == null ? new ArrayList<String>() : comment.getDownvoters();
        Log.d("debug",userComment.getContributionPoint()+"");
        Reward reward = rewardDAO.getBestReward(Math.max(userComment.getContributionPoint(), 1))[0];
        boolean isCreator = userComment.getUserID().equalsIgnoreCase(currentUser.getUserID());
//        String reward = "aaaaaa";

//        holder.tvUser.setText();
        setTextForCommentInfo(holder, userComment, reward, comment);
        setTextNumberForUpvoteAndDownVote(holder, upvoterList, downvoterList, currentUser);
        setOwnerButtonVisible(holder, isCreator ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }

    private void setTextForCommentInfo(ViewHolder holder, User creator, Reward reward, Comment comment) {
        if (creator.isGender()) {
            holder.tvUser.setText(String.format("    %s", creator.getDisplayName()));
            holder.tvUser.setTextColor(Color.BLUE);
        } else {
            holder.tvUser.setText(String.format("    %s", creator.getDisplayName()));
            holder.tvUser.setTextColor(Color.MAGENTA);
        }
        holder.tvCommentContent.setText(String.format("    %s", comment.getContent()));
        holder.tvReward.setText(reward.getRewardName());
    }

    private void setTextNumberForUpvoteAndDownVote(ViewHolder holder, List<String> upvoterList, List<String> downvoterList, User currentUser) {
        if (upvoterList.contains(currentUser.getUserID())) {
            SpannableString spanString = new SpannableString(upvoterList.size() + "");
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            holder.tvlikeNumbers.setText(spanString);
            holder.tvlikeNumbers.setTextColor(Color.BLUE);
        } else {
            SpannableString spanString = new SpannableString(upvoterList.size() + "");
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            holder.tvlikeNumbers.setText(spanString);
            holder.tvlikeNumbers.setTextColor(Color.BLACK);
        }
        if (downvoterList.contains(currentUser.getUserID())) {
            SpannableString spanString = new SpannableString(downvoterList.size() + "");
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            holder.tvdislikeNumbers.setText(spanString);
            holder.tvdislikeNumbers.setTextColor(Color.BLUE);
        } else {
            SpannableString spanString = new SpannableString(downvoterList.size() + "");
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            holder.tvdislikeNumbers.setText(spanString);
            holder.tvdislikeNumbers.setTextColor(Color.BLACK);
        }
    }

    private ViewHolder getHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.tvUser = convertView.findViewById(R.id.tvUser);
        holder.tvReward = convertView.findViewById(R.id.tvReward);
        holder.tvCommentContent = convertView.findViewById(R.id.tvNoteContent);
//        holder.tvNoteContent.setMovementMethod(new ScrollingMovementMethod());
        holder.ibDelete = convertView.findViewById(R.id.ibDeleteNote);
        holder.ibEdit = convertView.findViewById(R.id.ibEditNote);
        holder.ibtUpvote = convertView.findViewById(R.id.btUpvote);
        holder.tvlikeNumbers = convertView.findViewById(R.id.tvlikeNumbers);
        holder.tvdislikeNumbers = convertView.findViewById(R.id.tvdislikeNumbers);
        holder.tvComment = convertView.findViewById(R.id.tvComment);
        holder.ibtDownvote = convertView.findViewById(R.id.btDownvote);
        holder.btViewComment = convertView.findViewById(R.id.btViewComment);
        holder.btViewComment.setVisibility(View.INVISIBLE);
        holder.tvComment.setVisibility(View.INVISIBLE);
        return holder;
    }

    private void setOwnerButtonVisible(ViewHolder holder, int mode) {
        holder.ibDelete.setVisibility(mode);
        holder.ibEdit.setVisibility(mode);
    }

    private static class ViewHolder {
        TextView tvUser;
        TextView tvReward;
        TextView tvCommentContent;
        TextView tvlikeNumbers;
        TextView tvdislikeNumbers;
        TextView tvComment;
        ImageButton ibDelete;
        ImageButton ibEdit;
        ImageButton ibtUpvote;
        ImageButton ibtDownvote;
        ImageButton btViewComment;
    }

//    private void voteComment(int mode, int position){
//        User currentUser = LMemoDatabase.getInstance(aContext).userDAO().getLocalUser()[0];
//        if (InternetCheckingController.isOnline(aContext)) {
//            Log.i("Vote_success", "Has internet");
//            CommentController commentController = new CommentController(aContext);
//            Comment comment = null;
//            ProgressDialog instance = ProgressDialog.getInstance();
//            switch (mode){
//                case UPVOTE:
//                    if (comment.getUpvoters().contains(currentUser.getUserID())){
//                        instance.show(aContext);
//                        try {
//                            Log.i("Vote_success", "Start perform");
//                            commentController.upvoteComment(comment);
//                        } catch (Exception e) {
//                            Toast.makeText(aContext, e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    break;
//                case DOWNVOTE:
//                    if (!comment.getDownvoters().contains(currentUser.getUserID())) {
//                        instance.show(aContext);
//                        try {
//                            Log.i("Vote_success", "Start perform");
//                            commentController.downvoteComment(comment);
//                        } catch (Exception e) {
//                            Toast.makeText(aContext, e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    break;
//                default:
//                    throw new UnsupportedOperationException("There is no such mode");
//            }
//        }
//        else {
//            Toast.makeText(aContext, "There is no internet", Toast.LENGTH_LONG).show();
//        }
//    }
}
