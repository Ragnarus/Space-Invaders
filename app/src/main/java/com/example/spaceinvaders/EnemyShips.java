package com.example.spaceinvaders;

public class EnemyShips {
    public float x;
    public float y;
    public float width;
    public float height;
    public int lives;

    public float rightBorder;
    public float leftBorder;

    public EnemyShips(float x, float y, float width, float height, int lives) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lives = lives;
    }


    public void setBorders(float rightBorder, float leftBorder){
        this.rightBorder = rightBorder;
        this.leftBorder = leftBorder;
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