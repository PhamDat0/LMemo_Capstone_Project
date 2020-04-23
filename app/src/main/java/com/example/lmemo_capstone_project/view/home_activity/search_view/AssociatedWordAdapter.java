package com.example.lmemo_capstone_project.view.home_activity.search_view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;
import com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view.CreateSetActivity;

import java.util.List;

public class AssociatedWordAdapter extends BaseAdapter {

    private Activity aContext;
    private List<Word> listWord;
    private LayoutInflater layoutInflater;

    public AssociatedWordAdapter(Activity aContext, List<Word> listWord) {
        this.aContext = aContext;
        this.listWord = listWord;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listWord.size();
    }

    @Override
    public Object getItem(int position) {
        return listWord.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(aContext).inflate(R.layout.listview_associated_word, null);
            holder = new ViewHolder();
            holder.tvAssociationWord = convertView.findViewById(R.id.tvAssociatedWord);
            holder.ibDeleteAssociation = convertView.findViewById(R.id.ibDeleteAssciation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String kanjiWriting = listWord.get(position).getKanjiWriting();
        holder.tvAssociationWord.setText(kanjiWriting == null || kanjiWriting.length() == 0 ? listWord.get(position).getKana() : kanjiWriting);
        holder.ibDeleteAssociation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWordFromList(position);
            }
        });
        return convertView;
    }

    public void addWordToList(Word word) {
        if (!isExistedWord(word)) {
            listWord.add(word);
            notifyDataSetChanged();
        }
    }

    private boolean isExistedWord(Word word) {
        boolean result = false;
        for (Word word1 : listWord) {
            if (word1.getWordID() == word.getWordID()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void deleteWordFromList(int position) {
        if (listWord.size() > 1) {
            listWord.remove(position);
            if (listWord.size() < 10) {
                if (aContext instanceof CreateSetActivity) {
                    ((CreateSetActivity) aContext).warningPublicSetLowerThan10Card();
                    ((CreateSetActivity) aContext).updateNumberOfCards();
                }
            }
            notifyDataSetChanged();
        } else {
            Toast.makeText(aContext, "Cannot remove every association", Toast.LENGTH_LONG).show();
        }
    }

    public List<Word> getListOfWord() {
        return listWord;
    }

    static class ViewHolder {
        TextView tvAssociationWord;
        ImageButton ibDeleteAssociation;
    }
}
