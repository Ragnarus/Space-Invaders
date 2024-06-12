package com.example.spaceinvaders;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EndGameScreenViewModelFactory implements ViewModelProvider.Factory {

    private final String score;
    private final boolean won;
    EndGameScreenViewModel.NavigationCommand navigationCommand;



    public EndGameScreenViewModelFactory(String score, boolean won, EndGameScreenViewModel.NavigationCommand navigationCommand) {
        this.navigationCommand = navigationCommand;
        this.score = score;
        this.won = won;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EndGameScreenViewModel.class)) {
            return (T) new EndGameScreenViewModel(score, won, navigationCommand);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
