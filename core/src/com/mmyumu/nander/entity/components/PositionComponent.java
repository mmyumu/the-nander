package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PositionComponent implements Component, Pool.Poolable {
    public Float x = null;
    public Float y = null;
    public Float z = null;

    @Override
    public void reset() {
        x = null;
        y = null;
        z = null;
    }
}
