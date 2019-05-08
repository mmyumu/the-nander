package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class OverlayComponent implements Component, Pool.Poolable {

    public enum Type {
        TEXT, TEXTURE
    }

    public Type type;

    @Override
    public void reset() {
        type = null;
    }
}
