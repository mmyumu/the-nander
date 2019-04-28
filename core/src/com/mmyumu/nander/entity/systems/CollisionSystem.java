package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mmyumu.nander.entity.components.BulletComponent;
import com.mmyumu.nander.entity.components.CollisionComponent;
import com.mmyumu.nander.entity.components.EnemyComponent;
import com.mmyumu.nander.entity.components.PlayerComponent;
import com.mmyumu.nander.entity.components.TypeComponent;

import java.util.List;

public class CollisionSystem extends IteratingSystem {
    private final ComponentMapper<CollisionComponent> collisionComponentMapper;
    private final ComponentMapper<TypeComponent> typeComponentMapper;
    private final ComponentMapper<PlayerComponent> playerComponentMapper;
    private final ComponentMapper<EnemyComponent> enemyComponentMapper;
    private final ComponentMapper<BulletComponent> bulletComponentMapper;

    @SuppressWarnings("unchecked")
    public CollisionSystem() {
        super(Family.all(CollisionComponent.class).get());

        collisionComponentMapper = ComponentMapper.getFor(CollisionComponent.class);
        typeComponentMapper = ComponentMapper.getFor(TypeComponent.class);
        playerComponentMapper = ComponentMapper.getFor(PlayerComponent.class);
        enemyComponentMapper = ComponentMapper.getFor(EnemyComponent.class);
        bulletComponentMapper = ComponentMapper.getFor(BulletComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // get collision for this entity
        CollisionComponent collisionComponent = collisionComponentMapper.get(entity);
        //get collided entity
        List<Entity> collidedEntities = collisionComponent.collisionEntities;


        TypeComponent thisType = typeComponentMapper.get(entity);

        // Do Player Collisions
        if (thisType.type == TypeComponent.PLAYER) {
            if (!collidedEntities.isEmpty()) {
                for (Entity collidedEntity : collidedEntities) {
                    TypeComponent type = typeComponentMapper.get(collidedEntity);
                    if (type != null) {
                        switch (type.type) {
                            case TypeComponent.ENEMY:
                                //do player hit enemy thing
                                System.out.println("player hit enemy");
                                PlayerComponent pl = playerComponentMapper.get(entity);
                                pl.dead = true;
                                int score = (int) pl.camera.position.y;
                                System.out.println("Score = " + score);
                                break;
                            case TypeComponent.SCENERY:
                                //do player hit scenery thing
                                playerComponentMapper.get(entity).onPlatform = true;
                                System.out.println("player hit scenery");
                                break;
                            case TypeComponent.SPRING:
                                //do player hit other thing
                                playerComponentMapper.get(entity).onSpring = true;
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
                    }
                }
                collisionComponent.collisionEntities.clear(); // collision handled reset component
            }
        } else if (thisType.type == TypeComponent.ENEMY) {    // Do enemy collisions
            if (!collidedEntities.isEmpty()) {
                for (Entity collidedEntity : collidedEntities) {
                    TypeComponent type = typeComponentMapper.get(collidedEntity);
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
                                EnemyComponent enemy = enemyComponentMapper.get(entity);
                                enemy.dead = true;
                                BulletComponent bullet = bulletComponentMapper.get(collidedEntity);
                                bullet.dead = true;
                                System.out.println("enemy got shot");
                                break;
                            default:
                                System.out.println("No matching type found");
                        }
                    }
                }
                collisionComponent.collisionEntities.clear(); // collision handled reset component
            }
        }
    }
}