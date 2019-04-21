package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.List;

/*
 *  Stores collision data such as entity that this entity has collided with
 */
public class CollisionComponent implements Component, Pool.Poolable {
    private List<Entity> collisionEntities = new ArrayList<>();

    @Override
    public void reset() {
        collisionEntities = new ArrayList<>();
    }

    public List<Entity> getCollisionEntities() {
        return collisionEntities;
    }

    public void setCollisionEntities(List<Entity> collisionEntities) {
        this.collisionEntities = collisionEntities;
    }
}