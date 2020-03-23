package com.example.lmemo_capstone_project.view.home_activity.comment_view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.model.Comment;

import java.util.List;

public class CommentAdapter extends BaseAdapter {

    private List<Comment> commentList;
    private Activity aContext;

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
}
