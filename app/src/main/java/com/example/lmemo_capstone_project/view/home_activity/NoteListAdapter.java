package com.example.lmemo_capstone_project.view.home_activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.FlashcardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.RewardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.UserWithNotes;
import com.google.android.material.badge.BadgeDrawable;


import java.util.ArrayList;
import java.util.Map;

public class NoteListAdapter extends BaseAdapter {

    private final Map<String, User> listUserMap;
    private Activity aContext;
    private ArrayList<Note> listNote;
    private LayoutInflater layoutInflater;
    private RewardDAO rewardDAO = LMemoDatabase.getInstance(aContext).rewardDAO();
//    private int pos;



    public NoteListAdapter(Activity aContext, ArrayList<Note> listNote, Map<String, User> listUserMap) {
        this.aContext = aContext;
        this.listNote = listNote;
        layoutInflater = LayoutInflater.from(aContext);
        this.listUserMap = listUserMap;
    }
    @Override
    public int getCount() {
        return listNote.size();
    }

    @Override
    public Object getItem(int position) {
        return listNote.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(aContext).inflate(R.layout.activity_note_list_adapter, null);
            holder = new ViewHolder();
            holder.tvUser = convertView.findViewById(R.id.tvUser);
            holder.tvReward = convertView.findViewById(R.id.tvReward);
            holder.tvNoteContent = convertView.findViewById(R.id.tvNoteContent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        User creator = listUserMap.get(listNote.get(position).getCreatorUserID());
//        Log.d("Debug_gender",creator.isGender()+"");
        if(creator.isGender()) {
            holder.tvUser.setText(creator.getDisplayName());
            holder.tvUser.setTextColor(Color.BLUE);
        } else {
            holder.tvUser.setText(creator.getDisplayName());
            holder.tvUser.setTextColor(Color.MAGENTA);
        }

        holder.tvNoteContent.setText(listNote.get(position).getNoteContent());

        RewardDAO rewardDAO = LMemoDatabase.getInstance(aContext).rewardDAO();
        Reward reward = rewardDAO.getBestReward(creator.getContributionPoint()<1?1:creator.getContributionPoint())[0];
        holder.tvReward.setText(reward.getRewardName());

        return convertView;
    }


    static class ViewHolder {
        TextView tvUser;
        TextView tvReward;
        TextView tvNoteContent;
    }
}
