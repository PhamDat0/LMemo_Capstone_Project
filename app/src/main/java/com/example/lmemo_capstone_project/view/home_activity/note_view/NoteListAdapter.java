package com.example.lmemo_capstone_project.view.home_activity.note_view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.NoteDAO;
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
        return getNoteFromDB(position);
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
        Note note = getNoteFromDB(position);
        Log.i("UPVOTER_DOWNVOTER", note.getUpvoterList() + "" + note.getDownvoterList());
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
        Reward reward = rewardDAO.getBestReward(Math.max(creator.getContributionPoint(), 1))[0];

        holder.tvReward.setText(reward.getRewardName());

        List<String> upvoterList = note.getUpvoterList();
        List<String> downvoterList = note.getDownvoterList();

        if (upvoterList != null) {
            if (upvoterList.contains(currentUser.getUserID())) {
                SpannableString spanString = new SpannableString(upvoterList.size() + "");
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                holder.tvlikeNumbers.setText(spanString);
//                holder.tvlikeNumbers.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                holder.tvlikeNumbers.setTextColor(Color.BLUE);
            } else {
                SpannableString spanString = new SpannableString(upvoterList.size() + "");
                spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
                holder.tvlikeNumbers.setText(spanString);
//                holder.btUpvote.getBackground().setColorFilter(null);
                holder.tvlikeNumbers.setTextColor(Color.BLACK);
            }
        }
        if (downvoterList != null) {
            if (downvoterList.contains(currentUser.getUserID())) {
                SpannableString spanString = new SpannableString(downvoterList.size() + "");
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                holder.tvdislikeNumbers.setText(spanString);
//                holder.btDownvote.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                holder.tvdislikeNumbers.setTextColor(Color.BLUE);
            } else {
                SpannableString spanString = new SpannableString(downvoterList.size() + "");
                spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
                holder.tvdislikeNumbers.setText(spanString);
//                holder.btDownvote.getBackground().setColorFilter(null);
                holder.tvdislikeNumbers.setTextColor(Color.BLACK);
            }
        }

        if (mode == SEARCH_MODE) {
            holder.btUpvote.setVisibility(View.VISIBLE);
            holder.btDownvote.setVisibility(View.VISIBLE);
            holder.btViewComment.setVisibility(View.VISIBLE);
        } else {
            holder.btUpvote.setVisibility(View.GONE);
            holder.btDownvote.setVisibility(View.GONE);
            holder.tvlikeNumbers.setVisibility(View.GONE);
            holder.tvdislikeNumbers.setVisibility(View.GONE);
            holder.tvComment.setVisibility(View.GONE);
            holder.btViewComment.setVisibility(View.GONE);
        }


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
        holder.tvlikeNumbers = convertView.findViewById(R.id.tvlikeNumbers);
        holder.tvdislikeNumbers = convertView.findViewById(R.id.tvdislikeNumbers);
        holder.tvComment = convertView.findViewById(R.id.tvComment);
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
                Note note = getNoteFromDB(position);
                NoteController editAndDeleteNoteController = new NoteController(aContext);
                if (note.isPublic()) {
                    InternetCheckingController internetCheckingController = new InternetCheckingController();
                    if (internetCheckingController.isOnline(aContext)) {
                        Log.w("AddNoteActivity", "OnlineID adapter: " + note.getOnlineID());
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
                    if (getNoteFromDB(position).isPublic()) {
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

    private Note getNoteFromDB(int position) {
        NoteDAO noteDAO = LMemoDatabase.getInstance(aContext).noteDAO();
        Note note;
        try {
            note = noteDAO.getNotesByID(listNote.get(position).getNoteID())[0];
            note.setUpvoterList(listNote.get(position).getUpvoterList());
            note.setDownvoterList(listNote.get(position).getDownvoterList());
            note.setWordList(listNote.get(position).getWordList());
        } catch (ArrayIndexOutOfBoundsException e) {
            note = listNote.get(position);
        }
        return note;
    }

    private void vote(int mode, int position) {
        if (InternetCheckingController.isOnline(aContext)) {
            Log.i("Vote_success", "Has internet");
            NoteController noteController = new NoteController(aContext);
            Note note = getNoteFromDB(position);
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
        intent.putExtra("note", getNoteFromDB(position));
        Log.i("NOTE_ID", getNoteFromDB(position).getOnlineID() == null ? "null" : getNoteFromDB(position).getOnlineID());
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
        TextView tvlikeNumbers;
        TextView tvdislikeNumbers;
        TextView tvComment;
        ImageButton ibDelete;
        ImageButton ibEdit;
        ImageButton btUpvote;
        ImageButton btDownvote;
        ImageButton btViewComment;
        ListView lvComments;
        Button btAddComment;
    }
}
