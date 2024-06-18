package com.example.spaceinvaders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;


import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
//TODO multiplayer, maybe add menu bar in gamescreenfragment_layout for score and menu, add a better background and better ui
public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;



    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

       fragmentManager = getSupportFragmentManager();
       transaction = fragmentManager.beginTransaction();
       transaction.setReorderingAllowed(true).replace(R.id.fragmentContainer, StartScreenFragment.class, null).commit();
    }





}