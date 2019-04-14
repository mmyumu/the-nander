package com.mmyumu.nandr;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mmyumu.nandr.entity.components.CollisionComponent;

public class NAndRContactListener implements ContactListener {

    public NAndRContactListener() {
    }

    @Override
    public void beginContact(Contact contact) {
//        System.out.println("Contact");
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
//        System.out.println(fa.getBody().getType() + " has hit " + fb.getBody().getType());

        if (fa.getBody().getUserData() instanceof Entity &&
                fa.getBody().getUserData() instanceof Entity) {
            Entity entityA = (Entity) fa.getBody().getUserData();
            Entity entityB = (Entity) fb.getBody().getUserData();

            CollisionComponent colA = entityA.getComponent(CollisionComponent.class);
            CollisionComponent colB = entityB.getComponent(CollisionComponent.class);

            if (colA != null) {
                colA.collisionEntities.add(entityB);
                System.out.println("Collision between " + colA + " and " + entityB);
            }
            if (colB != null) {
                colB.collisionEntities.add(entityA);
                System.out.println("Collision between " + colB + " and " + entityA);
            }
        }
//        if (fa.getBody().getUserData() instanceof Entity) {
//            Entity ent = (Entity) fa.getBody().getUserData();
//            entityCollision(ent, fb);
//        } else if (fb.getBody().getUserData() instanceof Entity) {
//            Entity ent = (Entity) fb.getBody().getUserData();
//            entityCollision(ent, fa);
//        }
    }

//    private void entityCollision(Entity ent, Fixture fb) {
//        if (fb.getBody().getUserData() instanceof Entity) {
//            Entity colEnt = (Entity) fb.getBody().getUserData();
//
//            CollisionComponent col = ent.getComponent(CollisionComponent.class);
//            CollisionComponent colb = colEnt.getComponent(CollisionComponent.class);
//
//            if (col != null) {
//                col.collisionEntity = colEnt;
//            }
//            if (colb != null) {
//                colb.collisionEntity = ent;
//            }
//        }
//    }

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