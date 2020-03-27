package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.view.home_activity.note_view.NotesPager;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetFragment extends Fragment {

    private final int SET_FLASH_CARD_OFFLINE = 0;
    private final int SET_FLASH_CARD_ONLINE = 1;
    public SetFragment() {
        // Required empty public constructor
    }
    SetFragmentPager setFragmentPager;
    private Toolbar toolbartab;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.setFragmentViewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.setFragmentTabLayout);
        setFragmentPager = new SetFragmentPager(getParentFragmentManager());
        setFragmentPager.addFragment(new SetFlashCardOfflineTab() , "Offline");
        setFragmentPager.addFragment(new SetFlashCardOnlineTab(), "Online");
        viewPager.setOffscreenPageLimit(2);

        viewPager.setAdapter(setFragmentPager);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    //Private note
                    case SET_FLASH_CARD_OFFLINE:
                        break;
                    //Public note
                    case SET_FLASH_CARD_ONLINE:
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
