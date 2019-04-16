package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mmyumu.nander.entity.components.TiledMapComponent;

public class MapRenderingSystem extends IteratingSystem {
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    public MapRenderingSystem(OrthographicCamera camera) {
        super(Family.all(TiledMapComponent.class).get());
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        mapRenderer.render();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
//        ParticleEffectComponent pec = Mapper.peCom.get(entity);
        TiledMapComponent component = entity.getComponent(TiledMapComponent.class);
        mapRenderer = component.getMapRenderer();
        mapRenderer.setView(camera);
    }
}
