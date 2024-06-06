package com.example.spaceinvaders;

import androidx.lifecycle.ViewModel;

public class StartScreenViewModel extends ViewModel {

    private NavigationCommand navigationCommand;

    public interface NavigationCommand {
        void navigateTo(int fragmentId);
    }

    public StartScreenViewModel(NavigationCommand navigationCommand) {
        this.navigationCommand = navigationCommand;
    }

    public void onClick(int i) {
        if (navigationCommand != null) {
            navigationCommand.navigateTo(i);
        }
    }
}
