package com.example.spaceinvaders;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameScreenFragment extends Fragment implements GamePanel.ComTool {

    private GamePanel panel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_screen, container, false);

        // Find the GamePanel from the inflated view
        panel = view.findViewById(R.id.gamepanel);
        panel.setComTool(this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (panel != null) {
            panel.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (panel != null) {
            panel.resume();
        }
    }

    @Override
    public void gameHasEnded(boolean won, int score) {
        EndGameScreenFragment endGameScreenFragment = EndGameScreenFragment.newInstance(score, won);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, endGameScreenFragment).commit();
    }
}
