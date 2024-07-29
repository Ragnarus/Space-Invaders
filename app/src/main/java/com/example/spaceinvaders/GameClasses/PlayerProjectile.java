package com.example.spaceinvaders.GameClasses;



public class PlayerProjectile extends Projectile {
    //Class for PlayerProjectile

    public PlayerProjectile(float x, float y) {
        super(x, y);
    }

    @Override
    public void update() {
        this.y -= PROJECTILE_SPEED;
    }

}
