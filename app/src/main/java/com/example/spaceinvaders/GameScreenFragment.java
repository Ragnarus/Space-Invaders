package com.example.spaceinvaders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.spaceinvaders.GameClasses.EnemyProjectile;
import com.example.spaceinvaders.GameClasses.EnemyShips;
import com.example.spaceinvaders.GameClasses.PlayerProjectile;
import com.example.spaceinvaders.GameClasses.PlayerShip;
import com.example.spaceinvaders.GameClasses.Projectile;
import com.example.spaceinvaders.databinding.FragmentGameScreenBinding;
import com.example.spaceinvaders.databinding.FragmentOptionsScreenBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressLint("SetTextI18n")
public class GameScreenFragment extends Fragment implements  SensorEventListener, Runnable {

    //Variables
    //Sensor
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    //Game systems
    private MainActivity mainActivity;
    private GamePanel panel;
    private int score = 0;
    private boolean mRunning;
    private Thread mThread;
    private int screenWidth;
    private int screenHeight;
    private boolean isInitialized = false; // New flag to check initialization
    private double sensorThreshold;
    private Button settingsBtn, inGameResumeBtn;
    private LinearLayout inGameOptionsLayout;
    private TextView scoreTV;
    private ImageView[] lifes;
    private int lifeCount;
    private SeekBar sensitivity;
    private SeekBar volumeSb;
    private float volume;
    private CheckBox volumeCheckBox;
    private boolean volumeMuted;


    // Bitmaps for the Player, Enemy and Projectiles
    private Bitmap playerProjectileBitmap;
    private Bitmap  enemyProjectileBitmap;
    private Bitmap  enemyShipBitmap;
    private Bitmap playerShipBitmap;

    //Objects
    private PlayerShip playerShip;
    private List<EnemyShips> enemies;
    private List<PlayerProjectile> playerProjectiles;
    private List<EnemyProjectile> enemyProjectiles;

