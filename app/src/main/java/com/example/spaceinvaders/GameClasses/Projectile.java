package com.example.spaceinvaders.GameClasses;

public abstract class Projectile {
    public static final float PROJECTILE_WIDTH = 20;
    public static final float PROJECTILE_HEIGHT = 40;
    public static final float PROJECTILE_SPEED = 10;

    public float x;
    public float y;


    public Projectile(float x, float y){
        this.x = x;
        this.y = y;
    }

    public abstract void update();

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return PROJECTILE_WIDTH;
    }
    public float getHeight() {
        return PROJECTILE_HEIGHT;
    }
    public float getSpeed() {
        return PROJECTILE_SPEED;
    }
}
