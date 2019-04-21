package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Pool;

public class PlayerComponent implements Component, Pool.Poolable {
    private OrthographicCamera camera = null;
    private boolean onPlatform = false;
    private boolean onSpring = false;
    private boolean dead = false;
    private float shootDelay = 0.5f;
    private float timeSinceLastShot = 0f;

    @Override
    public void reset() {
        camera = null;
        onPlatform = false;
        onSpring = false;
        dead = false;
        shootDelay = 0.5f;
        timeSinceLastShot = 0f;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public boolean isOnPlatform() {
        return onPlatform;
    }

    public void setOnPlatform(boolean onPlatform) {
        this.onPlatform = onPlatform;
    }

    public boolean isOnSpring() {
        return onSpring;
    }

    public void setOnSpring(boolean onSpring) {
        this.onSpring = onSpring;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public float getShootDelay() {
        return shootDelay;
    }

    public void setShootDelay(float shootDelay) {
        this.shootDelay = shootDelay;
    }

    public float getTimeSinceLastShot() {
        return timeSinceLastShot;
    }

    public void setTimeSinceLastShot(float timeSinceLastShot) {
        this.timeSinceLastShot = timeSinceLastShot;
    }
}
