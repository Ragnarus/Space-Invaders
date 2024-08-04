package com.example.spaceinvaders;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.example.spaceinvaders.databinding.FragmentOptionsScreenBinding;
import com.example.spaceinvaders.databinding.FragmentStartScreenBinding;


public class OptionsScreenFragment extends Fragment {

    private float volume;
    private boolean volumeMuted;
    private double sensitivity;
    private SeekBar volumeSb;
    private SeekBar sensitivitySb;
    private CheckBox isvolumeMuted;
    private Button backBtn;
    private Button deleteAllHighscoreBtn;


    MainActivity mainActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        volume = mainActivity.getVolume();
        volumeMuted = mainActivity.isVolumeMuted();
        sensitivity = mainActivity.getSensitivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        volume = mainActivity.getVolume();
        volumeMuted = mainActivity.isVolumeMuted();
        sensitivity = mainActivity.getSensitivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options_screen, container, false);
        //Seekbar init
        sensitivitySb = view.findViewById(R.id.sensitivitySeekbar);
        sensitivitySb.setProgress((int) (sensitivity * 10));
        sensitivitySb.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        sensitivity = sensitivitySb.getProgress() /10d;
                        mainActivity.setSensitivity(sensitivity);
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something when the user starts interacting with the SeekBar
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Do something when the user stops interacting with the SeekBar
                    }
                });


        volumeSb = view.findViewById(R.id.volumeSeekbar);
        volumeSb.setMax(100);
        volumeSb.setProgress((int) volume);
        volumeSb.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        volume = progress / 100f;
                        mainActivity.setVolume(volume);
                        mainActivity.onVolumeChanged();
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something when the user starts interacting with the SeekBar
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Do something when the user stops interacting with the SeekBar
                    }
                });

        //Checkbox init
        isvolumeMuted = view.findViewById(R.id.volumeCheckBox);
        isvolumeMuted.setChecked(volumeMuted);

        isvolumeMuted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            volumeMuted = isChecked;
            mainActivity.setVolumeMuted(volumeMuted);
            mainActivity.onVolumeChanged();
        });

        //Button init
        backBtn = view.findViewById(R.id.inGameOptionsContinueButton);
        deleteAllHighscoreBtn = view.findViewById(R.id.deletingLeHighscores);

        backBtn.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, StartScreenFragment.class, null).commit();
        });

        deleteAllHighscoreBtn.setOnClickListener(v -> {
            UserDAO userDAO = new UserDAO(getContext());
            userDAO.open();
            userDAO.deleteAllUsers();
            userDAO.close();
        });


        return view;
    }
}