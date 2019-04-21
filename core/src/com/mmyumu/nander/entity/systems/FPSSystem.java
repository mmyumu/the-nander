package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mmyumu.nander.entity.components.FPSComponent;
import com.mmyumu.nander.entity.components.OverlayComponent;

public class FPSSystem extends IteratingSystem {
    public FPSSystem() {
        super(Family.all(FPSComponent.class, OverlayComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        OverlayComponent overlayComponent = entity.getComponent(OverlayComponent.class);
        overlayComponent.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + " fps");
    }
}
