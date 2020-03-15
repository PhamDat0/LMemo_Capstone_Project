package com.example.lmemo_capstone_project.view.home_activity.note_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.lmemo_capstone_project.R;
import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment {


    private final int PRIVATE_NOTE = 0;
    private final int PUBLIC_NOTE = 1;

    public NotesFragment() {
        // Required empty public constructor
    }

    NotesPager notesPager;
    private Toolbar toolbartab;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        notesPager = new NotesPager(getParentFragmentManager());
        notesPager.addFragment(new PrivateNoteTab(), "Private");
        notesPager.addFragment(new PublicNoteTab(), "Public");

        viewPager.setAdapter(notesPager);

        tabLayout.setupWithViewPager(viewPager);

        //Chuyen du lieu giua cac tab tai day
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    //Private note
                    case PRIVATE_NOTE:
                        break;
                    //Public note
                    case PUBLIC_NOTE:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

}
