package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ParticleEffectComponent implements Component, Poolable {
    public PooledEffect particleEffect;
    public boolean attached = false;
    public float xOffset = 0;
    public float yOffset = 0;
    public float timeTilDeath = 0.5f; // add a 1 second delay
    public boolean dead = false;
    public Body attachedBody;

    @Override
    public void reset() {
        particleEffect.free(); // free the pooled effect
        particleEffect = null; // empty this component's particle effect
        xOffset = 0;
        yOffset = 0;
        attached = false;
        dead = false;
        attachedBody = null;
        timeTilDeath = 0.5f;
    }
}