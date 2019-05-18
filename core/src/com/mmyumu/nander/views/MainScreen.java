package com.mmyumu.nander.views;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mmyumu.nander.DFUtils;
import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.NanderGame;
import com.mmyumu.nander.NanderScreen;
import com.mmyumu.nander.controller.Inputs;
import com.mmyumu.nander.controller.KeyboardController;
import com.mmyumu.nander.entity.components.ParticleEffectComponent;
import com.mmyumu.nander.entity.components.PlayerComponent;
import com.mmyumu.nander.entity.systems.AnimationSystem;
import com.mmyumu.nander.entity.systems.BulletSystem;
import com.mmyumu.nander.entity.systems.CollisionSystem;
import com.mmyumu.nander.entity.systems.EnemySystem;
import com.mmyumu.nander.entity.systems.PhysicsDebugSystem;
import com.mmyumu.nander.entity.systems.PhysicsSystem;
import com.mmyumu.nander.entity.systems.PlayerControlSystem;
import com.mmyumu.nander.entity.systems.RenderingSystem;
import com.mmyumu.nander.entity.systems.WallSystem;


public class MainScreen implements Screen {
    /**
     * One Plus 6 resolution: 2280*1080 -> ratio=2.11111
     **/
    private final ComponentMapper<ParticleEffectComponent> particleEffectComponentMapper;

    private final Viewport viewport;
    private final InputMultiplexer inputMultiplexer;
    private final Inputs inputs;
    private final Overlay overlay;
    private Entity player;
    private NanderGame parent;
    private OrthographicCamera camera;
    private KeyboardController controller;
    private SpriteBatch spriteBatch;
    private PooledEngine engine;
    private LevelFactory levelFactory;

    /**
     * @param nanderGame the game
     */
    public MainScreen(NanderGame nanderGame) {
        parent = nanderGame;
        parent.assetManager.queueAddSounds();
        parent.assetManager.queueAddParticleEffects();
        parent.assetManager.manager.finishLoading();

        inputs = new Inputs();
        controller = new KeyboardController(inputs);
        engine = new PooledEngine();


        spriteBatch = new SpriteBatch();

        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        camera = renderingSystem.getCamera();
        viewport = renderingSystem.getViewport();

        spriteBatch.setProjectionMatrix(camera.combined);

        levelFactory = new LevelFactory(engine, parent.assetManager);
        levelFactory.createMap();
        player = levelFactory.createPlayer(camera);

        engine.addSystem(new AnimationSystem());
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsDebugSystem(levelFactory.world, renderingSystem.getCamera()));
        engine.addSystem(new PhysicsSystem(levelFactory.world));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(inputs, levelFactory, viewport));
        engine.addSystem(new EnemySystem());
        engine.addSystem(new WallSystem(levelFactory));
        engine.addSystem(new BulletSystem(levelFactory));

        particleEffectComponentMapper = ComponentMapper.getFor(ParticleEffectComponent.class);

        overlay = new Overlay(inputs, parent.assetManager);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(overlay);
        inputMultiplexer.addProcessor(controller);
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);


        overlay.act();
        overlay.draw();

        //check if player is dead. if so show end screen
        PlayerComponent pc = (player.getComponent(PlayerComponent.class));
        if (pc.dead) {
            if (pc.particleEffect != null) {
                particleEffectComponentMapper.get(pc.particleEffect).dead = true;
            }
            DFUtils.log("YOU DIED : back to menu you go!");
            parent.lastScore = (int) pc.camera.position.y;
            parent.changeScreen(NanderScreen.ENDGAME);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

    // reset world or start world again
    public void resetWorld() {
        System.out.println("Resetting world");
        engine.removeAllEntities();
        levelFactory.resetWorld();

        levelFactory.createMap();
        player = levelFactory.createPlayer(camera);

        int wallWidth = (int) (1 * RenderingSystem.PPM);
        int wallHeight = (int) (60 * RenderingSystem.PPM);
        TextureRegion wallRegion = DFUtils.makeTextureRegion(wallWidth, wallHeight, "222222FF");
        levelFactory.createWalls(wallRegion); //TODO make some damn images for this stuff

        // reset controller controls (fixes bug where controller stuck on directrion if died in that position)
        inputs.reset();
    }
}
