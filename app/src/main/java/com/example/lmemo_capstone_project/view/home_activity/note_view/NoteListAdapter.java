package com.example.lmemo_capstone_project.view.home_activity.note_view;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.RewardDAO;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.note_controller.EditAndDeleteNoteController;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.example.lmemo_capstone_project.model.room_db_entity.User;

import java.util.List;
import java.util.Map;

public class NoteListAdapter extends BaseAdapter {

    private final Map<String, User> listUserMap;
    private Activity aContext;
    private List<Note> listNote;
    private LayoutInflater layoutInflater;
    private RewardDAO rewardDAO;
//    private int pos;


    public NoteListAdapter(Activity aContext, List<Note> listNote, Map<String, User> listUserMap) {
        this.aContext = aContext;
        this.listNote = listNote;
        layoutInflater = LayoutInflater.from(aContext);
        this.listUserMap = listUserMap;
        rewardDAO = LMemoDatabase.getInstance(aContext).rewardDAO();
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
        User currentUser = LMemoDatabase.getInstance(aContext).userDAO().getLocalUser()[0];
        if (convertView == null) {
            convertView = LayoutInflater.from(aContext).inflate(R.layout.activity_note_list_adapter, null);
            holder = new ViewHolder();
            holder.tvUser = convertView.findViewById(R.id.tvUser);
            holder.tvReward = convertView.findViewById(R.id.tvReward);
            holder.tvNoteContent = convertView.findViewById(R.id.tvNoteContent);
            holder.ibDelete = convertView.findViewById(R.id.ibDeleteNote);
            holder.ibEdit = convertView.findViewById(R.id.ibEditNote);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        User creator = listUserMap.get(listNote.get(position).getCreatorUserID());
//        Log.d("Debug_gender",creator.isGender()+"");
        if (creator.isGender()) {
            holder.tvUser.setText(creator.getDisplayName());
            holder.tvUser.setTextColor(Color.BLUE);
        } else {
            holder.tvUser.setText(creator.getDisplayName());
            holder.tvUser.setTextColor(Color.MAGENTA);
        }
        holder.tvNoteContent.setText(listNote.get(position).getNoteContent());
        Reward reward = rewardDAO.getBestReward(creator.getContributionPoint() < 1 ? 1 : creator.getContributionPoint())[0];
        holder.tvReward.setText(reward.getRewardName());
        //ユーザーがノートを持っている場合には削除と更新できます。
        if (creator.getUserID().equalsIgnoreCase(currentUser.getUserID())) {
            Log.d("CompareID", creator.getUserID() + " / " + currentUser.getUserID() + " / " + creator.getUserID().equalsIgnoreCase(currentUser.getUserID()));
            setOwnerButtonVisible(holder, View.VISIBLE);
            setActionOnclick(holder, position);
        } else {
            setOwnerButtonVisible(holder, View.INVISIBLE);
        }
        return convertView;
    }

    private void setActionOnclick(ViewHolder holder, final int position) {
        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = listNote.get(position);
                EditAndDeleteNoteController editAndDeleteNoteController = new EditAndDeleteNoteController(aContext);
                if (note.isPublic()) {
                    InternetCheckingController internetCheckingController = new InternetCheckingController();
                    if (internetCheckingController.isOnline(aContext)) {
                        editAndDeleteNoteController.deleteNote(note);
                        updateUI(position);
                    } else {
                        Toast.makeText(aContext, "There is no internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    editAndDeleteNoteController.deleteNote(note);
                    updateUI(position);
                }
            }
        });
    }

    private void updateUI(int position) {
        listNote.remove(position);
        this.notifyDataSetChanged();
    }

    private void setOwnerButtonVisible(ViewHolder holder, int mode) {
        holder.ibDelete.setVisibility(mode);
        holder.ibEdit.setVisibility(mode);
    }

    private static class ViewHolder {
        TextView tvUser;
        TextView tvReward;
        TextView tvNoteContent;
        ImageButton ibDelete;
        ImageButton ibEdit;
    }
}
