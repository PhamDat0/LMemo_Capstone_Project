package com.example.lmemo_capstone_project.view.home_activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.RewardDAO;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.UserDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.example.lmemo_capstone_project.model.room_db_entity.User;
import com.example.lmemo_capstone_project.view.home_activity.account_view.MyAccountFragment;
import com.example.lmemo_capstone_project.view.home_activity.account_view.SignInFragment;
import com.example.lmemo_capstone_project.view.home_activity.constant_view.CopyrightFragment;
import com.example.lmemo_capstone_project.view.home_activity.constant_view.SettingsFragment;
import com.example.lmemo_capstone_project.view.home_activity.flashcard_view.FlashCardFragment;
import com.example.lmemo_capstone_project.view.home_activity.note_view.NotesFragment;
import com.example.lmemo_capstone_project.view.home_activity.search_view.SearchFragment;
import com.example.lmemo_capstone_project.view.home_activity.search_view.WordSearchingFragment;
import com.example.lmemo_capstone_project.view.home_activity.set_flashcard_view.SetFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int TEST_FLASHCARD_REQUEST_CODE = 100;
    public static final int ADD_NOTE_REQUEST_CODE = 101;
    public static final int EDIT_NOTE_REQUEST_CODE = 102;
    public static final int NOTI_FOR_WORD = 110;
    public static final int NOTI_FOR_FC = 120;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    private FirebaseAuth mAuth;
    private int wordID = -1;
    private boolean reminderIsExist = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        Log.i("Open_successful", "Error is really in DB");

        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.home_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        Menu menu = navigationView.getMenu();

        MenuItem account= menu.findItem(R.id.account);
        MenuItem resources= menu.findItem(R.id.resources);
        SpannableString s1 = new SpannableString(account.getTitle());
        SpannableString s2 = new SpannableString(resources.getTitle());
        s1.setSpan(new TextAppearanceSpan(this, R.style.TitleColorMenu), 0, s1.length(), 0);
        account.setTitle(s1);
        s2.setSpan(new TextAppearanceSpan(this, R.style.TitleColorMenu), 0, s2.length(), 0);
        resources.setTitle(s2);
        navigationView.setNavigationItemSelectedListener(this);

