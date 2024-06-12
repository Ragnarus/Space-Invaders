package com.example.spaceinvaders;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.spaceinvaders.databinding.FragmentEndGameScreenBinding;


public class EndGameScreenFragment extends Fragment implements EndGameScreenViewModel.NavigationCommand{

    private FragmentEndGameScreenBinding binding;
    private EndGameScreenViewModel viewModel;
    private Button continueBtn;

    public EndGameScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentEndGameScreenBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_end_game_screen, container, false);
        viewModel = new ViewModelProvider(this).get(EndGameScreenViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    public void continueToStartScreen(){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, StartScreenFragment.class, null).commit();
    }


}