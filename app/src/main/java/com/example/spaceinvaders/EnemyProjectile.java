package com.example.spaceinvaders;

public class EnemyProjectile extends PlayerProjectile{

    public EnemyProjectile(float x, float y, float width, float height, float speedY) {
        super(x, y, width, height, speedY);
    }

    @Override
    public void update(){
        y += speedY;
    }
}
