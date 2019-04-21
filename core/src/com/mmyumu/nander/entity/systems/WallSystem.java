package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.WallComponent;

public class WallSystem extends IteratingSystem {
    private ComponentMapper<B2dBodyComponent> bm = ComponentMapper.getFor(B2dBodyComponent.class);
    private LevelFactory lvlFactory;

    public WallSystem(LevelFactory lvlFactory) {
        super(Family.all(WallComponent.class).get());
        this.lvlFactory = lvlFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // get current y level of player entity
        float currentyLevel = lvlFactory.player.getComponent(B2dBodyComponent.class).getBody().getPosition().y;
        // get the body component of the wall we're updating
        Body bod = bm.get(entity).getBody();
        //set the walls y position to match the player
        bod.setTransform(bod.getPosition().x, currentyLevel, bod.getAngle());
    }
}