package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ZComponent implements Component, Pool.Poolable {
    public Float z;

    @Override
    public void reset() {
        z = null;
    }
}
