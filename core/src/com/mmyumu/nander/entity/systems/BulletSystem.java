package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.BulletComponent;
import com.mmyumu.nander.entity.components.ParticleEffectComponent;

public class BulletSystem extends IteratingSystem {

    private final ComponentMapper<B2dBodyComponent> b2dBodyComponentMapper;
    private final ComponentMapper<BulletComponent> bulletComponentMapper;
    private final ComponentMapper<ParticleEffectComponent> particleEffectComponentMapper;
    private LevelFactory levelFactory;

    @SuppressWarnings("unchecked")
    public BulletSystem(LevelFactory levelFactory) {
        super(Family.all(BulletComponent.class).get());
        this.levelFactory = levelFactory;

        b2dBodyComponentMapper = ComponentMapper.getFor(B2dBodyComponent.class);
        bulletComponentMapper = ComponentMapper.getFor(BulletComponent.class);
        particleEffectComponentMapper = ComponentMapper.getFor(ParticleEffectComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //get box 2d body and bullet components
        B2dBodyComponent b2body = b2dBodyComponentMapper.get(entity);
        BulletComponent bullet = bulletComponentMapper.get(entity);

        // apply bullet velocity to bullet body
        b2body.body.setLinearVelocity(bullet.xVelocity, bullet.yVelocity);

        // get player pos
        B2dBodyComponent playerBodyComp = b2dBodyComponentMapper.get(levelFactory.player);
        float px = playerBodyComp.body.getPosition().x;
        float py = playerBodyComp.body.getPosition().y;

        //get bullet pos
        float bx = b2body.body.getPosition().x;
        float by = b2body.body.getPosition().y;

        // if bullet is 20 units away from player on any axis then it is probably off screen
        if (bx - px > 20 || by - py > 20) {
            bullet.dead = true;
        }

        //check if bullet is dead
        if (bullet.dead) {
            System.out.println("Bullet died");
            if (bullet.particleEffect != null) {
                ParticleEffectComponent particleEffectComponent = particleEffectComponentMapper.get(bullet.particleEffect);
                if (particleEffectComponent != null) {
                    particleEffectComponent.dead = true;
                }
            }

            b2body.dead = true;
        }
    }
}