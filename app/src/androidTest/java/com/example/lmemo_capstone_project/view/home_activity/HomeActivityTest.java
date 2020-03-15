package com.example.lmemo_capstone_project.view.home_activity;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentTransaction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.view.home_activity.search_view.SearchFragment;
import com.example.lmemo_capstone_project.view.home_activity.search_view.WordSearchingFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeActivityTest {
    @Rule
    public ActivityTestRule<HomeActivity> activityTestRule = new ActivityTestRule<>(HomeActivity.class);
    private String stringExpected;

    @Before
    public void init() {
        stringExpected = "";
//        activityTestRule.launchActivity(new Intent(activityTestRule.getActivity(),HomeActivity.class));
    }

    @Test
    public void onCreateTest() {
        FragmentTransaction fragmentTransaction = activityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
        SearchFragment fragment = new SearchFragment();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment, "searchHome");
        WordSearchingFragment wordFragment = new WordSearchingFragment();
        int wordID = -1;
        if (wordID != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt("wordID", wordID);
            fragment.setArguments(bundle);
            Log.w("HomeActivity", "Run at HomeActivity" + bundle.toString());
        }
        fragmentTransaction.replace(R.id.searchFrameLayout, wordFragment, "SearchWord");
        fragmentTransaction.commitAllowingStateLoss();
        onView(withId(R.id.txtSearch)).check(matches(withText(stringExpected)));
        onView(withId(R.id.txtSearch)).perform(typeText("抱きしめる"), pressImeActionButton(), closeSoftKeyboard());

    }
}