package com.example.spaceinvaders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;


import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private ViewModel startScreenVM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       fragmentManager = getSupportFragmentManager();
       transaction = fragmentManager.beginTransaction();
       transaction.setReorderingAllowed(true).replace(R.id.fragmentContainer, StartScreenFragment.class, null).commit();

        startScreenVM = new StartScreenViewModel(transaction,this);



    }
}