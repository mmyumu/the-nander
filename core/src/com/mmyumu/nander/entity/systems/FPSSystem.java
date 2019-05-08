package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mmyumu.nander.entity.components.FPSComponent;
import com.mmyumu.nander.entity.components.OverlayComponent;
import com.mmyumu.nander.entity.components.TextComponent;

public class FPSSystem extends IteratingSystem {
    private final ComponentMapper<TextComponent> textComponentMapper;

    public FPSSystem() {
        super(Family.all(FPSComponent.class, OverlayComponent.class, TextComponent.class).get());

        textComponentMapper = ComponentMapper.getFor(TextComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TextComponent textComponent = textComponentMapper.get(entity);
        textComponent.text = "FPS: " + Gdx.graphics.getFramesPerSecond() + " fps";
    }
}
