package com.example.lmemo_capstone_project.view.home_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.lmemo_capstone_project.model.room_db_entity.Flashcard;
import com.example.lmemo_capstone_project.model.room_db_entity.Reward;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    public static final int TEST_FLASHCARD_REQUEST_CODE = 100;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    private FirebaseAuth mAuth;
    private int wordID=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.home_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);


        DefaultFragment();
        setRewardData();


        mAuth = FirebaseAuth.getInstance();

//        RewardDAO rewardDAO = LMemoDatabase.getInstance(getApplicationContext()).rewardDAO();
//        for(int i=0;i<=5;i++) {
//            Reward r = new Reward();
//            r.setRewardID(i);
//            r.setRewardName("Reward:" + i);
//            r.setMinimumReachPoint(i*10);
//            rewardDAO.insertReward(r);
//        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey("wordID"))
            {

                wordID = extras.getInt("wordID");
                Log.w("HomeActivity", "Got Something: "+wordID+" blo bla: "+extras.getInt("wordID")+ " exists: "+extras.containsKey("wordID"));
            }

        }
        else {
            Log.w("HomeActivity", "Nothing found ");
        }


        DefaultFragment();
    }

    //set item to reward table in database
    private void setRewardData() {
        LMemoDatabase lMemoDatabase = LMemoDatabase.getInstance(getApplicationContext());
        Reward reward = new Reward();
        Reward reward1 = new Reward();
        Reward reward2 = new Reward();
        Reward reward3 = new Reward();
        Reward reward4= new Reward();
        Reward reward5 = new Reward();
        RewardDAO rewardDAO = lMemoDatabase.rewardDAO();
        Reward[] getRewards = rewardDAO.getRewards();
        if(getRewards.length==0) {
            reward.setRewardName("User");
            reward.setMinimumReachPoint(0);
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
            Log.d("myApp","Reward info:" +getRewards.length + getRewards[0].getRewardID() +" --" +
                            getRewards[0].getRewardName()+"-"+getRewards[0].getMinimumReachPoint()
                    + "---"
                    + getRewards[1].getRewardName()+"-"+getRewards[1].getMinimumReachPoint() + "---"
                    + getRewards[2].getRewardName()+"-"+getRewards[2].getMinimumReachPoint() + "---"
                    + getRewards[3].getRewardName() +"-"+getRewards[3].getMinimumReachPoint() + "---"
                    + getRewards[4].getRewardName()+"-"+getRewards[4].getMinimumReachPoint()
                    + "---" + getRewards[5].getRewardName() +"-"+getRewards[5].getMinimumReachPoint() + "---");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    //Default cho man hinh home la search
    public void DefaultFragment(){

        SearchFragment fragment = new SearchFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"searchHome");
        WordSearchingFragment wordFragment = new WordSearchingFragment();
        if(wordID != -1){
            Bundle bundle = new Bundle();
            bundle.putInt("wordID",wordID);
            fragment.setArguments(bundle);
            Log.w("HomeActivity", "Run at HomeActivity"+ bundle.toString());
        }
        fragmentTransaction.replace(R.id.searchFrameLayout,wordFragment,"SearchWord");
        fragmentTransaction.commit();

    }


    //Set item listener tren navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.Search_Home){
            DefaultFragment();
        }
        else if(id == R.id.login){
            SignInFragment fragment = new SignInFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"signIn");
            fragmentTransaction.commit();
        }
        else if(id == R.id.createAcc){

        }
        else if(id == R.id.myAccount){
            Fragment fragment = LMemoDatabase.getInstance(getApplicationContext()).userDAO().
                    getLocalUser()[0].isGuest() ? new SignInFragment() : new MyAccountFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"myAccount");
            fragmentTransaction.commit();
        }
        else if(id == R.id.myFlashcard){
            showFlashcardFragment();
        }
        else if(id == R.id.myNote){
            NotesFragment fragment = new NotesFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"Notes");
            fragmentTransaction.commit();
        }
        else if(id == R.id.contact){
            ContactUsFragment fragment = new ContactUsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"Contact");
            fragmentTransaction.commit();
        }
        else if(id == R.id.newInfo){
            WhatsNewFragment fragment = new WhatsNewFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"whatNew");
            fragmentTransaction.commit();
        }
        else if(id == R.id.terms){
            TermsofUseFragment fragment = new TermsofUseFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"termsOfUse");
            fragmentTransaction.commit();
        }
        else if(id == R.id.privacy){
            PrivacyFragment fragment = new PrivacyFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"privacy");
            fragmentTransaction.commit();
        }
        else if(id == R.id.signOut){
            final SignInFragment fragment = new SignInFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"signIn");
            fragmentTransaction.runOnCommit(new Runnable() {
                @Override
                public void run() {
                    fragment.LogOutFirebaseAndSQLite();
                }
            });
            fragmentTransaction.commit();

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if(manager.getBackStackEntryCount() > 1 ) {
            manager.popBackStack();//Pops one of the added fragments
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TEST_FLASHCARD_REQUEST_CODE:
                Toast.makeText(getApplicationContext(), "Test complete", Toast.LENGTH_LONG).show();
                showFlashcardFragment();
                break;
        }
    }

}

