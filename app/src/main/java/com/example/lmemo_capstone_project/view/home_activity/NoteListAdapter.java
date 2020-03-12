package com.example.lmemo_capstone_project.view.home_activity;

import android.app.Activity;
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
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.relationship_classes.UserWithNotes;


import java.util.ArrayList;

public class NoteListAdapter extends BaseAdapter {

    private Activity aContext;
    private ArrayList<Note> listNote;
    private LayoutInflater layoutInflater;
    private FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(aContext).flashcardDAO();
//    private int pos;


    public NoteListAdapter(Activity aContext, ArrayList<Note> listNote) {
        this.aContext = aContext;
        this.listNote = listNote;
        layoutInflater = LayoutInflater.from(aContext);
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

        holder.tvUser.setText(listNote.get(position).getCreatorUserID());

        holder.tvNoteContent.setText(listNote.get(position).getNoteContent());

//        holder.tvReward.setText(listNote.get(position));

        return convertView;
    }


    static class ViewHolder {
        TextView tvUser;
        TextView tvReward;
        TextView tvNoteContent;
    }
}
