package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.PositionComponent;
import com.mmyumu.nander.entity.components.TransformComponent;

public class PhysicsSystem extends IteratingSystem {

    private static final float MAX_STEP_TIME = 1 / 60f;
    private static float accumulator = 0f;
    private final ComponentMapper<B2dBodyComponent> b2dBodyComponentMapper;
    private final ComponentMapper<TransformComponent> transformComponentMapper;
    private final ComponentMapper<PositionComponent> positionComponentMapper;

    private final World world;
    private final Array<Entity> bodiesQueue;


    @SuppressWarnings("unchecked")
    public PhysicsSystem(World world) {
//        super(Family.all(B2dBodyComponent.class, TransformComponent.class, PositionComponent.class).get());
        super(Family.all(B2dBodyComponent.class, TransformComponent.class).get());
        this.world = world;
        this.bodiesQueue = new Array<>();

        b2dBodyComponentMapper = ComponentMapper.getFor(B2dBodyComponent.class);
        transformComponentMapper = ComponentMapper.getFor(TransformComponent.class);
        positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        if (accumulator >= MAX_STEP_TIME) {
            world.step(MAX_STEP_TIME, 6, 2);
            accumulator -= MAX_STEP_TIME;

            //Entity Queue
            for (Entity entity : bodiesQueue) {
                TransformComponent transformComponent = transformComponentMapper.get(entity);
                PositionComponent positionComponent = positionComponentMapper.get(entity);

                B2dBodyComponent bodyComponent = b2dBodyComponentMapper.get(entity);
                Vector2 position = bodyComponent.body.getPosition();
//                System.out.println("Entity " + entity + " [before set from body] x=" + positionComponent.getX() + ", y=" + positionComponent.getY());
                positionComponent.x = position.x;
                positionComponent.y = position.y;
//                System.out.println("Entity " + entity + "  [after set from body] x=" + positionComponent.getX() + ", y=" + positionComponent.getY());
                transformComponent.rotation = bodyComponent.body.getAngle() * MathUtils.radiansToDegrees;
                if (bodyComponent.dead) {
                    System.out.println("Removing a body and entity");
                    world.destroyBody(bodyComponent.body);
                    getEngine().removeEntity(entity);
                }
            }

            bodiesQueue.clear();
        }

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        bodiesQueue.add(entity);
    }
}