    // Settings
    private static  int enemycount;
    private static final int MAX_ENEMY_COUNT = 10;
    private static  final int MAX_ENEMY_PER_ROW = 5;
    private float xAcceleration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_screen, container, false);
        mainActivity = (MainActivity) getActivity();
        volume = mainActivity.getVolume();
        volumeMuted = mainActivity.isVolumeMuted();
        sensorThreshold = mainActivity.getSensitivity();
        //Init the Views for the inGameOptions
        settingsBtn = view.findViewById(R.id.settingsbtn);
        inGameResumeBtn = view.findViewById(R.id.inGameOptionsContinueButton);
        inGameOptionsLayout = view.findViewById(R.id.inGameOptionsLayout);
        scoreTV = view.findViewById(R.id.scoreTv);
        settingsBtn.setOnClickListener(v -> {
            requireActivity().runOnUiThread(this::onGamePause);
            inGameOptionsLayout.setVisibility(View.VISIBLE);
        });

        inGameResumeBtn.setOnClickListener(v -> {
            inGameOptionsLayout.setVisibility(View.INVISIBLE);
            requireActivity().runOnUiThread(this::onGameResume);
        });


        //Seekbar init
        sensitivity = view.findViewById(R.id.sensitivitySeekbar);
        sensitivity.setProgress((int) (sensorThreshold * 10));
        sensitivity.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        sensorThreshold = sensitivity.getProgress() /10d;
                        mainActivity.setSensitivity(sensorThreshold);
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
                        volume = progress/ 100f;
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
        volumeCheckBox = view.findViewById(R.id.volumeCheckBox);
        volumeCheckBox.setChecked(volumeMuted);

        volumeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            volumeMuted = isChecked;
            mainActivity.setVolumeMuted(volumeMuted);
            mainActivity.onVolumeChanged();
        });



        // Find the GamePanel from the inflated view
        panel = view.findViewById(R.id.gamepanel);
        panel.setComTool(this);


        // Load the bitmaps from resources
        Bitmap playerShipBitmapToScale = BitmapFactory.decodeResource(getResources(), R.drawable.playerfighteridle);
        playerShipBitmap = Bitmap.createScaledBitmap(playerShipBitmapToScale, (int)PlayerShip.WIDTH, (int)PlayerShip.HEIGHT, true);
        Bitmap enemyShipBitmapToScale = BitmapFactory.decodeResource(getResources(), R.drawable.enemyshipidle);
        enemyShipBitmap = Bitmap.createScaledBitmap(enemyShipBitmapToScale, (int)EnemyShips.WIDTH, (int)EnemyShips.HEIGHT, true);
        playerProjectileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playerprojectile);
        enemyProjectileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemyprojectile);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Initialize Projectiles and Enemyships
        playerProjectiles = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        enemies = new ArrayList<>();
        enemycount = 0;

        // Use a ViewTreeObserver to get the width and height after the view has been laid out
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                screenWidth = view.getWidth();
                screenHeight = view.getHeight();
                Log.d("MyTest", "screenWidth: " + screenWidth + ", screenHeight: " + screenHeight);
                // Initialize the player ship now that we have the screen dimensions
                playerShip = new PlayerShip(screenWidth / 2 - PlayerShip.WIDTH / 2, ( screenHeight - PlayerShip.HEIGHT - 150));
                isInitialized = true;
            }
        });

        //init the player ship live display
        lifeCount = 0;
        lifes = new ImageView[ PlayerShip.MAX_LIVES];
        lifes[0] = view.findViewById(R.id.lifeOne);
        lifes[1] = view.findViewById(R.id.lifeTwo);
        lifes[2] = view.findViewById(R.id.lifeThree);

        return view;
    }

    @Override
    public void onPause() {
        //OnPause
        super.onPause();
        sensorManager.unregisterListener(this);
        mRunning = false;
        boolean retry = true;
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onResume() {
        //OnResume
        super.onResume();
        mRunning = true;
        mThread = new Thread(this);
        mThread.start();
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onGamePause() {
        //Method pauses game and joins the game thread is called when ingameoptions are called
        sensorManager.unregisterListener(this);
        mRunning = false;
        boolean retry = true;
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onGameResume() {
        //Method resumes the game and starts the game thread is called when continue is pressed

        mRunning = true;
        mThread = new Thread(this);
        mThread.start();
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void updateScore() {
        //updates score
        requireActivity().runOnUiThread(() -> scoreTV.setText("" + score));
    }


    public void gameHasEnded(boolean won, int score) {
        //If game has ended replaces this fragment with endGameScreenFragment
        EndGameScreenFragment endGameScreenFragment = EndGameScreenFragment.newInstance(score, won);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, endGameScreenFragment).commit();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Method for is for controling the playership through the Accellerometer
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (event.values[0] > sensorThreshold || event.values[0] < - sensorThreshold ){
                if (event.values[0] > 2){
                    xAcceleration = - 1;
                } else {
                    xAcceleration = 1;
                }
            } else{
                xAcceleration = 0;
            }

            Log.d("MyTest","" + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Do nothing
    }


    public boolean onGamePanelTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Fire a projectile on tap event
            float projectileX = playerShip.x + (PlayerShip.WIDTH / 2) - (Projectile.PROJECTILE_WIDTH / 2) +10;
            float projectileY = playerShip.y - Projectile.PROJECTILE_HEIGHT;
            playerProjectiles.add(new PlayerProjectile(projectileX, projectileY));
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        while (mRunning) {
            /*this is the main Game Loop this Method it handles the logic and call gamepanel to draw the objects*/

            if (!isInitialized){
                //if the view is not initialized skipp loop
                continue;
            }

            // These List are the draw queue
            ArrayList<Float> xDraw = new ArrayList<>();
            ArrayList<Float> yDraw = new ArrayList<>();
            ArrayList<Bitmap> bitMapDraw = new ArrayList<>();



            // Update player position based on sensor data
            playerShip.x += xAcceleration * playerShip.speed;
            if (playerShip.x < 0) {
                playerShip.x = 0;
            }

            if (playerShip.x + PlayerShip.WIDTH > screenWidth) {
                playerShip.x = screenWidth - PlayerShip.WIDTH;
            }

            if (playerShip.lives <= 0){
                gameHasEnded(false,score);
            }

            xDraw.add(playerShip.x);
            yDraw.add(playerShip.y);
            bitMapDraw.add(playerShipBitmap);

            //Update and draw Enemies
            List<EnemyShips> enemysToRemove = new ArrayList<>();
            if (enemies.isEmpty()) {
                //Do Nothing
            } else {
                for (EnemyShips enemyShip : enemies){
                    enemyShip.update();
                    Random shootChance = new Random();

                    //shoot enemy projectiles
                    int chance = shootChance.nextInt(100);
                    if (chance == 1){
                        float projectileX = enemyShip.x + (EnemyShips.WIDTH / 2) - (Projectile.PROJECTILE_WIDTH / 2) +10;
                        float projectileY = enemyShip.y + EnemyShips.HEIGHT + Projectile.PROJECTILE_HEIGHT;
                        enemyProjectiles.add(new EnemyProjectile(projectileX, projectileY));
                    }
                    if (enemyShip.lives <= 0){
                        enemysToRemove.add(enemyShip);
                    } else {
                        xDraw.add(enemyShip.x);
                        yDraw.add(enemyShip.y);
                        bitMapDraw.add(enemyShipBitmap);
                    }
                }
            }
            enemies.removeAll(enemysToRemove);

            // Update and draw enemy projectiles
            List<EnemyProjectile> enemyProjectilesToRemove = new ArrayList<>();
            List<EnemyProjectile> copyOfEnemyprojectiles = new ArrayList<>(enemyProjectiles);
            for (EnemyProjectile enemyProjectile : copyOfEnemyprojectiles) {
                enemyProjectile.update();

                //HitDetection for player
                //Reduce enemy ship's lives
                if (playerShip.isHitBy(enemyProjectile)) {
                    playerShip.loseLife();
                    lifes[lifeCount].setVisibility(View.INVISIBLE);
                    lifeCount++;
                    if (playerShip.lives <= 0){
                        gameHasEnded(false,score);
                    }
                    enemyProjectilesToRemove.add(enemyProjectile); // Remove the projectile
                    break; // No need to check other enemies for this projectile
                }

                if (enemyProjectile.y + Projectile.PROJECTILE_HEIGHT < 0) {
                    enemyProjectilesToRemove.add(enemyProjectile);
                } else {
                    xDraw.add(enemyProjectile.x);
                    yDraw.add(enemyProjectile.y);
                    bitMapDraw.add(enemyProjectileBitmap);
                }
            }
            enemyProjectiles.removeAll(enemyProjectilesToRemove);

            // Update and draw player projectiles
            List<PlayerProjectile> playerProjectilesToRemove = new ArrayList<>();
            List<PlayerProjectile> copyOfPlayerprojectiles = new ArrayList<>(playerProjectiles);
            for (PlayerProjectile playerProjectile : copyOfPlayerprojectiles) {
                playerProjectile.update();
                //HitDetection for enemies
                for (EnemyShips enemyShip : enemies) {
                    if (enemyShip.isHitBy(playerProjectile)) {
                        score += 20;
                        updateScore();
                        enemyShip.loseLife(); // Reduce enemy ship's lives
                        playerProjectilesToRemove.add(playerProjectile); // Remove the projectile
                        break; // No need to check other enemies for this projectile
                    }
                }
                if (playerProjectile.y + Projectile.PROJECTILE_HEIGHT < 0) {
                    playerProjectilesToRemove.add(playerProjectile);
                } else {
                    xDraw.add(playerProjectile.x);
                    yDraw.add(playerProjectile.y);
                    bitMapDraw.add(playerProjectileBitmap);
                }
            }
            playerProjectiles.removeAll(playerProjectilesToRemove);

            // Calls initEnemies if all enemies are dead the method handles the Enemy spawn
            if (enemies.isEmpty()){
                initializeEnemies();
            }

            // Drawing panel does the drawing
            panel.draw(xDraw, yDraw,bitMapDraw);
        }
    }


        private void initializeEnemies() {

            // This method handles the Enemy spawn and checks if one of the endings (All Enemys have been defeated) has been achieved
            if (enemycount < MAX_ENEMY_COUNT){
                enemycount +=1;
            } else {
                gameHasEnded(true,score);
            }

            //Checks how many rows of enemys are there
            float[] enemyDistancePerRow;
            if (enemycount % MAX_ENEMY_PER_ROW + 1 == 0){
                enemyDistancePerRow = new float[enemycount /5];
            } else {
                enemyDistancePerRow = new float[(enemycount /5) + 1];
            }

            //Calculates distances that should be between enemys
            for (int i = 0; i < enemyDistancePerRow.length ; i++){
                if (i == enemyDistancePerRow.length - 1){
                    int howManyEnemiesAreInSameRow = enemycount % 5; // cause there are can be 5 enemys in a row

                    enemyDistancePerRow[i] = (screenWidth / (howManyEnemiesAreInSameRow + 1)) - EnemyShips.WIDTH / 2;
                } else {
                    enemyDistancePerRow[i] = (screenWidth / (MAX_ENEMY_PER_ROW + 1)) - EnemyShips.WIDTH / 2;
                }
            }

            //Enemy spawning
            float lastShipPositionX = 0;
            int rowCount = 0;
            for (int i = 1; i <= enemycount; i++) {
                float y = (float) (EnemyShips.HEIGHT + (EnemyShips.HEIGHT * rowCount * 1.5));
                float x;
                if (i % MAX_ENEMY_PER_ROW  == 1) {
                    x =  enemyDistancePerRow[rowCount] + lastShipPositionX;

                } else {
                    x =  enemyDistancePerRow[rowCount] + lastShipPositionX;
                }

                EnemyShips ship = new EnemyShips(x,y);
                ship.setBorders((int)(ship.x + (enemyDistancePerRow[rowCount] / 2)),(int)(ship.x - (enemyDistancePerRow[rowCount] / 2)));
                enemies.add(ship);
                lastShipPositionX = x;

                if (i % MAX_ENEMY_PER_ROW  == 0) {
                    rowCount++;
                    lastShipPositionX = 0;
                }
            }
        }
}

