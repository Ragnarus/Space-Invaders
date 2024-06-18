package com.example.spaceinvaders;

import java.util.Random;

public class EnemyShips {
    public float x;
    public float y;
    public float width;
    public float height;
    public int lives;
    public int speed;
    public float rightBorder;
    public float leftBorder;
    public float startLocation;
    public float goalLocation;

    public EnemyShips(float x, float y, float width, float height, int lives) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lives = lives;
        this.speed = 1;
        startLocation = x;
        goalLocation = x;
    }


    public void setBorders(float rightBorder, float leftBorder){
        this.rightBorder = rightBorder;
        this.leftBorder = leftBorder;
    }

    public void update() {
        if(x < goalLocation){
            x += speed;
        } else if(x > goalLocation){
            x -= speed;
        } else if (x == goalLocation) {
            if (x == startLocation){
                setNewGoalLocation();
            }else {
                goalLocation = startLocation;
            }
        }
    }

    private void setNewGoalLocation(){
        Random random = new Random();
        goalLocation = random.nextInt((int) (rightBorder - leftBorder)) + leftBorder;
    }

    public void loseLife() {
        lives--;
    }

    public boolean isHitBy(PlayerProjectile playerProjectile) {
        return playerProjectile.x < x + width &&
                playerProjectile.x + playerProjectile.width > x &&
                playerProjectile.y < y + height &&
                playerProjectile.y + playerProjectile.height > y;
    }
}