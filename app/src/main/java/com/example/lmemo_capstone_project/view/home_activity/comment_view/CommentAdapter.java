package com.example.lmemo_capstone_project.view.home_activity.comment_view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.comment_controller.CommentController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.RewardDAO;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.model.Comment;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.ProgressDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends BaseAdapter {

    private static final int UPVOTE = 0;
    private static final int DOWNVOTE = 1;
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
        setActionForButton(holder, comment, currentUser, userComment, upvoterList, downvoterList);
        return convertView;
    }

    private void setActionForButton(ViewHolder holder, final Comment comment, User currentUser, User userComment, List<String> upvoterList, List<String> downvoterList) {
        holder.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = (Note) aContext.getIntent().getSerializableExtra("note");
                Intent intent = new Intent(aContext, AddCommentActivity.class);
                intent.putExtra("mode", AddCommentActivity.IN_EDITING_MODE);
                intent.putExtra("note", note);
                intent.putExtra("comment", comment);
                aContext.startActivityForResult(intent, CommentActivity.EDIT_COMMENT_REQUEST_CODE);
            }
        });
        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentController commentController = new CommentController(aContext);
                if (InternetCheckingController.isOnline(aContext)) {
                    Log.w("AddCommentActivity", "OnlineID adapter: " + comment.getCommentID());
                    commentController.deleteCommentFromFB(comment);
                    updateUI(comment);
                } else {
                    Toast.makeText(aContext, "There is no internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.ibtUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteComment(UPVOTE, comment);
            }
        });
        holder.tvLikeNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteComment(UPVOTE, comment);
            }
        });
        holder.ibtDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteComment(DOWNVOTE, comment);
            }
        });
        holder.tvDislikeNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteComment(DOWNVOTE, comment);
            }
        });
    }
    private void updateUI(Comment comment) {
        commentList.remove(comment);
        this.notifyDataSetChanged();
    }
    private void setTextForCommentInfo(ViewHolder holder, User creator, Reward reward, Comment comment) {
        if (creator.isGender()) {
            holder.tvUser.setText(creator.getDisplayName());
            holder.tvUser.setTextColor(Color.BLUE);
        } else {
            holder.tvUser.setText(creator.getDisplayName());
            holder.tvUser.setTextColor(Color.MAGENTA);
        }
        holder.tvCommentContent.setText(comment.getContent());
        holder.tvReward.setText(reward.getRewardName());
    }

    private void setTextNumberForUpvoteAndDownVote(ViewHolder holder, List<String> upvoterList, List<String> downvoterList, User currentUser) {
        if (upvoterList.contains(currentUser.getUserID())) {
            SpannableString spanString = new SpannableString(upvoterList.size() + "");
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            holder.tvLikeNumbers.setText(spanString);
            holder.tvLikeNumbers.setTextColor(Color.BLUE);
        } else {
            SpannableString spanString = new SpannableString(upvoterList.size() + "");
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            holder.tvLikeNumbers.setText(spanString);
            holder.tvLikeNumbers.setTextColor(Color.BLACK);
        }
        if (downvoterList.contains(currentUser.getUserID())) {
            SpannableString spanString = new SpannableString(downvoterList.size() + "");
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            holder.tvDislikeNumbers.setText(spanString);
            holder.tvDislikeNumbers.setTextColor(Color.BLUE);
        } else {
            SpannableString spanString = new SpannableString(downvoterList.size() + "");
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            holder.tvDislikeNumbers.setText(spanString);
            holder.tvDislikeNumbers.setTextColor(Color.BLACK);
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
        holder.tvLikeNumbers = convertView.findViewById(R.id.tvlikeNumbers);
        holder.tvDislikeNumbers = convertView.findViewById(R.id.tvdislikeNumbers);
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
        TextView tvLikeNumbers;
        TextView tvDislikeNumbers;
        TextView tvComment;
        ImageButton ibDelete;
        ImageButton ibEdit;
        ImageButton ibtUpvote;
        ImageButton ibtDownvote;
        ImageButton btViewComment;
    }

    private void voteComment(int mode, Comment comment) {
        User currentUser = LMemoDatabase.getInstance(aContext).userDAO().getLocalUser()[0];
        if (InternetCheckingController.isOnline(aContext)) {
            Log.i("Vote_success", "Has internet");
            CommentController commentController = new CommentController(aContext);
            ProgressDialog instance = ProgressDialog.getInstance();
            switch (mode) {
                case UPVOTE:
                    if (!comment.getUpvoters().contains(currentUser.getUserID())) {
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
        } else {
            Toast.makeText(aContext, "There is no internet", Toast.LENGTH_LONG).show();
        }
    }
}
