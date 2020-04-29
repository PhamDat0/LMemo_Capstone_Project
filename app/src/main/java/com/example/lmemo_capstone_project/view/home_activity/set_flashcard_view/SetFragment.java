package com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.lmemo_capstone_project.R;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.setFragmentViewPager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.setFragmentTabLayout);
        SetFragmentPager setFragmentPager = new SetFragmentPager(getParentFragmentManager());
        final SetFlashCardOfflineTab setFlashCardOfflineTab = new SetFlashCardOfflineTab();
        final SetFlashCardOnlineTab setFlashCardOnlineTab = new SetFlashCardOnlineTab();
        setFragmentPager.addFragment(setFlashCardOfflineTab, "Offline");
        setFragmentPager.addFragment(setFlashCardOnlineTab, "Online");
        viewPager.setOffscreenPageLimit(2);

        viewPager.setAdapter(setFragmentPager);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//                Log.i("SELECT_TAB", tab.getPosition() + "\n" + setFragmentPager.getItem(tab.getPosition()).toString());
//                if (tab.getPosition() == 0) {
//
//                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
//                    Intent i = new Intent("TAG_REFRESH");
//                    lbm.sendBroadcast(i);
//
//                }
                switch (tab.getPosition()) {
                    //Private note
                    case SET_FLASH_CARD_OFFLINE:
                        setFlashCardOfflineTab.onResume();
                        break;
                    //Public note
                    case SET_FLASH_CARD_ONLINE:
                        setFlashCardOnlineTab.onResume();
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
