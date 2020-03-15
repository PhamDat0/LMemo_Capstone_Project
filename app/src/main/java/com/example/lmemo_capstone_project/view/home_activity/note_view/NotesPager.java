package com.example.lmemo_capstone_project.view.home_activity.note_view;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class NotesPager extends FragmentStatePagerAdapter {

    ArrayList<Fragment> FragmentList;
    ArrayList<String> FragmentTitleList;
    int tabCount;

    public NotesPager(FragmentManager fm) {
        super(fm);
        FragmentList = new ArrayList<>();
        FragmentTitleList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentList.get(position);

    }

    @Override
    public int getCount() {
        return FragmentList.size();
    }
    public void addFragment(Fragment fragment, String title){
        FragmentList.add(fragment);
        FragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentTitleList.get(position);
    }
}