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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends SurfaceView implements Runnable, SensorEventListener {
    //TODO use Accelometer
    private Context mContext;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SurfaceHolder mSurfaceHolder;
    private int screenHeight;
    private int screenWidth;
    private Paint mPaint;
    private boolean mRunning;
    private Thread mThread;
    private ComTool comTool;
    private int score;

    //PlayerStats
    private final int MAX_PLAYER_Lives = 3;
    private  int currentPlayerLives;

    private PlayerShip playerShip;
    private float xRotationRate;
    private static final int playerShipSizex = 180;
    private static final int playerShipSizey = 100;
    private Bitmap playerShipBitmap;
    private final int shipMovementSpeed = 50;

    //Projectile Properties
    private List<PlayerProjectile> playerProjectiles;
    private List<EnemyProjectile> enemyProjectiles;
    private static final float PROJECTILE_WIDTH = 20;
    private static final float PROJECTILE_HEIGHT = 40;
    private static final float PROJECTILE_SPEED = 20;
    private Bitmap  playerProjectileBitmap;
    private Bitmap  enemyProjectileBitmap;

    //Enemy Properties
    private List<EnemyShips> enemies;
    private static final float ENEMY_WIDTH = 57 * 2;
    private static final float ENEMY_HEIGHT = 103;
    private Bitmap  enemyShipBitmap;
    private static  int ENEMYCOUNT;
    private static final int MAX_ENEMY_COUNT = 5;

    private static  final int MAX_ENEMY_PER_ROW = 5;
    private static  final int ENEMY_LIVES = 1;

    public GamePanel(Context context) {
        super(context);
        init(context);
        mContext = context;
    }

    public GamePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public GamePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        mContext = context;
    }

    public GamePanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        mContext = context;
    }

    public interface ComTool {
        void gameHasEnded(boolean won, int score);
    }

    public void setComTool(ComTool comTool) {
        this.comTool = comTool;
    }

    private void init(Context context) {
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mPaint.setColor(Color.DKGRAY);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        score = 0;
        playerProjectiles = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        enemies = new ArrayList<>();
        ENEMYCOUNT = 0;
        currentPlayerLives = MAX_PLAYER_Lives;


        // Load the bitmaps from resources
        playerShipBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playerfighteridle);
        playerProjectileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playerprojectile);

        Bitmap enemyShipBitmapToScale = BitmapFactory.decodeResource(getResources(), R.drawable.enemyshipidle);
        enemyShipBitmap = Bitmap.createScaledBitmap(enemyShipBitmapToScale, 100, 100, true);
        enemyProjectileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemyprojectile);


        resume();
        // Get the screen dimensions
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                screenWidth = getWidth();
                screenHeight = getHeight();
                float x = (screenWidth / 2) - playerShipSizex;
                float y = screenHeight - playerShipSizey - 200;//200 = Abstand zum "boden"
                playerShip = new PlayerShip(x,y,playerShipSizex,playerShipSizey, MAX_PLAYER_Lives);
            }
        });


    }

    // Initialisierung der feindlichen Schiffe
    private void initializeEnemies() {
        //Enemy Init
        if (ENEMYCOUNT < MAX_ENEMY_COUNT){
            ENEMYCOUNT +=1;
        } else {
            comTool.gameHasEnded(true,score);
        }


        float y = ENEMY_HEIGHT;
        float distance;
        float distanceSecondRow = 0;
        if (ENEMYCOUNT <= MAX_ENEMY_PER_ROW){
            distance = (screenWidth - (ENEMY_WIDTH * ENEMYCOUNT)) / (ENEMYCOUNT + 1);
        } else {
            distance = (screenWidth - (ENEMY_WIDTH * MAX_ENEMY_PER_ROW)) / (MAX_ENEMY_PER_ROW + 1);
            distanceSecondRow = (screenWidth - (ENEMY_WIDTH * (ENEMYCOUNT - MAX_ENEMY_PER_ROW))) / (ENEMYCOUNT + 1 - MAX_ENEMY_PER_ROW);
        }

        float x = distance;

        for (int i = 1; i <= ENEMYCOUNT; i++) {
            EnemyShips ship =new EnemyShips((int)x,y,ENEMY_WIDTH,ENEMY_HEIGHT, ENEMY_LIVES);
            ship.setBorders((int)(ship.x + (distance / 2)),(int)(ship.x - (distance / 2)));
            enemies.add(ship);

            if (i <= MAX_ENEMY_PER_ROW){
                x += ENEMY_WIDTH + distance;
            } else {
                x += ENEMY_WIDTH + distanceSecondRow;
            }

            if (i % 5 == 0){
                x = distanceSecondRow;
                y += ENEMY_HEIGHT +  ENEMY_HEIGHT/2;
            }
        }
    }

    //TODO:  Enemy  movement  animation maybe
    @Override
    public void run() {
        while (mRunning) {

            if (!mSurfaceHolder.getSurface().isValid()) {
                continue;
            }

            Canvas canvas = mSurfaceHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);

                // Update player position based on sensor data
                playerShip.x += xRotationRate * shipMovementSpeed;
                if (playerShip.x < 0) {
                    playerShip.x = 0;
                }
                if (playerShip.x + playerShipSizex > screenWidth) {
                    playerShip.x = screenWidth - playerShipSizex;
                }
                if (currentPlayerLives <= 0){
                    comTool.gameHasEnded(false,score);
                }
                canvas.drawBitmap(playerShipBitmap, playerShip.x, playerShip.y, mPaint);


                //Update and draw Enemies
                mPaint.setColor(Color.WHITE);
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
                            float projectileX = enemyShip.x + (float)(ENEMY_WIDTH / 2) - (PROJECTILE_WIDTH / 2) +10;
                            float projectileY = enemyShip.y + PROJECTILE_HEIGHT;
                            enemyProjectiles.add(new EnemyProjectile(projectileX, projectileY, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, PROJECTILE_SPEED));
                        }

                        if (enemyShip.lives <= 0){
                            enemysToRemove.add(enemyShip);
                        } else {
                            canvas.drawBitmap(enemyShipBitmap, enemyShip.x,enemyShip.y , mPaint);
                        }
                    }
                }
                enemies.removeAll(enemysToRemove);

                // Update and draw enemy projectiles
                mPaint.setColor(Color.WHITE);
                List<EnemyProjectile> enemyProjectilesToRemove = new ArrayList<>();
                List<EnemyProjectile> copyOfEnemyprojectiles = new ArrayList<>(enemyProjectiles);
                for (EnemyProjectile enemyProjectile : copyOfEnemyprojectiles) {
                    enemyProjectile.update();

                    //HitDetection for player
                    if (playerShip.isHitBy(enemyProjectile)) {
                        playerShip.loseLife(); // Reduce enemy ship's lives
                        if (playerShip.lives <= 0){
                            comTool.gameHasEnded(false,score);
                        }
                        enemyProjectilesToRemove.add(enemyProjectile); // Remove the projectile
                        break; // No need to check other enemies for this projectile
                    }

                    if (enemyProjectile.y + enemyProjectile.height < 0) {
                        enemyProjectilesToRemove.add(enemyProjectile);
                    } else {
                        canvas.drawBitmap(enemyProjectileBitmap, enemyProjectile.x, enemyProjectile.y , mPaint);
                    }
                }
                enemyProjectiles.removeAll(enemyProjectilesToRemove);


                // Update and draw player projectiles
                mPaint.setColor(Color.WHITE);
                List<PlayerProjectile> playerProjectilesToRemove = new ArrayList<>();
                List<PlayerProjectile> copyOfPlayerprojectiles = new ArrayList<>(playerProjectiles);
                for (PlayerProjectile playerProjectile : copyOfPlayerprojectiles) {
                    playerProjectile.update();
                    //HitDetection for enemies
                    for (EnemyShips enemyShip : enemies) {
                        if (enemyShip.isHitBy(playerProjectile)) {
                            score += 20;
                            enemyShip.loseLife(); // Reduce enemy ship's lives
                            playerProjectilesToRemove.add(playerProjectile); // Remove the projectile
                            break; // No need to check other enemies for this projectile
                        }
                    }
                    if (playerProjectile.y + playerProjectile.height < 0) {
                        playerProjectilesToRemove.add(playerProjectile);
                    } else {
                        canvas.drawBitmap(playerProjectileBitmap, playerProjectile.x, playerProjectile.y , mPaint);
                    }
                }
                playerProjectiles.removeAll(playerProjectilesToRemove);


                if (enemies.isEmpty()){
                    initializeEnemies();
                }
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
            float projectileX = playerShip.x + (float)(playerShipSizex / 2) - (PROJECTILE_WIDTH / 2) +10;
            float projectileY = playerShip.y - PROJECTILE_HEIGHT;
            playerProjectiles.add(new PlayerProjectile(projectileX, projectileY, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, PROJECTILE_SPEED));
            return true;
        }
        return super.onTouchEvent(event);
    }
}