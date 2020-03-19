package com.example.lmemo_capstone_project.view.home_activity.note_view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.CannotPerformFirebaseRequest;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.RewardDAO;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.note_controller.NoteController;
import com.example.lmemo_capstone_project.model.room_db_entity.Note;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.home_activity.HomeActivity;

import java.util.List;
import java.util.Map;

public class NoteListAdapter extends BaseAdapter {
    public static final int SEARCH_MODE = 0;
    public static final int VIEW_MODE = 1;
    private static final int UPVOTE = 1;
    private static final int DOWNVOTE = 2;
    private final Map<String, User> listUserMap;
    private Activity aContext;
    private List<Note> listNote;
    //    private LayoutInflater layoutInflater;
    private RewardDAO rewardDAO;
    private int mode;
//    private int pos;


    public NoteListAdapter(Activity aContext, List<Note> listNote, Map<String, User> listUserMap, int mode) {
        this.aContext = aContext;
        this.listNote = listNote;
//        layoutInflater = LayoutInflater.from(aContext);
        this.listUserMap = listUserMap;
        this.mode = mode;
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
            holder = getHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Note note = listNote.get(position);
        User creator = listUserMap.get(note.getCreatorUserID());
//        Log.d("Debug_gender",creator.isGender()+"");
        if (creator.isGender()) {
            holder.tvUser.setText(creator.getDisplayName());
            holder.tvUser.setTextColor(Color.BLUE);
        } else {
            holder.tvUser.setText(creator.getDisplayName());
            holder.tvUser.setTextColor(Color.MAGENTA);
        }
        holder.tvNoteContent.setText(note.getNoteContent());
        Reward reward = rewardDAO.getBestReward(creator.getContributionPoint() < 1 ? 1 : creator.getContributionPoint())[0];



        setActionOnclick(holder, position);
        //ユーザーがノートを持っている場合には削除と更新できます。
        if (creator.getUserID().equalsIgnoreCase(currentUser.getUserID())) {
            Log.d("CompareID", creator.getUserID() + " / " + currentUser.getUserID() + " / " + creator.getUserID().equalsIgnoreCase(currentUser.getUserID()));
            setOwnerButtonVisible(holder, View.VISIBLE);
        } else {
            setOwnerButtonVisible(holder, View.INVISIBLE);
        }
        return convertView;
    }

    private ViewHolder getHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.tvUser = convertView.findViewById(R.id.tvUser);
        holder.tvReward = convertView.findViewById(R.id.tvReward);
        holder.tvNoteContent = convertView.findViewById(R.id.tvNoteContent);
        holder.tvNoteContent.setMovementMethod(new ScrollingMovementMethod());
        holder.ibDelete = convertView.findViewById(R.id.ibDeleteNote);
        holder.ibEdit = convertView.findViewById(R.id.ibEditNote);
        holder.btUpvote = convertView.findViewById(R.id.btUpvote);
        holder.likeNumbers = convertView.findViewById(R.id.likeNumbers);
        holder.dislikeNumbers = convertView.findViewById(R.id.dislikeNumbers);
        holder.btDownvote = convertView.findViewById(R.id.btDownvote);
        holder.btViewComment = convertView.findViewById(R.id.btViewComment);
        holder.lvComments = convertView.findViewById(R.id.lvComments);
        holder.btAddComment = convertView.findViewById(R.id.btAddComment);
        return holder;
    }

    private void setActionOnclick(ViewHolder holder, final int position) {
        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = listNote.get(position);
                NoteController editAndDeleteNoteController = new NoteController(aContext);
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
        holder.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aContext instanceof FragmentActivity) {
                    if (listNote.get(position).isPublic()) {
                        if (InternetCheckingController.isOnline(aContext)) {
                            callForEditDialog(position);
                        } else {
                            Toast.makeText(aContext, "There is no internet", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        callForEditDialog(position);
                    }
                } else {
                    throw new UnsupportedOperationException("This action is not supported");
                }
            }
        });
        holder.btUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Vote_success", "Onclick");
                vote(UPVOTE, position);
            }
        });
        holder.btDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Vote_success", "Onclick");
                vote(DOWNVOTE, position);
            }
        });
    }

    private void vote(int mode, int position) {
        if (InternetCheckingController.isOnline(aContext)) {
            Log.i("Vote_success", "Has internet");
            NoteController noteController = new NoteController(aContext);
            Note note = listNote.get(position);
            switch (mode) {
                case UPVOTE:
                    try {
                        Log.i("Vote_success", "Start perform");
                        noteController.upvote(note);
                    } catch (CannotPerformFirebaseRequest cannotPerformFirebaseRequest) {
                        Toast.makeText(aContext, cannotPerformFirebaseRequest.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
                case DOWNVOTE:
                    try {
                        Log.i("Vote_success", "Start perform");
                        noteController.downvote(note);
                    } catch (CannotPerformFirebaseRequest cannotPerformFirebaseRequest) {
                        Toast.makeText(aContext, cannotPerformFirebaseRequest.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("There is no such mode");
            }
        } else {
            Toast.makeText(aContext, "There is no internet", Toast.LENGTH_LONG).show();
        }
    }

    private void callForEditDialog(int position) {
        Intent intent = new Intent(aContext, CreateNoteActivity.class);
        intent.putExtra("mode", CreateNoteActivity.IN_EDITING_MODE);
        intent.putExtra("note", listNote.get(position));
        Log.i("NOTE_ID", listNote.get(position).getOnlineID() == null ? "null" : listNote.get(position).getOnlineID());
        aContext.startActivityForResult(intent, HomeActivity.EDIT_NOTE_REQUEST_CODE);
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
        TextView likeNumbers;
        TextView dislikeNumbers;
        ImageButton ibDelete;
        ImageButton ibEdit;
        ImageButton btUpvote;
        ImageButton btDownvote;
        ImageButton btViewComment;
        ListView lvComments;
        Button btAddComment;
    }
}
