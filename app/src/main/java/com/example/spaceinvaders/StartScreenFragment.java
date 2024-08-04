package com.example.spaceinvaders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.spaceinvaders.databinding.FragmentStartScreenBinding;


public class StartScreenFragment extends Fragment implements StartScreenViewModel.NavigationCommand {

    private StartScreenViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentStartScreenBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start_screen, container, false);
        viewModel = new ViewModelProvider(this, new StartScreenViewModelFactory(this)).get(StartScreenViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void navigateTo(int fragmentId) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        switch (fragmentId) {
            case 0:
                transaction.replace(R.id.fragmentContainer, GameScreenFragment.class, null).commit();
                break;
            case 1:
                //transaction.replace(R.id.fragmentContainer, MultiplayerLobbyFragment.class, null).commit();
                transaction.replace(R.id.fragmentContainer, MultiplayerGameScreenFragment.class, null).commit();
                break;
            case 2:
                transaction.replace(R.id.fragmentContainer, HighscoreScreenFragment.class, null).commit();
                break;
            case 3:
                transaction.replace(R.id.fragmentContainer, OptionsScreenFragment.class, null).commit();
                break;
            case 4:
                transaction.replace(R.id.fragmentContainer, AboutScreenFragment.class, null).commit();
                break;
            case 5:
                getActivity().finishAffinity();
                break;
        }
    }
}
