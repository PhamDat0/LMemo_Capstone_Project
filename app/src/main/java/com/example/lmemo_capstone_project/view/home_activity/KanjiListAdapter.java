package com.example.lmemo_capstone_project.view.home_activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.model.room_db_entity.Kanji;

import java.util.List;

public class KanjiListAdapter extends BaseAdapter {

    private Activity aContext;
    private List<Kanji> listKanji;
    private LayoutInflater layoutInflater;

    public KanjiListAdapter(Activity aContext, List<Kanji> listKanji) {
        this.aContext = aContext;
        this.listKanji = listKanji;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listKanji.size();
    }

    @Override
    public Object getItem(int position) {
        return listKanji.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(aContext).inflate(R.layout.activity_kanji_list_adapter, null);
            holder = new ViewHolder();
            holder.tvKanji = convertView.findViewById(R.id.tvKanji);
            holder.tvKunyomi = convertView.findViewById(R.id.tvKunyomi);
            holder.tvOnyomi = convertView.findViewById(R.id.tvOnyomi);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvKanji.setText(listKanji.get(position).getKanji());
        holder.tvKunyomi.setText(" "+listKanji.get(position).getKunyomi());
        holder.tvOnyomi.setText(" "+listKanji.get(position).getOnyomi());
        return convertView;
    }

    static class ViewHolder {
        TextView tvKanji;
        TextView tvKunyomi;
        TextView tvOnyomi;
    }
}
