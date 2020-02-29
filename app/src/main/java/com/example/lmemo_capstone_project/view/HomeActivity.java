package com.example.lmemo_capstone_project.view;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.facebook.login.LoginFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    getLocalUser()[0].isGuest() ? new LoginFragment() : new MyAccountFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"myAccount");
            fragmentTransaction.commit();
        }
        else if(id == R.id.myFlashcard){
            FlashCardFragment fragment = new FlashCardFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrameLayout,fragment,"flashCard");
            fragmentTransaction.commit();
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

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
//            super.onBackPressed();
        }

    }


}
