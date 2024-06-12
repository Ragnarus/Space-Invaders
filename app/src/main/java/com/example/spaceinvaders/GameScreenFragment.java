package com.example.spaceinvaders;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameScreenFragment extends Fragment {


   private GamePanel  panel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        panel = new GamePanel(getContext());
        return inflater.inflate(R.layout.fragment_game_screen, container, false);

    }

    public void onGameEnd() {
        //TODO
    }


    @Override
    public void onPause() {
        super.onPause();
        panel.pause();
    }
    @Override
    public void onResume() {
        super.onResume();
        panel.resume();
    }

    public void setGameEndScreen(){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, EndGameScreenFragment.class, null).commit();
    }

}