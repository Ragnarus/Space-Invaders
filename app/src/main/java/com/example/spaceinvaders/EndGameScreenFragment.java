package com.example.spaceinvaders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.spaceinvaders.databinding.FragmentEndGameScreenBinding;


public class EndGameScreenFragment extends Fragment implements EndGameScreenViewModel.NavigationCommand {

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
        Log.e("EndGameScreenFragment", "In newInstance");
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("EndGameScreenFragment", "In onCreate");
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
        binding.setLifecycleOwner(getViewLifecycleOwner());  // Verwendung des ViewLifecycleOwner
        nameInput = binding.playernameEditText;
        Log.e("EndGameScreenFragment", "In onCreateView");
        return binding.getRoot();
    }

    public void continueToStartScreen(){
        String name = nameInput.getText().toString();
        userDAO.insertUser(name, score);
        userDAO.close();
        Log.d("EndGameScreenFragment", "User data inserted and DAO closed. Continuing to StartScreenFragment.");
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, StartScreenFragment.class, null).commit();
        Log.d("EndGameScreenFragment", "Fragment transaction committed.");
    }
}
