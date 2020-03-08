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
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import java.util.ArrayList;

public class FlashcardListAdapter extends BaseAdapter {

    private Activity aContext;
    private ArrayList<Word> listFlashcard;
    private LayoutInflater layoutInflater;
    private FlashcardDAO flashcardDAO = LMemoDatabase.getInstance(aContext).flashcardDAO();
//    private int pos;


    public FlashcardListAdapter(Activity aContext, ArrayList<Word> listFlashcard) {
        this.aContext = aContext;
        this.listFlashcard = listFlashcard;
        layoutInflater = LayoutInflater.from(aContext);
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
        ViewHolder holder;
//        pos = position;
        if (convertView == null) {
            convertView = LayoutInflater.from(aContext).inflate(R.layout.activity_flashcard_list_adapter, null);
            holder = new ViewHolder();
            holder.tvKanji = convertView.findViewById(R.id.tvKanji);
            holder.tvKana = convertView.findViewById(R.id.tvKana);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        Word w = listFlashcard.get(position);

//        Flashcard[] flashcards = flashcardDAO.getAllFlashcard();
//        int flashcardID = flashcards[0].getFlashcardID();

        holder.tvKanji.setText(listFlashcard.get(position).getKanjiWriting());
        holder.tvKanji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardInfo(position,v);
            }
        });
        holder.tvKana.setText(listFlashcard.get(position).getKana());
        holder.tvKana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardInfo(position,v);
            }
        });
//        holder.btnDelete.setTag(position);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
                listFlashcard.remove(position);
                notifyDataSetChanged();
//                refresh();
            }
        });

        return convertView;
    }

//    private void checkingAndRemoveFlashcard() {
//        Flashcard flashcard = flashcardDAO.getFlashCardByID(listFlashcard.get().getWordID())[0];
//        flashcard.setLastState(99);
//        flashcardDAO.updateFlashcard(flashcard);
//
//        notifyDataSetChanged();
//    }



    static class ViewHolder {
        TextView tvKanji;
        TextView tvKana;
        ImageButton btnDelete;
    }



//    private void alert(final int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
//        builder.setCancelable(true);
//        builder.setTitle("Delete Flashcard");
//        builder.setMessage("Are you sure?");
//        builder.setPositiveButton("Confirm",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Delete(position);
//                    }
//                });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

    /**
     *この関数フラッシュカードを削除します
     */
    private void delete(int position) {
        Log.d("myapp", position + "" + " || " + listFlashcard.get(position).getWordID());
        Flashcard flashcard = flashcardDAO.getFlashCardByID(listFlashcard.get(position).getWordID())[0];
//                        Flashcard flashcard = flashcards[0];
        flashcard.setLastState(99);
        flashcardDAO.updateFlashcard(flashcard);
        if (flashcardDAO.getAllVisibleFlashcard().length == 0) {
            aContext.findViewById(R.id.btReview).setVisibility(View.INVISIBLE);
        }
//        listFlashcard.remove(listFlashcard.get(position));
    }

//    public interface {
//        void onItemSelected(String key, String Value);
//    }s


    private void flashcardInfo(int position,View v) {
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        WordDAO wordDAO = LMemoDatabase.getInstance(aContext).wordDAO();
        Word flashcardDetail = wordDAO.getAWords(listFlashcard.get(position).getKana())[0];
        Bundle bundle = new Bundle();
        FlashcardInfoFragment infoFragment = new FlashcardInfoFragment();
        bundle.putSerializable("wordResult", flashcardDetail);
        infoFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.FrameFlashcard, infoFragment, "flashcard");
//        FragmentTransaction ft = getSupportFragmentManager.beginTransaction();
//        ft.replace(R.id.content, fragment, backStateName);
        fragmentTransaction.addToBackStack(null);
//        ft.commit();
        fragmentTransaction.commit();
    }

}
