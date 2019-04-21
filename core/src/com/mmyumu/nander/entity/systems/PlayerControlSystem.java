package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.ParticleEffectManager;
import com.mmyumu.nander.controller.KeyboardController;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.PlayerComponent;
import com.mmyumu.nander.entity.components.StateComponent;

public class PlayerControlSystem extends IteratingSystem {

    ComponentMapper<PlayerComponent> pm;
    ComponentMapper<B2dBodyComponent> bodm;
    ComponentMapper<StateComponent> sm;
    KeyboardController controller;
    private LevelFactory lvlFactory;


    @SuppressWarnings("unchecked")
    public PlayerControlSystem(KeyboardController keyCon, LevelFactory lvlFactory) {
        super(Family.all(PlayerComponent.class).get());
        this.controller = keyCon;
        this.lvlFactory = lvlFactory;
        pm = ComponentMapper.getFor(PlayerComponent.class);
        bodm = ComponentMapper.getFor(B2dBodyComponent.class);
        sm = ComponentMapper.getFor(StateComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        B2dBodyComponent b2body = bodm.get(entity);
//        StateComponent state = sm.get(entity);
        PlayerComponent player = pm.get(entity);

//        System.out.println("b2body.body.getPosition().x=" + b2body.body.getPosition().x);
        player.getCamera().position.x = b2body.getBody().getPosition().x;
        player.getCamera().position.y = b2body.getBody().getPosition().y;


        System.out.println("B2D Body " + b2body + " x=" + b2body.getBody().getPosition().x + ", y=" + b2body.getBody().getPosition().y);

//        if (b2body.body.getLinearVelocity().y == 0) {
//            if (state.get() == StateComponent.STATE_FALLING) {
//                state.set(StateComponent.STATE_NORMAL);
//            }
//            if (b2body.body.getLinearVelocity().x != 0 && state.get() != StateComponent.STATE_MOVING) {  // NEW
//                state.set(StateComponent.STATE_MOVING);
//            }
//        }

//        if (b2body.body.getLinearVelocity().y < 0 && state.get() == StateComponent.STATE_FALLING) {
//            // player is actually falling. check if they are on platform
//            if (player.onPlatform) {
//                //overwrite old y value with 0 t stop falling but keep x vel
//                b2body.body.setLinearVelocity(b2body.body.getLinearVelocity().x, 0f);
//            }
//        }

        // make player teleport higher
        if (player.isOnSpring()) {
            //b2body.body.applyLinearImpulse(0, 175f, b2body.body.getWorldCenter().x,b2body.body.getWorldCenter().y, true);
            //add particle effect at feet
            lvlFactory.makeParticleEffect(ParticleEffectManager.SMOKE, b2body.getBody().getPosition().x, b2body.getBody().getPosition().y);
            // move player
            b2body.getBody().setTransform(b2body.getBody().getPosition().x, b2body.getBody().getPosition().y + 10, b2body.getBody().getAngle());
            player.setOnSpring(false);
        }


        if (controller.left) {
            b2body.getBody().setLinearVelocity(MathUtils.lerp(b2body.getBody().getLinearVelocity().x, -10f, 0.5f), b2body.getBody().getLinearVelocity().y);
//            b2body.getBody().setLinearVelocity(-10, 0);
//            b2body.getBody().applyLinearImpulse(10, 0, player.camera.position.x, player.camera.position.y, true);
//            b2body.getBody().applyLinearImpulse(new Vector2(-10f, 0), b2body.getBody().getWorldCenter(), true);
        }
        if (controller.right) {
            b2body.getBody().setLinearVelocity(MathUtils.lerp(b2body.getBody().getLinearVelocity().x, 10f, 0.5f), b2body.getBody().getLinearVelocity().y);
//            b2body.getBody().setLinearVelocity(10, 0);
        }

        if (!controller.left && !controller.right) {
            b2body.getBody().setLinearVelocity(MathUtils.lerp(b2body.getBody().getLinearVelocity().x, 0, 0.5f), b2body.getBody().getLinearVelocity().y);
//            b2body.getBody().setLinearVelocity(0, b2body.getBody().getLinearVelocity().y);
        }

        if (controller.up) {
            b2body.getBody().setLinearVelocity(b2body.getBody().getLinearVelocity().x, MathUtils.lerp(b2body.getBody().getLinearVelocity().y, 10f, 0.5f));
//            b2body.getBody().setLinearVelocity(0, 10);
        }
        if (controller.down) {
            b2body.getBody().setLinearVelocity(b2body.getBody().getLinearVelocity().x, MathUtils.lerp(b2body.getBody().getLinearVelocity().y, -10f, 0.5f));
//            b2body.getBody().setLinearVelocity(0, -10);
        }

        if (!controller.up && !controller.down) {
            b2body.getBody().setLinearVelocity(b2body.getBody().getLinearVelocity().x, MathUtils.lerp(b2body.getBody().getLinearVelocity().y, 0, 0.5f));
//            b2body.getBody().setLinearVelocity(b2body.getBody().getLinearVelocity().x, 0);
        }


        if (player.getTimeSinceLastShot() > 0) {
            player.setTimeSinceLastShot(player.getTimeSinceLastShot() - deltaTime);
        }

        if (controller.isMouse1Down) { // if mouse button is pressed
            // user wants to fire
            if (player.getTimeSinceLastShot() <= 0) { // check the player hasn't just shot
                //player can shoot so do player shoot
                Vector3 mousePos = new Vector3(controller.mouseLocation.x, controller.mouseLocation.y, 0); // get mouse position
                player.getCamera().unproject(mousePos); // convert position from screen to box2d world position
                float speed = 10f;  // set the speed of the bullet
                float shooterX = b2body.getBody().getPosition().x; // get player location
                float shooterY = b2body.getBody().getPosition().y; // get player location
                float velx = mousePos.x - shooterX; // get distance from shooter to target on x plain
                float vely = mousePos.y - shooterY; // get distance from shooter to target on y plain
                float length = (float) Math.sqrt(velx * velx + vely * vely); // get distance to target direct
                if (length != 0) {
                    velx = velx / length;  // get required x velocity to aim at target
                    vely = vely / length;  // get required y velocity to aim at target
                }
                // create a bullet
                lvlFactory.createBullet(shooterX, shooterY, velx * speed, vely * speed);
                //reset timeSinceLastShot
                player.setTimeSinceLastShot(player.getShootDelay());
            }
        }
    }
}