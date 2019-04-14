package com.mmyumu.nandr.entity.systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.mmyumu.nandr.LevelFactory;
import com.mmyumu.nandr.entity.components.B2dBodyComponent;
import com.mmyumu.nandr.entity.components.WaterFloorComponent;

public class WaterFloorSystem extends IteratingSystem {
    private ComponentMapper<B2dBodyComponent> bm = ComponentMapper.getFor(B2dBodyComponent.class);
    private LevelFactory lvlFactory;

    public WaterFloorSystem(LevelFactory lvlFactory) {
        super(Family.all(WaterFloorComponent.class).get());
        this.lvlFactory = lvlFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // get current y level of player entity
        float currentyLevel = lvlFactory.player.getComponent(B2dBodyComponent.class).body.getPosition().y;
        // get the body component of the wall we're updating
        Body bod = bm.get(entity).body;

        float speed = (currentyLevel / 1000);

        speed = speed > 0.25f ? 0.25f : speed;

        // make sure water doesn't get too far behind
        if (bod.getPosition().y < currentyLevel - 50) {
            bod.setTransform(bod.getPosition().x, currentyLevel - 50, bod.getAngle());
        }

        bod.setTransform(bod.getPosition().x, bod.getPosition().y + speed, bod.getAngle());
    }

}