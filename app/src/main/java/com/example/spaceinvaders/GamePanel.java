package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class GamePanel extends SurfaceView implements Runnable, SensorEventListener {
    //TODO use Accelometer
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SurfaceHolder mSurfaceHolder;
    private int screenHeight;
    private int screenWidth;
    private Paint mPaint;
    private boolean mRunning;
    private Thread mThread;

    //PlayerStats
    private final int MAX_PLAYER_Lives = 3;
    private  int currentPlayerLives;
    private int playerShipXCord;
    private int playerShipYCord;
    private float xRotationRate;
    private static final int playerShipSizex = 180;
    private static final int playerShipSizey = 100;

    private Bitmap playerShipBitmap;

    private int shipMovementSpeed = 50;

    //Projectile Properties
    private List<Projectile> projectiles;
    private static final float PROJECTILE_WIDTH = 20;
    private static final float PROJECTILE_HEIGHT = 40;
    private static final float PROJECTILE_SPEED = 20;
    private Bitmap  playerProjectileBitmap;

    //Enemy Properties
    private List<EnemyShips> enemies;
    private static final float ENEMY_WIDTH = 57 * 2;
    private static final float ENEMY_HEIGHT = 103;
    private Bitmap  enemyShipBitmap;
    private static  final int ENEMY_COUNT = 1;
    private static  final int MAX_ENEMY_PER_ROW = 5;
    private static  final int ENEMY_LIVES = 3;

    public GamePanel(Context context) {
        super(context);
        init(context);
    }

    public GamePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GamePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public GamePanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mPaint.setColor(Color.DKGRAY);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        projectiles = new ArrayList<>();
        enemies = new ArrayList<>();
        currentPlayerLives = MAX_PLAYER_Lives;
        enemies = new ArrayList<>();

        // Load the bitmaps from resources
        playerShipBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playerfighteridle);
        playerProjectileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playerprojectile);

        Bitmap enemyShipBitmapToScale = BitmapFactory.decodeResource(getResources(), R.drawable.enemyshipidle);
        enemyShipBitmap = Bitmap.createScaledBitmap(enemyShipBitmapToScale, 100, 100, true);

        // Get the screen dimensions
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                screenWidth = getWidth();
                screenHeight = getHeight();
                playerShipXCord = (screenWidth/2 )- playerShipSizex;
                playerShipYCord = screenHeight - playerShipSizey - 200;//200 = Abstand zum "boden"
            }
        });
        initializeEnemies();
        resume();
    }

    // Initialisierung der feindlichen Schiffe
    private void initializeEnemies() {//geht net gscheid
        //Enemy Init
        int x = 0;
        float y = ENEMY_HEIGHT/2;
        float distance = (ENEMY_WIDTH * MAX_ENEMY_PER_ROW)/5;
        for (int i = 1; i <= ENEMY_COUNT; i++) {
            enemies.add(new EnemyShips(x,y,ENEMY_WIDTH,ENEMY_HEIGHT, ENEMY_LIVES));
            x += ENEMY_WIDTH * 2;//-22 comes from a sense of proportion
            if (i % 5 == 0){
                x = 0;
                y += ENEMY_HEIGHT +  ENEMY_HEIGHT/2;
            }

            Log.d("MyInfo", "x: " + x + " y: " + y + " EnemyWidth: " + ENEMY_WIDTH );
            Log.d("MyInfo", "screenHeight = " +  screenHeight + ", screenWidth = " + screenWidth + ", Distance = " + distance);
        }
    }

    //TODO: Player hit detection, Enemy projectiles and enemy spawn logic and animation
    @Override
    public void run() {
        while (mRunning) {
            //TODO why is screen hight and width always 0

            if (!mSurfaceHolder.getSurface().isValid()) {
                continue;
            }

            Canvas canvas = mSurfaceHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);

                // Update player position based on sensor data
                playerShipXCord += xRotationRate * shipMovementSpeed;  // Modify as needed
                if (playerShipXCord < 0) {
                    playerShipXCord = 0;
                }
                if (playerShipXCord + playerShipSizex > screenWidth) {
                    playerShipXCord = screenWidth - playerShipSizex;
                }
                if (currentPlayerLives <= 0){
                    //TODO endgame and tell endgameFrag if lose or win
                }

                // Draw player ship
                canvas.drawBitmap(playerShipBitmap, playerShipXCord, playerShipYCord, mPaint);


                //Update and draw Enemys
                mPaint.setColor(Color.WHITE);
                List<EnemyShips> enemysToRemove = new ArrayList<>();
                //All Enemys destroyed if yes end game
                if (enemies.isEmpty()) {

                } else {
                    for (EnemyShips enemyShip : enemies){
                        if (enemyShip.lives <= 0){
                            enemysToRemove.add(enemyShip);
                        } else {
                            canvas.drawBitmap(enemyShipBitmap, enemyShip.x,enemyShip.y , mPaint);
                        }
                    }
                }

                enemies.removeAll(enemysToRemove);

                // Draw player ship
                canvas.drawBitmap(playerShipBitmap, playerShipXCord, playerShipYCord, mPaint);


                //Update and draw Enemys
                mPaint.setColor(Color.WHITE);
                List<EnemyShips> enemysToRemove = new ArrayList<>();
                //All Enemys destroyed if yes end game
                if (enemies.isEmpty()) {

                } else {
                    for (EnemyShips enemyShip : enemies){
                        if (enemyShip.lives <= 0){
                            enemysToRemove.add(enemyShip);
                        } else {
                            canvas.drawBitmap(enemyShipBitmap, enemyShip.x,enemyShip.y , mPaint);
                        }
                    }
                }

                enemies.removeAll(enemysToRemove);

                // Update and draw projectiles
                mPaint.setColor(Color.WHITE);
                List<Projectile> projectilesToRemove = new ArrayList<>();
                for (Projectile projectile : projectiles) {
                    projectile.update();

                    //HitDetection
                    for (EnemyShips enemyShip : enemies) {
                        if (enemyShip.isHitBy(projectile)) {
                            enemyShip.lives--; // Reduce enemy ship's lives
                            projectilesToRemove.add(projectile); // Remove the projectile
                            break; // No need to check other enemies for this projectile
                        }
                    }

                    if (projectile.y + projectile.height < 0) {
                        projectilesToRemove.add(projectile);
                    } else {
                        canvas.drawBitmap(playerProjectileBitmap, projectile.x,projectile.y , mPaint);
                    }
                }
                projectiles.removeAll(projectilesToRemove);


                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void pause() {
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

    public void resume() {
        mRunning = true;
        mThread = new Thread(this);
        mThread.start();
        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            xRotationRate = event.values[1];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Do nothing

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Fire a projectile
            float projectileX = playerShipXCord + (float)(playerShipSizex / 2) - (PROJECTILE_WIDTH / 2) +10;
            float projectileY = playerShipYCord - PROJECTILE_HEIGHT;
            projectiles.add(new Projectile(projectileX, projectileY, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, PROJECTILE_SPEED));
            return true;
        }
        return super.onTouchEvent(event);
    }
}