//        changeToFlashcardFragment();
//        DefaultFragment();
        setRewardData();
        onNewIntent(getIntent());

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
            loadWord(intent);
    }

    private void loadWord(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("wordID") && ((int) extras.get("mode")) == NOTI_FOR_WORD) {

                wordID = extras.getInt("wordID");
                Log.w("HomeActivity", "Got Something: " + wordID + " blo bla: " + extras.getInt("wordID") + " exists: " + extras.containsKey("wordID"));
            }
            else if (extras.containsKey("flashcardReminderNotification") && ((int) extras.get("mode")) == NOTI_FOR_FC){
                reminderIsExist = extras.getBoolean("flashcardReminderNotification");
            }
        } else {
            Log.w("HomeActivity", "Nothing found ");
        }
        DefaultFragment();
        reminderIsExist= false;
        wordID = -1;
    }

    //set item to reward table in database
    private void setRewardData() {
        LMemoDatabase lMemoDatabase = LMemoDatabase.getInstance(getApplicationContext());
        Reward reward = new Reward();
        Reward reward1 = new Reward();
        Reward reward2 = new Reward();
        Reward reward3 = new Reward();
        Reward reward4 = new Reward();
        Reward reward5 = new Reward();
        RewardDAO rewardDAO = lMemoDatabase.rewardDAO();
        Reward[] getRewards = rewardDAO.getRewards();
        if (getRewards.length == 0) {
            reward.setRewardName("User");
            reward.setMinimumReachPoint(-99);
            rewardDAO.insertReward(reward);

            reward1.setRewardName("First contribution");
            reward1.setRewardID(1);
            reward1.setMinimumReachPoint(1);
            rewardDAO.insertReward(reward1);

            reward2.setRewardName("Valuable Contributor");
            reward2.setMinimumReachPoint(5);
            reward2.setRewardID(2);
            rewardDAO.insertReward(reward2);

            reward3.setRewardName("Rising Stars");
            reward3.setMinimumReachPoint(10);
            reward3.setRewardID(3);
            rewardDAO.insertReward(reward3);


            reward4.setRewardName("Excellent Specialist");
            reward4.setMinimumReachPoint(20);
            reward4.setRewardID(4);
            rewardDAO.insertReward(reward4);

            reward5.setRewardName("Supreme Professor");
            reward5.setMinimumReachPoint(50);
            reward5.setRewardID(5);
            rewardDAO.insertReward(reward5);
        } else {
            Log.d("myApp", "Reward info:" + getRewards.length + getRewards[0].getRewardID() + " --" +
                    getRewards[0].getRewardName() + "-" + getRewards[0].getMinimumReachPoint()
                    + "---"
                    + getRewards[1].getRewardName() + "-" + getRewards[1].getMinimumReachPoint() + "---"
                    + getRewards[2].getRewardName() + "-" + getRewards[2].getMinimumReachPoint() + "---"
                    + getRewards[3].getRewardName() + "-" + getRewards[3].getMinimumReachPoint() + "---"
                    + getRewards[4].getRewardName() + "-" + getRewards[4].getMinimumReachPoint()
                    + "---" + getRewards[5].getRewardName() + "-" + getRewards[5].getMinimumReachPoint() + "---");
        }

        UserDAO userDAO = lMemoDatabase.userDAO();
        if (userDAO.getLocalUser().length == 0) {
            User user = new User();
            user.setUserID("GUEST");
            user.setEmail("GUEST");
            user.setContributionPoint(0);
            user.setDisplayName("GUEST");
            user.setGender(true);
            user.setLoginTime(new Date());
            userDAO.insertUser(user);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    //Default cho man hinh home la search
    public void DefaultFragment() {
        if (reminderIsExist){
            showSetFlashcardFragment();
        }
        else {
            SearchFragment fragment = new SearchFragment();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.mainFrameLayout, fragment, "searchHome");
            WordSearchingFragment wordFragment = new WordSearchingFragment();
            if (wordID != -1) {
                Bundle bundle = new Bundle();
                bundle.putInt("wordID", wordID);
                fragment.setArguments(bundle);
                Log.w("HomeActivity", "Run at HomeActivity" + bundle.toString());
            }
            fragmentTransaction.replace(R.id.searchFrameLayout, wordFragment, "SearchWord");
            fragmentTransaction.commit();
        }


    }


    //Set item listener tren navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Search_Home) {
            DefaultFragment();
        } else if (id == R.id.login) {
            SignInFragment fragment = new SignInFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout, fragment, "signIn");
            fragmentTransaction.commit();
        } else if (id == R.id.myAccount) {
            Fragment fragment = LMemoDatabase.getInstance(getApplicationContext()).userDAO().
                    getLocalUser()[0].isGuest() ? new SignInFragment() : new MyAccountFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout, fragment, "myAccount");
            fragmentTransaction.commit();
        } else if (id == R.id.myFlashcard) {
            showFlashcardFragment();
        } else if (id == R.id.setFlashcard) {
            showSetFlashcardFragment();
        }
        else if (id == R.id.myNote) {
            showNoteFragment();
        } else if (id == R.id.settings) {
            SettingsFragment fragment = new SettingsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout, fragment, "Settings");
            fragmentTransaction.commit();
        } else if (id == R.id.copyright) {
            CopyrightFragment fragment = new CopyrightFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout, fragment, "termsOfUse");
            fragmentTransaction.commit();
        } else if (id == R.id.signOut) {
            final SignInFragment fragment = new SignInFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout, fragment, "signIn");
            fragmentTransaction.runOnCommit(new Runnable() {
                @Override
                public void run() {
                    fragment.logOutFirebaseAndSQLite();
                }
            });
            fragmentTransaction.commit();

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSetFlashcardFragment() {
        SetFragment fragment = new SetFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment, "setFragment");
        fragmentTransaction.commit();
    }

    private void showFlashcardFragment() {
        FlashCardFragment fragment = new FlashCardFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment, "flashCard");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        //        } else if(manager.getBackStackEntryCount() > 1 ) {
//            manager.popBackStack();//Pops one of the added fragments
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TEST_FLASHCARD_REQUEST_CODE:
                Toast.makeText(getApplicationContext(), "Test complete", Toast.LENGTH_LONG).show();
//                showFlashcardFragment();
                break;
            case ADD_NOTE_REQUEST_CODE:
                loadWord(data);
                break;
        }
    }

    private void showNoteFragment() {
        NotesFragment fragment = new NotesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment, "Notes");
        fragmentTransaction.commit();
    }


}

