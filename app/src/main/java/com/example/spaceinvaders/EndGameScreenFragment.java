package com.example.spaceinvaders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.spaceinvaders.databinding.FragmentEndGameScreenBinding;


public class EndGameScreenFragment extends Fragment implements EndGameScreenViewModel.NavigationCommand{

    private int score;
    private boolean won;
    private FragmentEndGameScreenBinding binding;
    private EndGameScreenViewModel viewModel;
    private EditText nameInput;
    UserDAO userDAO;

    public EndGameScreenFragment() {}

    public static EndGameScreenFragment newInstance(int score, boolean won) {
        EndGameScreenFragment fragment = new EndGameScreenFragment();
        Bundle args = new Bundle();
        args.putInt("score", score);
        args.putBoolean("won", won);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            score = getArguments().getInt("score");
            won = getArguments().getBoolean("won");
        }
        userDAO = new UserDAO(getContext());
        userDAO.open();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_end_game_screen, container, false);
        viewModel = new ViewModelProvider(this, new EndGameScreenViewModelFactory(String.valueOf(score),won, this)).get(EndGameScreenViewModel.class);
        binding.setVM(viewModel);
        binding.setLifecycleOwner(this);
        nameInput = binding.playernameEditText;
        return binding.getRoot();
    }

    public void continueToStartScreen(){
        String name = nameInput.getText().toString();
        userDAO.insertUser(name, score);
        userDAO.close();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, StartScreenFragment.class, null).commit();
    }
}