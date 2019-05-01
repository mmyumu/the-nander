package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.EnemyComponent;

public class EnemySystem extends IteratingSystem {

    private final ComponentMapper<EnemyComponent> enemyComponentMapper;
    private final ComponentMapper<B2dBodyComponent> b2dBodyComponentMapper;

    @SuppressWarnings("unchecked")
    public EnemySystem() {
        super(Family.all(EnemyComponent.class).get());
        enemyComponentMapper = ComponentMapper.getFor(EnemyComponent.class);
        b2dBodyComponentMapper = ComponentMapper.getFor(B2dBodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnemyComponent enemyCom = enemyComponentMapper.get(entity);        // get EnemyComponent
        B2dBodyComponent bodyCom = b2dBodyComponentMapper.get(entity);    // get B2dBodyComponent

        // get distance of enemy from its original start position (pad center)
        float distFromOrig = Math.abs(enemyCom.xPosCenter - bodyCom.body.getPosition().x);

        // if distance > 1 swap direction
        enemyCom.goingLeft = (distFromOrig > 1) ? !enemyCom.goingLeft : enemyCom.goingLeft;

        // set speed base on direction
        float speed = enemyCom.goingLeft ? -0.01f : 0.01f;

        // apply speed to body
        bodyCom.body.setTransform(bodyCom.body.getPosition().x + speed,
                bodyCom.body.getPosition().y,
                bodyCom.body.getAngle());

        // check for dead enemies
        if (enemyCom.dead) {
            bodyCom.dead = true;
        }
    }
}