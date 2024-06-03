package com.example.spaceinvaders;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import java.io.Closeable;

public class StartScreenViewModel extends androidx.lifecycle.ViewModel {

    private FragmentTransaction transaction;
    private Activity mainRef;


    public StartScreenViewModel(FragmentTransaction transaction, Activity mainRef) {
        this.transaction = transaction;
        this.mainRef = mainRef;
    }

    public void onClick(int i){
        switch (i)  {
            case 0: transaction.replace(R.id.fragmentContainer, GameScreenFragment.class, null).commit();
            case 1: transaction.replace(R.id.fragmentContainer, HighscoreScreenFragment.class, null).commit();
            case 2: transaction.replace(R.id.fragmentContainer, OptionsScreenFragment.class, null).commit();
            case 3: transaction.replace(R.id.fragmentContainer, AboutScreenFragment.class, null).commit();
            case 4:  finishAffinity(mainRef);


        }


    }

}
