package com.example.spaceinvaders.GameClasses;

public class PlayerShip {
    public float x;
    public float y;
    public static final float WIDTH = 200;
    public static final float HEIGHT = 200;
    public int lives;
    public static final int MAX_LIVES = 3;
    public int speed = 10;


    public PlayerShip(float x, float y) {
        this.x = x;
        this.y = y;

        this.lives = MAX_LIVES;
    }

    public void loseLife() {
        lives--;
    }

    public boolean isHitBy(Projectile playerProjectile) {
        return playerProjectile.x < x + WIDTH &&
                playerProjectile.x + playerProjectile.PROJECTILE_WIDTH > x &&
                playerProjectile.y < y + HEIGHT &&
                playerProjectile.y + playerProjectile.PROJECTILE_HEIGHT > y;
    }
}
