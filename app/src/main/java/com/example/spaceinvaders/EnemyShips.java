package com.example.spaceinvaders;

public class EnemyShips {
    public float x, y, width, height;
    public int lives;

    public EnemyShips(float x, float y, float width, float height, int lives) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lives = lives;
    }

    public void loseLife() {
        lives--;
    }

    public boolean isHitBy(Projectile projectile) {
        return projectile.x < x + width &&
                projectile.x + projectile.width > x &&
                projectile.y < y + height &&
                projectile.y + projectile.height > y;
    }
}