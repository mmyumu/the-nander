package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class RenderingComponent implements Component, Pool.Poolable {

    public enum RenderingType {
        MAP, OVERLAY, OBJECTS, PARTICLES
    }

    public RenderingType renderingType;

    @Override
    public void reset() {
        renderingType = null;
    }
}
