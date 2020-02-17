package com.example.lmemo_capstone_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lmemo_capstone_project.room_database.LMemoDatabase;
import com.example.lmemo_capstone_project.room_database.dao.UserDAO;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LMemoDatabase dbAccessor = LMemoDatabase.getInstance(getApplicationContext());
        UserDAO userDAO = dbAccessor.userDAO();
    }
}
