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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

public class GamePanel extends SurfaceView implements Runnable, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SurfaceHolder mSurfaceHolder;

    private Paint mPaint;
    private boolean mRunning;
    private Thread mThread;

    private int playerShipXCord;
    private int playerShipYCord;
    private float xRotationRate;
    private static final int playerShipSizex = 180;
    private static final int playerShipSizey = 100;
    private int screenHeight;
    private int screenWidth;
    private Bitmap playerShipBitmap;
    private Bitmap  playerProjectileBitmap;
    private int shipMovementSpeed = 50;

    private List<Projectile> projectiles;
    private static final float PROJECTILE_WIDTH = 20;
    private static final float PROJECTILE_HEIGHT = 40;
    private static final float PROJECTILE_SPEED = 20;


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
        resume();

        // Load the player ship bitmap from resources
        playerShipBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playerfighteridle);
        playerProjectileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playerprojectile);


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

    }

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
                playerShipXCord += xRotationRate * shipMovementSpeed;  // Modify as needed
                if (playerShipXCord < 0) {
                    playerShipXCord = 0;
                }
                if (playerShipXCord + playerShipSizex > screenWidth) {
                    playerShipXCord = screenWidth - playerShipSizex;
                }

                // Update and draw projectiles
                mPaint.setColor(Color.WHITE);
                List<Projectile> projectilesToRemove = new ArrayList<>();
                for (Projectile projectile : projectiles) {
                    projectile.update();
                    if (projectile.y + projectile.height < 0) {
                        projectilesToRemove.add(projectile);
                    } else {
                        canvas.drawBitmap(playerProjectileBitmap, projectile.x,projectile.y , mPaint);
                    }
                }
                projectiles.removeAll(projectilesToRemove);


                // Draw player ship (example representation)
                mPaint.setColor(Color.WHITE);
                canvas.drawBitmap(playerShipBitmap, playerShipXCord, playerShipYCord, mPaint);
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