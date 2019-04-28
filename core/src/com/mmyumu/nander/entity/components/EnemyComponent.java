package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class EnemyComponent implements Component, Pool.Poolable {
    public boolean dead = false;
    public float xPosCenter = -1;
    public boolean isGoingLeft = false;

    @Override
    public void reset() {
        dead = false;
        xPosCenter = -1;
        isGoingLeft = false;
    }
}