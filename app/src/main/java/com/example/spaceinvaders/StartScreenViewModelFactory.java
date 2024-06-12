package com.example.spaceinvaders;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
//wurde von chatgpt erstellt
public class StartScreenViewModelFactory implements ViewModelProvider.Factory {

    private StartScreenViewModel.NavigationCommand navigationCommand;

    public StartScreenViewModelFactory(StartScreenViewModel.NavigationCommand navigationCommand) {
        this.navigationCommand = navigationCommand;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StartScreenViewModel.class)) {
            return (T) new StartScreenViewModel(navigationCommand);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}


