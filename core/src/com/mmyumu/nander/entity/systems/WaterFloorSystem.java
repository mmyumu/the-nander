package com.mmyumu.nander.entity.systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.WaterFloorComponent;

public class WaterFloorSystem extends IteratingSystem {
    private final ComponentMapper<B2dBodyComponent> b2dBodyComponentMapper;
    private LevelFactory lvlFactory;

    public WaterFloorSystem(LevelFactory lvlFactory) {
        super(Family.all(WaterFloorComponent.class).get());
        this.lvlFactory = lvlFactory;

        b2dBodyComponentMapper = ComponentMapper.getFor(B2dBodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        float currentyLevel = b2dBodyComponentMapper.get(lvlFactory.player).getBody().getPosition().y;
        Body body = b2dBodyComponentMapper.get(entity).getBody();

        float speed = (currentyLevel / 1000);

        speed = speed > 0.25f ? 0.25f : speed;

        // make sure water doesn't get too far behind
        if (body.getPosition().y < currentyLevel - 50) {
            body.setTransform(body.getPosition().x, currentyLevel - 50, body.getAngle());
        }

        body.setTransform(body.getPosition().x, body.getPosition().y + speed, body.getAngle());
    }

}