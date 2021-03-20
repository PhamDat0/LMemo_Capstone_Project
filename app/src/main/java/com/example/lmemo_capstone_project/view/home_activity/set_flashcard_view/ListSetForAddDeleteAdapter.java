package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.internet_checking_controller.InternetCheckingController;
import com.example.lmemo_capstone_project.controller.set_flashcard_controller.SetFlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.SetFlashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.ProgressDialog;

import java.util.List;

class ListSetForAddDeleteAdapter extends BaseAdapter {
    private final List<SetFlashcard> setFlashcards;
    private final FragmentActivity aContext;
    private final Word word;
    private ViewHolder holder;

    public ListSetForAddDeleteAdapter(FragmentActivity activity, List<SetFlashcard> setFlashcards, Word word) {
        this.aContext = activity;
        this.setFlashcards = setFlashcards;
        this.word = word;
    }

    @Override
    public int getCount() {
        return setFlashcards.size();
    }

    @Override
    public Object getItem(int position) {
        return setFlashcards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(aContext).inflate(R.layout.activity_set_flashcard_list_with_checkbox_adapter, null);
            holder = getHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SetFlashcard setFlashcard = setFlashcards.get(position);

        setupContent(holder, setFlashcard, word);
        setActionForButtons(setFlashcard, holder);
        return convertView;
    }

    private void setActionForButtons(final SetFlashcard setFlashcard, final ViewHolder holder) {
        holder.cbFlashcardBelongToSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.cbFlashcardBelongToSet.isChecked();
                if (!setFlashcard.isPublic()) {
                    updateSet(setFlashcard, isChecked);
                } else {
                    if (InternetCheckingController.isOnline(aContext)) {
                        ProgressDialog.getInstance().show(aContext);
                        updateSet(setFlashcard, isChecked);
                    } else {
                        notifyNoInternet();
                    }
                }
            }
        });
    }

    private void updateSet(SetFlashcard setFlashcard, boolean isChecked) {
        final SetFlashcardController setFlashcardController = new SetFlashcardController(aContext);
        if (isChecked) {
            setFlashcard.getWordID().add((long) word.getWordID());
        } else {
            Log.i("HAS_OR_NOT", "" +
                    setFlashcard.getWordID().remove((long) word.getWordID())
            );
        }
        try {
            setFlashcardController.updateSet(setFlashcard, setFlashcard.getSetName(), setFlashcard.isPublic(), setFlashcard.getWordID());
        } catch (Exception e) {
            ProgressDialog.getInstance().dismiss();
            setFlashcard.getWordID().add((long) word.getWordID());
            Toast.makeText(aContext, e.getMessage(), Toast.LENGTH_LONG).show();
            holder.cbFlashcardBelongToSet.setChecked(!isChecked);
            notifyDataSetChanged();
        }
    }

    private void notifyNoInternet() {
        Toast.makeText(aContext, "There is no internet, you cannot change public set!", Toast.LENGTH_LONG).show();
    }

    private void setupContent(ViewHolder holder, SetFlashcard setFlashcard, Word word) {
        holder.tvSetName.setText(setFlashcard.getSetName());
        holder.cbFlashcardBelongToSet.setChecked(setFlashcard.getWordID().contains((long) word.getWordID()));
    }

    private ViewHolder getHolder(View convertView) {
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.tvSetName = convertView.findViewById(R.id.tvSetName);
        viewHolder.cbFlashcardBelongToSet = convertView.findViewById(R.id.cbFlashcardBelongToSet);

        return viewHolder;

    }

    private class ViewHolder {
        TextView tvSetName;
        CheckBox cbFlashcardBelongToSet;
    }
}
