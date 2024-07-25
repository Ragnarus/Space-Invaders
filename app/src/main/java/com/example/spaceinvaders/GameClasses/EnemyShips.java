package com.example.spaceinvaders.GameClasses;

import java.util.Random;

public class EnemyShips {
    public float x;
    public float y;
    public static final float WIDTH = 100;
    public static final float HEIGHT = 100;
    public int lives;
    public int speed;
    public float rightBorder;
    public float leftBorder;
    public float startLocation;
    public float goalLocation;

    public EnemyShips(float x, float y) {
        this.x = x;
        this.y = y;
        this.lives = 1;
        this.speed = 1;
        startLocation = x;
        goalLocation = x;
    }


    public void setBorders(float rightBorder, float leftBorder){
        this.rightBorder = rightBorder;
        this.leftBorder = leftBorder;
    }

    public void update() {
        // Use a small epsilon value for floating-point comparison
        final float epsilon = 0.1f;

        if (Math.abs(x - goalLocation) > epsilon) {
            if (x < goalLocation) {
                x += speed;
                if (x > goalLocation) {
                    x = goalLocation; // Prevent overshooting
                }
            } else if (x > goalLocation) {
                x -= speed;
                if (x < goalLocation) {
                    x = goalLocation; // Prevent overshooting
                }
            }
        } else {
            if (Math.abs(x - startLocation) <= epsilon) {
                setNewGoalLocation();
            } else {
                goalLocation = startLocation;
            }
        }
    }

    private void setNewGoalLocation() {
        Random random = new Random();
        goalLocation = leftBorder + random.nextInt((int) (rightBorder - leftBorder));
    }


    public void loseLife() {
        lives--;
    }

    public boolean isHitBy(PlayerProjectile playerProjectile) {
        return playerProjectile.x < x + WIDTH &&
                playerProjectile.x + playerProjectile.getWidth() > x &&
                playerProjectile.y < y + HEIGHT &&
                playerProjectile.y + playerProjectile.getHeight() > y;
    }
}