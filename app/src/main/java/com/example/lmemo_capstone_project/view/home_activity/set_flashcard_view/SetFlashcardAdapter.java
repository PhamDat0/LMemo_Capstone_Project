package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.SetFlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.User;

import java.util.List;

public class SetFlashcardAdapter extends BaseAdapter {
    public static final int OFFLINE_MODE = 0;
    public static final int ONLINE_MODE = 1;

    private List<SetFlashcard> setFlashcardList;
    private Activity aContext;
    private int mode;
    private User currentUser;

    public SetFlashcardAdapter(List<SetFlashcard> setFlashcardList, Activity aContext, int mode) {
        this.setFlashcardList = setFlashcardList;
        this.aContext = aContext;
        this.mode = mode;
        currentUser = LMemoDatabase.getInstance(aContext).userDAO().getLocalUser()[0];
    }

    @Override
    public int getCount() {
        return setFlashcardList.size();
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
            convertView = LayoutInflater.from(aContext).inflate(R.layout.activity_set_flashcard_list_adapter, null);
            holder = getHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SetFlashcard setFlashcard = setFlashcardList.get(position);
        holder.tvSetName.setText(setFlashcard.getSetName());
        setVisibleForButtons(setFlashcard, holder);
        setActionForButtons(setFlashcard, holder);
        return convertView;
    }

    private void setActionForButtons(final SetFlashcard setFlashcard, ViewHolder holder) {
        final SetFlashcardController setFlashcardController = new SetFlashcardController(aContext);
        holder.swPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (InternetCheckingController.isOnline(aContext)) {
                    if (isChecked) {
                        setFlashcardController.uploadSetToFirebase(setFlashcard);
                    } else {
                        setFlashcardController.makeSetPrivate(setFlashcard);
                    }
                } else {
                    Toast.makeText(aContext, "There is no internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.ibChangeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setVisibleForButtons(SetFlashcard setFlashcard, ViewHolder holder) {
        boolean isOwner = isOwner(setFlashcard);
        holder.swPublic.setVisibility(isOwner && !setFlashcard.getCreatorID().equalsIgnoreCase("GUEST") ? View.VISIBLE : View.INVISIBLE);
        holder.swPublic.setChecked(setFlashcard.isPublic());
        holder.ibChangeWord.setVisibility(isOwner ? View.VISIBLE : View.INVISIBLE);
        holder.ibDelete.setVisibility(isOwner ? View.VISIBLE : View.INVISIBLE);
        holder.ibReview.setVisibility(isInOfflineMode() ? View.VISIBLE : View.INVISIBLE);
        holder.ibDownload.setVisibility((!isInOfflineMode() && !isOwner(setFlashcard)) ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean isInOfflineMode() {
        return mode == OFFLINE_MODE;
    }

    private boolean isOwner(SetFlashcard setFlashcard) {
        return setFlashcard.getCreatorID().equalsIgnoreCase(currentUser.getUserID());
    }

    private ViewHolder getHolder(View convertView) {
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.tvSetName = convertView.findViewById(R.id.tvSetName);
        viewHolder.swPublic = convertView.findViewById(R.id.swPublic);
        viewHolder.ibReview = convertView.findViewById(R.id.ibReview);
        viewHolder.ibChangeWord = convertView.findViewById(R.id.ibChangeWord);
        viewHolder.ibDelete = convertView.findViewById(R.id.ibDelete);
        viewHolder.ibDownload = convertView.findViewById(R.id.ibDownload);

        return viewHolder;
    }

    private static class ViewHolder {
        TextView tvSetName;
        Switch swPublic;
        ImageButton ibReview, ibChangeWord, ibDelete, ibDownload;
    }
}
