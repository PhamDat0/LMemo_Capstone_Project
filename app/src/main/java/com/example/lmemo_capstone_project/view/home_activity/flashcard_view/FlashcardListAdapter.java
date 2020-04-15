package com.example.lmemo_capstone_project.view.home_activity.flashcard_view;

import android.app.Activity;
import android.os.Bundle;
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
import com.example.lmemo_capstone_project.controller.flashcard_controller.FlashcardController;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view.AddToSetDialog;

import java.util.ArrayList;

public class FlashcardListAdapter extends BaseAdapter {

    private final FlashCardFragment flashCardFragment;
    private Activity aContext;
    private ArrayList<Word> listFlashcard;
    private FlashcardDAO flashcardDAO;
    private FlashcardController flashcardController;

    public FlashcardListAdapter(Activity aContext, ArrayList<Word> listFlashcard, FlashCardFragment flashCardFragment) {
        this.aContext = aContext;
        this.listFlashcard = listFlashcard;
        this.flashCardFragment = flashCardFragment;
        flashcardDAO = LMemoDatabase.getInstance(aContext).flashcardDAO();
//        LayoutInflater layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listFlashcard.size();
    }

    @Override
    public Object getItem(int position) {
        return listFlashcard.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        flashcardController = new FlashcardController(aContext, listFlashcard);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(aContext).inflate(R.layout.activity_flashcard_list_adapter, null);
            holder = new ViewHolder();
            holder.tvKanji = convertView.findViewById(R.id.tvKanji);
            holder.tvKana = convertView.findViewById(R.id.tvKana);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            holder.ibAddToSet = convertView.findViewById(R.id.ibAddToSet);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvKanji.setText(listFlashcard.get(position).getKanjiWriting());
        holder.tvKanji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardInfo(position, v);
            }
        });
        holder.tvKana.setText(listFlashcard.get(position).getKana());
        holder.tvKana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardInfo(position, v);
            }
        });
//        holder.btnDelete.setTag(position);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardController.delete(position);
                listFlashcard.remove(position);
                notifyDataSetChanged();
                if (flashcardDAO.getAllVisibleFlashcard().length == 0) {
                    aContext.findViewById(R.id.btReview).setVisibility(View.INVISIBLE);
                }
//                refresh();
            }
        });

        holder.ibAddToSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word = new Word();
                word.setWordID(listFlashcard.get(position).getWordID());
                showAddToSetDialog(word);
            }
        });

        return convertView;
    }

    private void showAddToSetDialog(Word word) {
        FragmentTransaction ft = flashCardFragment.getParentFragmentManager().beginTransaction();
        AddToSetDialog addToSetDialog = AddToSetDialog.newInstance(word);
        addToSetDialog.show(ft, "dialog");
    }

    /**
     * @param position: これはフラッシュカードのポジションです。
     * @param v　
     * この関数はフラッシュカードのインフォメイションを表示します。
     */
    public void flashcardInfo(int position, View v) {
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
//        WordDAO wordDAO = LMemoDatabase.getInstance(aContext).wordDAO();
//        Word flashcardDetail = wordDAO.getAWords(listFlashcard.get(position).getKana())[0];
        Word flashcardDetail = flashcardController.flashcardInfo(position);
        Bundle bundle = new Bundle();
        FlashcardInfoFragment infoFragment = new FlashcardInfoFragment();
        bundle.putSerializable("wordResult", flashcardDetail);
        infoFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.FrameFlashcard, infoFragment, "flashcard");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    static class ViewHolder {
        TextView tvKanji;
        TextView tvKana;
        ImageButton btnDelete;
        ImageButton ibAddToSet;
    }
}
