package com.mmyumu.nandr.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mmyumu.nandr.entity.components.CollisionComponent;
import com.mmyumu.nandr.entity.components.EnemyComponent;
import com.mmyumu.nandr.entity.components.PlayerComponent;
import com.mmyumu.nandr.entity.components.TypeComponent;

public class CollisionSystem extends IteratingSystem {
    ComponentMapper<CollisionComponent> cm;
    ComponentMapper<PlayerComponent> pm;
    ComponentMapper<EnemyComponent> em;

    @SuppressWarnings("unchecked")
    public CollisionSystem() {
        super(Family.all(CollisionComponent.class).get());

        cm = ComponentMapper.getFor(CollisionComponent.class);
        pm = ComponentMapper.getFor(PlayerComponent.class);
        em = ComponentMapper.getFor(EnemyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // get collision for this entity
        CollisionComponent cc = cm.get(entity);
        //get collided entity
        Entity collidedEntity = cc.collisionEntity;

        TypeComponent thisType = entity.getComponent(TypeComponent.class);

        // Do Player Collisions
        if (thisType.type == TypeComponent.PLAYER) {
            if (collidedEntity != null) {
                TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
                if (type != null) {
                    switch (type.type) {
                        case TypeComponent.ENEMY:
                            //do player hit enemy thing
                            System.out.println("player hit enemy");
                            PlayerComponent pl = pm.get(entity);
                            pl.isDead = true;
                            int score = (int) pl.cam.position.y;
                            System.out.println("Score = " + score);
                            break;
                        case TypeComponent.SCENERY:
                            //do player hit scenery thing
                            pm.get(entity).onPlatform = true;
                            System.out.println("player hit scenery");
                            break;
                        case TypeComponent.SPRING:
                            //do player hit other thing
                            pm.get(entity).onSpring = true;
                            System.out.println("player hit spring: bounce up");
                            break;
                        case TypeComponent.OTHER:
                            //do player hit other thing
                            System.out.println("player hit other");
                            break;
                        case TypeComponent.BULLET:
                            System.out.println("Player just shot. bullet in player atm");
                            break;
                        default:
                            System.out.println("No matching type found");
                    }
                    cc.collisionEntity = null; // collision handled reset component
                } else {
                    System.out.println("type == null");
                }
            }
        } else if (thisType.type == TypeComponent.ENEMY) {    // Do enemy collisions
            if (collidedEntity != null) {
                TypeComponent type = collidedEntity.getComponent(TypeComponent.class);
                if (type != null) {
                    switch (type.type) {
                        case TypeComponent.PLAYER:
                            System.out.println("enemy hit player");
                            break;
                        case TypeComponent.ENEMY:
                            System.out.println("enemy hit enemy");
                            break;
                        case TypeComponent.SCENERY:
                            System.out.println("enemy hit scenery");
                            break;
                        case TypeComponent.SPRING:
                            System.out.println("enemy hit spring");
                            break;
                        case TypeComponent.OTHER:
                            System.out.println("enemy hit other");
                            break;
                        case TypeComponent.BULLET:
                            EnemyComponent enemy = em.get(entity);
                            enemy.isDead = true;
                            System.out.println("enemy got shot");
                        default:
                            System.out.println("No matching type found");
                    }
                    cc.collisionEntity = null; // collision handled reset component
                } else {
                    System.out.println("type == null");
                }
            }
        }
    }
}