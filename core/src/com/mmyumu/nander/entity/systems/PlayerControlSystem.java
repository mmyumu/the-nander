package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.controller.KeyboardController;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.ParticleEffectComponent;
import com.mmyumu.nander.entity.components.PlayerComponent;
import com.mmyumu.nander.entity.components.ZComponent;

public class PlayerControlSystem extends IteratingSystem {
    private static final float BULLET_SPEED = 15f;
    private static final float PLAYER_SPEED = 10f;
    private static final float ACCELERATION = 1f;
    private static final float TRAIL_SPREAD_ANGLE = 60f;

    private final ComponentMapper<PlayerComponent> pm;
    private final ComponentMapper<B2dBodyComponent> bodm;
    private final KeyboardController controller;
    private final ComponentMapper<ParticleEffectComponent> particleEffectComponentMapper;
    private final ComponentMapper<ZComponent> zComponentMapper;
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
        this.particleEffectComponentMapper = ComponentMapper.getFor(ParticleEffectComponent.class);
        this.zComponentMapper = ComponentMapper.getFor(ZComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        B2dBodyComponent b2body = bodm.get(entity);
        PlayerComponent playerComponent = pm.get(entity);

        playerComponent.camera.position.x = b2body.body.getPosition().x;
        playerComponent.camera.position.y = b2body.body.getPosition().y;

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
        b2body.body.setLinearVelocity(MathUtils.lerp(b2body.body.getLinearVelocity().x, x, ACCELERATION), MathUtils.lerp(b2body.body.getLinearVelocity().y, y, ACCELERATION));

        // Create trail particle if needed
        if (playerMovement.isZero()) {
            if (playerComponent.particleEffect != null) {
                ParticleEffectComponent particleEffectComponent = particleEffectComponentMapper.get(playerComponent.particleEffect);
                if (particleEffectComponent != null) {
                    particleEffectComponent.particleEffect.getEmitters().forEach(ParticleEmitter::allowCompletion);
                }
            }
        } else {
            if (playerComponent.particleEffect == null) {
                playerComponent.particleEffect = lvlFactory.makeTrail(b2body);
            } else {
                ParticleEffectComponent particleEffectComponent = particleEffectComponentMapper.get(playerComponent.particleEffect);
                if (particleEffectComponent == null) {
                    playerComponent.particleEffect = lvlFactory.makeTrail(b2body);
                }
            }
        }

        // Setup the Z for the trail
        if (playerComponent.particleEffect != null) {
            ZComponent zComponent = zComponentMapper.get(playerComponent.particleEffect);
            if (zComponent != null) {
                if (controller.up) {
                    zComponent.z = 15f;
                } else if (!playerMovement.isZero()) {
                    zComponent.z = 5f;
                }
            }

            if (!playerMovement.isZero()) {
                float low = (playerMovement.angle() + 180) % 360;
                ParticleEffectComponent particleEffectComponent = particleEffectComponentMapper.get(playerComponent.particleEffect);
                particleEffectComponent.particleEffect.getEmitters().forEach(emitter -> {
                    emitter.getAngle().setLow(low);
                    emitter.getAngle().setHighMin(low - TRAIL_SPREAD_ANGLE);
                    emitter.getAngle().setHighMax(low + TRAIL_SPREAD_ANGLE);
                });
            }
        }


        if (playerComponent.timeSinceLastShot > 0) {
            playerComponent.timeSinceLastShot = playerComponent.timeSinceLastShot - deltaTime;
        }

        if (controller.isMouse1Down) { // if mouse button is pressed
            // user wants to fire
            if (playerComponent.timeSinceLastShot <= 0) { // check the playerComponent hasn't just shot
                //playerComponent can shoot so do playerComponent shoot
                mousePos.set(controller.mouseLocation.x, controller.mouseLocation.y, 0);
                viewport.unproject(mousePos);
                float shooterX = b2body.body.getPosition().x;
                float shooterY = b2body.body.getPosition().y;

                System.out.println("Player x=" + shooterX + ", y=" + shooterY + " Mouse x=" + mousePos.x + ", y=" + mousePos.y);

                bulletMovement.set(mousePos.x - shooterX, mousePos.y - shooterY);
                bulletMovement.nor();
                lvlFactory.createBullet(shooterX, shooterY, bulletMovement.x * BULLET_SPEED, bulletMovement.y * BULLET_SPEED);
                playerComponent.timeSinceLastShot = playerComponent.shootDelay;
            }
        }
    }
}