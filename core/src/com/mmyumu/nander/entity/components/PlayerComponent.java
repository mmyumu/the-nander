package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Pool;

public class PlayerComponent implements Component, Pool.Poolable {
    public OrthographicCamera camera = null;
    public boolean onPlatform = false;
    public boolean onSpring = false;
    public boolean dead = false;
    public float shootDelay = 0.5f;
    public float timeSinceLastShot = 0f;
    public Entity particleEffect;

    @Override
    public void reset() {
        camera = null;
        onPlatform = false;
        onSpring = false;
        dead = false;
        shootDelay = 0.5f;
        timeSinceLastShot = 0f;
        particleEffect = null;
    }
}
