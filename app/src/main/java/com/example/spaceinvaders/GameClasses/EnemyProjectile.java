package com.example.spaceinvaders.GameClasses;

public class EnemyProjectile extends Projectile{
    //Class for EnemyProjectile

    public EnemyProjectile(float x, float y) {
        super(x, y);
    }

    @Override
    public void update() {
        this.y += PROJECTILE_SPEED;

    }
}
