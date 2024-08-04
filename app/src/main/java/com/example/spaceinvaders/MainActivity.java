package com.example.spaceinvaders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;


import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
//TODO multiplayer, maybe add menu bar in gamescreenfragment_layout for score and menu, add a better background and better ui
public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private float volume = 10;
    private double Sensitivity = 0.7;
    private boolean volumeMuted = false;
    private MediaPlayer mediaPlayer;



    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

       fragmentManager = getSupportFragmentManager();
       transaction = fragmentManager.beginTransaction();
       transaction.setReorderingAllowed(true).replace(R.id.fragmentContainer, StartScreenFragment.class, null).commit();

       //Backgroundmusic
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        onVolumeChanged();
        mediaPlayer.start();

    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public double getSensitivity() {
        return Sensitivity;
    }

    public void setSensitivity(double sensitivity) {
        Sensitivity = sensitivity;
    }

    public boolean isVolumeMuted() {
        return volumeMuted;
    }

    public void setVolumeMuted(boolean volumeMuted) {
        this.volumeMuted = volumeMuted;
    }

    public void onVolumeChanged(){
        mediaPlayer.setVolume(volume, volume);
        if (volumeMuted){
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } else {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}


