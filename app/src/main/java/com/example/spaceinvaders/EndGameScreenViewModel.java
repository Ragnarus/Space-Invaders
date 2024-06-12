package com.example.spaceinvaders;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EndGameScreenViewModel extends ViewModel {

    public interface NavigationCommand {
        void continueToStartScreen();
    }


    private EndGameScreenViewModel.NavigationCommand navigationCommand;
    private final MutableLiveData<String> score = new MutableLiveData<>();
    private final MutableLiveData<String> won = new MutableLiveData<>();

    public EndGameScreenViewModel(String score, boolean won) {

        this.score.setValue(score);
        if (won) {
            this.won.setValue("You won");
        } else {
            this.won.setValue("You lost");
        }

    }

    public MutableLiveData<String> getScore() {
        return score;
    }

    public MutableLiveData<String> getWon() {
        return won;
    }

    public void onClick(){
        navigationCommand.continueToStartScreen();
    }
}
