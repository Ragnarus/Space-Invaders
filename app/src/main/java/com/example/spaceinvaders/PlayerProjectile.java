package com.example.spaceinvaders;

public class PlayerProjectile {
    public float x;
    public float y;
    public float width;
    public float height;
    public float speedY;

    public PlayerProjectile(float x, float y, float width, float height, float speedY) {
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
