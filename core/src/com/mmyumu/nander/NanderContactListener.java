package com.mmyumu.nander;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mmyumu.nander.entity.components.CollisionComponent;

public class NanderContactListener implements ContactListener {

    public NanderContactListener() {
    }

    @Override
    public void beginContact(Contact contact) {
//        Fixture fa = contact.getFixtureA();
//        Fixture fb = contact.getFixtureB();
//
//        if (fa.getBody().getUserData() instanceof Entity &&
//                fa.getBody().getUserData() instanceof Entity) {
//            Entity entityA = (Entity) fa.getBody().getUserData();
//            Entity entityB = (Entity) fb.getBody().getUserData();
//
//            CollisionComponent colA = entityA.getComponent(CollisionComponent.class);
//            CollisionComponent colB = entityB.getComponent(CollisionComponent.class);
//
//            if (colA != null) {
//                colA.collisionEntities.add(entityB);
//                System.out.println("Collision between " + colA + " and " + entityB);
//            }
//            if (colB != null) {
//                colB.collisionEntities.add(entityA);
//                System.out.println("Collision between " + colB + " and " + entityA);
//            }
//        }
    }

    @Override
    public void endContact(Contact contact) {
        System.out.println("Contact end");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}