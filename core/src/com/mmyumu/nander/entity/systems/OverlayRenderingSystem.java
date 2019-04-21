package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mmyumu.nander.entity.components.OverlayComponent;

import java.util.ArrayList;
import java.util.List;

public class OverlayRenderingSystem extends IteratingSystem {
    private SpriteBatch batch;
    private Camera camera;
    private List<Entity> renderQueue;
    private BitmapFont font;

    private float lastDisplay = 0f;

    public OverlayRenderingSystem(SpriteBatch batch, Camera camera) {
        super(Family.all(OverlayComponent.class).get());
        this.batch = batch;
        this.camera = camera;
        this.renderQueue = new ArrayList<>();
        this.font = new BitmapFont();
        this.font.getData().setScale(0.1f);


    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        //        camera.update();
        this.batch.setProjectionMatrix(camera.combined); //or your matrix to draw GAME WORLD, not UI
        if (!renderQueue.isEmpty()) {
            batch.begin();

            for (Entity entity : renderQueue) {
                OverlayComponent overlayComponent = entity.getComponent(OverlayComponent.class);
//                font.draw(batch, overlayComponent.getText(), overlayComponent.getX(), 10);
                System.out.println(overlayComponent.getText());
            }

            renderQueue.clear();
            batch.end();
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        lastDisplay += deltaTime;

        if (lastDisplay > 0.2) {
            renderQueue.add(entity);
            lastDisplay = 0;
        }
    }
}
