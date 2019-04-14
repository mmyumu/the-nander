package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.BulletComponent;
import com.mmyumu.nander.entity.components.Mapper;

public class BulletSystem extends IteratingSystem {

    private LevelFactory lvlFactory;

    @SuppressWarnings("unchecked")
    public BulletSystem(LevelFactory lvlFactory) {
        super(Family.all(BulletComponent.class).get());
        this.lvlFactory = lvlFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //get box 2d body and bullet components
        B2dBodyComponent b2body = Mapper.b2dCom.get(entity);
        BulletComponent bullet = Mapper.bulletCom.get(entity);

        // apply bullet velocity to bullet body
        b2body.body.setLinearVelocity(bullet.xVel, bullet.yVel);

        // get player pos
        B2dBodyComponent playerBodyComp = Mapper.b2dCom.get(lvlFactory.player);
        float px = playerBodyComp.body.getPosition().x;
        float py = playerBodyComp.body.getPosition().y;

        //get bullet pos
        float bx = b2body.body.getPosition().x;
        float by = b2body.body.getPosition().y;

        // if bullet is 20 units away from player on any axis then it is probably off screen
        if (bx - px > 20 || by - py > 20) {
            bullet.isDead = true;
        }

//check if bullet is dead
        if(bullet.isDead){
            System.out.println("Bullet died");
            Mapper.peCom.get(bullet.particleEffect).isDead = true;
            b2body.isDead = true;
        }
    }
}