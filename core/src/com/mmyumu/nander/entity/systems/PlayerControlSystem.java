package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.controller.KeyboardController;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.PlayerComponent;

public class PlayerControlSystem extends IteratingSystem {

    private final ComponentMapper<PlayerComponent> pm;
    private final ComponentMapper<B2dBodyComponent> bodm;
    private final KeyboardController controller;
    private LevelFactory lvlFactory;
    private Viewport viewport;
    private Vector3 mousePos;


    @SuppressWarnings("unchecked")
    public PlayerControlSystem(KeyboardController keyCon, LevelFactory lvlFactory, Viewport viewport) {
        super(Family.all(PlayerComponent.class).get());
        this.controller = keyCon;
        this.lvlFactory = lvlFactory;
        this.viewport = viewport;

        this.mousePos = new Vector3();

        this.pm = ComponentMapper.getFor(PlayerComponent.class);
        this.bodm = ComponentMapper.getFor(B2dBodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        B2dBodyComponent b2body = bodm.get(entity);
        PlayerComponent player = pm.get(entity);

        player.getCamera().position.x = b2body.getBody().getPosition().x;
        player.getCamera().position.y = b2body.getBody().getPosition().y;

        if (controller.left) {
            b2body.getBody().setLinearVelocity(MathUtils.lerp(b2body.getBody().getLinearVelocity().x, -10f, 0.5f), b2body.getBody().getLinearVelocity().y);
        }
        if (controller.right) {
            b2body.getBody().setLinearVelocity(MathUtils.lerp(b2body.getBody().getLinearVelocity().x, 10f, 0.5f), b2body.getBody().getLinearVelocity().y);
        }

        if (!controller.left && !controller.right) {
            b2body.getBody().setLinearVelocity(MathUtils.lerp(b2body.getBody().getLinearVelocity().x, 0, 0.5f), b2body.getBody().getLinearVelocity().y);
        }

        if (controller.up) {
            b2body.getBody().setLinearVelocity(b2body.getBody().getLinearVelocity().x, MathUtils.lerp(b2body.getBody().getLinearVelocity().y, 10f, 0.5f));
        }
        if (controller.down) {
            b2body.getBody().setLinearVelocity(b2body.getBody().getLinearVelocity().x, MathUtils.lerp(b2body.getBody().getLinearVelocity().y, -10f, 0.5f));
        }

        if (!controller.up && !controller.down) {
            b2body.getBody().setLinearVelocity(b2body.getBody().getLinearVelocity().x, MathUtils.lerp(b2body.getBody().getLinearVelocity().y, 0, 0.5f));
        }


        if (player.getTimeSinceLastShot() > 0) {
            player.setTimeSinceLastShot(player.getTimeSinceLastShot() - deltaTime);
        }

        if (controller.isMouse1Down) { // if mouse button is pressed
            // user wants to fire
            if (player.getTimeSinceLastShot() <= 0) { // check the player hasn't just shot
                //player can shoot so do player shoot
                mousePos.set(controller.mouseLocation.x, controller.mouseLocation.y, 0);
                viewport.unproject(mousePos);
                float speed = 10f;  // set the speed of the bullet
                float shooterX = b2body.getBody().getPosition().x; // get player location
                float shooterY = b2body.getBody().getPosition().y; // get player location

                System.out.println("Player x=" + shooterX + ", y=" + shooterY + " Mouse x=" + mousePos.x + ", y=" + mousePos.y);

                float velx = mousePos.x - shooterX; // get distance from shooter to target on x plain
                float vely = mousePos.y - shooterY; // get distance from shooter to target on y plain
                float length = (float) Math.sqrt(velx * velx + vely * vely); // get distance to target direct
                if (length != 0) {
                    velx = velx / length;  // get required x velocity to aim at target
                    vely = vely / length;  // get required y velocity to aim at target
                }
                lvlFactory.createBullet(shooterX, shooterY, velx * speed, vely * speed);
                player.setTimeSinceLastShot(player.getShootDelay());
            }
        }
    }
}