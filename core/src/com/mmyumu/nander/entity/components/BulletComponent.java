package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BulletComponent implements Component, Poolable{
    public Entity particleEffect;
    public float xVelocity = 0;
    public float yVelocity = 0;
    public boolean dead = false;

    @Override
    public void reset() {
        xVelocity = 0;
        yVelocity = 0;
        dead = false;
        particleEffect = null;
    }
}