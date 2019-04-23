package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.controller.KeyboardController;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.PlayerComponent;

public class PlayerControlSystem extends IteratingSystem {
    private static final float BULLET_SPEED = 15f;
    private static final float PLAYER_SPEED = 10f;
    private static final float ACCELERATION = 1f;

    private final ComponentMapper<PlayerComponent> pm;
    private final ComponentMapper<B2dBodyComponent> bodm;
    private final KeyboardController controller;
    private LevelFactory lvlFactory;
    private Viewport viewport;

    private final Vector2 playerMovement;
    private final Vector2 bulletMovement;
    private final Vector3 mousePos;


    @SuppressWarnings("unchecked")
    public PlayerControlSystem(KeyboardController keyCon, LevelFactory lvlFactory, Viewport viewport) {
        super(Family.all(PlayerComponent.class).get());
        this.controller = keyCon;
        this.lvlFactory = lvlFactory;
        this.viewport = viewport;

        this.playerMovement = new Vector2();
        this.mousePos = new Vector3();
        this.bulletMovement = new Vector2();

        this.pm = ComponentMapper.getFor(PlayerComponent.class);
        this.bodm = ComponentMapper.getFor(B2dBodyComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        B2dBodyComponent b2body = bodm.get(entity);
        PlayerComponent player = pm.get(entity);

        player.getCamera().position.x = b2body.getBody().getPosition().x;
        player.getCamera().position.y = b2body.getBody().getPosition().y;

        playerMovement.set(0, 0);

        if (controller.left) {
            playerMovement.x -= 1;
        }
        if (controller.right) {
            playerMovement.x += 1;
        }
        if (controller.up) {
            playerMovement.y += 1;
        }
        if (controller.down) {
            playerMovement.y -= 1;
        }


        playerMovement.nor();   // Caps the playerMovement vector at a length of one, even when it's at an odd angle

        float x = playerMovement.x * PLAYER_SPEED;
        float y = playerMovement.y * PLAYER_SPEED;
        b2body.getBody().setLinearVelocity(MathUtils.lerp(b2body.getBody().getLinearVelocity().y, x, ACCELERATION), MathUtils.lerp(b2body.getBody().getLinearVelocity().y, y, ACCELERATION));


        if (player.getTimeSinceLastShot() > 0) {
            player.setTimeSinceLastShot(player.getTimeSinceLastShot() - deltaTime);
        }

        if (controller.isMouse1Down) { // if mouse button is pressed
            // user wants to fire
            if (player.getTimeSinceLastShot() <= 0) { // check the player hasn't just shot
                //player can shoot so do player shoot
                mousePos.set(controller.mouseLocation.x, controller.mouseLocation.y, 0);
                viewport.unproject(mousePos);
                float shooterX = b2body.getBody().getPosition().x;
                float shooterY = b2body.getBody().getPosition().y;

                System.out.println("Player x=" + shooterX + ", y=" + shooterY + " Mouse x=" + mousePos.x + ", y=" + mousePos.y);

                bulletMovement.set(mousePos.x - shooterX, mousePos.y - shooterY);
                bulletMovement.nor();
                lvlFactory.createBullet(shooterX, shooterY, bulletMovement.x * BULLET_SPEED, bulletMovement.y * BULLET_SPEED);
                player.setTimeSinceLastShot(player.getShootDelay());
            }
        }
    }
}