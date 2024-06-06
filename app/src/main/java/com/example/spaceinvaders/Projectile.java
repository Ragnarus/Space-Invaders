package com.example.spaceinvaders;

public class Projectile {
    public float x, y, width, height;
    public float speedY;

    public Projectile(float x, float y, float width, float height, float speedY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speedY = speedY;
    }

    public void update() {
        y -= speedY;
    }
}
