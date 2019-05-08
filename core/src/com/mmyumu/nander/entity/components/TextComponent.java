package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class TextComponent implements Component, Pool.Poolable {
    public String text;

    @Override
    public void reset() {
        text = null;
    }
}
