package com.mmyumu.nander.views;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mmyumu.nander.DFUtils;
import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.NanderGame;
import com.mmyumu.nander.controller.KeyboardController;
import com.mmyumu.nander.entity.components.FPSComponent;
import com.mmyumu.nander.entity.components.OverlayComponent;
import com.mmyumu.nander.entity.components.ParticleEffectComponent;
import com.mmyumu.nander.entity.components.PlayerComponent;
import com.mmyumu.nander.entity.components.PositionComponent;
import com.mmyumu.nander.entity.systems.AnimationSystem;
import com.mmyumu.nander.entity.systems.BulletSystem;
import com.mmyumu.nander.entity.systems.CollisionSystem;
import com.mmyumu.nander.entity.systems.EnemySystem;
import com.mmyumu.nander.entity.systems.FPSSystem;
import com.mmyumu.nander.entity.systems.ParticleEffectSystem;
import com.mmyumu.nander.entity.systems.PhysicsDebugSystem;
import com.mmyumu.nander.entity.systems.PhysicsSystem;
import com.mmyumu.nander.entity.systems.PlayerControlSystem;
import com.mmyumu.nander.entity.systems.RenderingSystem;
import com.mmyumu.nander.entity.systems.WallSystem;
import com.mmyumu.nander.entity.systems.WaterFloorSystem;


public class MainScreen implements Screen {
    /* One Plus 6 resolution: 2280*1080 -> ratio=2.11111 */
    private static final int VIEWPORT_WIDTH = 32;
    private static final int VIEWPORT_HEIGHT = 15;
    private final ComponentMapper<ParticleEffectComponent> particleEffectComponentMapper;

    private FitViewport viewport;
    private Entity player;
    private NanderGame parent;
    private OrthographicCamera camera;
    private KeyboardController controller;
    private SpriteBatch spriteBatch;
    private PooledEngine engine;
    private LevelFactory lvlFactory;

    private Sound ping;
    private Sound boing;
    private TextureAtlas atlas;


    /**
     * @param nanderGame the game
     */
    public MainScreen(NanderGame nanderGame) {
        parent = nanderGame;
        parent.assetManager.queueAddSounds();
        parent.assetManager.queueAddParticleEffects();
        parent.assetManager.manager.finishLoading();
        ping = parent.assetManager.manager.get("sounds/ping.wav", Sound.class);
        boing = parent.assetManager.manager.get("sounds/boing.wav", Sound.class);
        controller = new KeyboardController();
        engine = new PooledEngine();
        lvlFactory = new LevelFactory(engine, parent.assetManager);


        spriteBatch = new SpriteBatch();
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        camera = renderingSystem.getCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        viewport.getCamera().update();
        ParticleEffectSystem particleSystem = new ParticleEffectSystem(spriteBatch, camera);
        spriteBatch.setProjectionMatrix(camera.combined);



        engine.addSystem(new AnimationSystem());

        engine.addSystem(new FPSSystem());
//        engine.addSystem(mapRenderingSystem);
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsDebugSystem(lvlFactory.world, renderingSystem.getCamera()));
        engine.addSystem(particleSystem);
//        engine.addSystem(overlayRenderingSystem);
        engine.addSystem(new PhysicsSystem(lvlFactory.world));

        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller, lvlFactory, viewport));
        engine.addSystem(new EnemySystem());
        player = lvlFactory.createPlayer(camera);
        engine.addSystem(new WallSystem(lvlFactory));
        engine.addSystem(new WaterFloorSystem(lvlFactory));
        engine.addSystem(new BulletSystem(lvlFactory));


        OverlayComponent overlayComponent = new OverlayComponent();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.setX(10f);
        positionComponent.setY(Gdx.graphics.getHeight() - 10f);
        positionComponent.setZ(0f);

        Entity fpsEntity = engine.createEntity();
        fpsEntity.add(new FPSComponent());
        fpsEntity.add(overlayComponent);
        fpsEntity.add(positionComponent);
        engine.addEntity(fpsEntity);

        lvlFactory.createMap();

        particleEffectComponentMapper = ComponentMapper.getFor(ParticleEffectComponent.class);
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);

        //check if player is dead. if so show end screen
        PlayerComponent pc = (player.getComponent(PlayerComponent.class));
        if (pc.isDead()) {
            if (pc.getParticleEffect() != null) {
                particleEffectComponentMapper.get(pc.getParticleEffect()).isDead = true;
            }
            DFUtils.log("YOU DIED : back to menu you go!");
            parent.lastScore = (int) pc.getCamera().position.y;
            parent.changeScreen(NanderGame.ENDGAME);
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
        lvlFactory.resetWorld();

        player = lvlFactory.createPlayer(camera);

        int wallWidth = (int) (1 * RenderingSystem.PPM);
        int wallHeight = (int) (60 * RenderingSystem.PPM);
        TextureRegion wallRegion = DFUtils.makeTextureRegion(wallWidth, wallHeight, "222222FF");
        lvlFactory.createWalls(wallRegion); //TODO make some damn images for this stuff

        // reset controller controls (fixes bug where controller stuck on directrion if died in that position)
        controller.left = false;
        controller.right = false;
        controller.up = false;
        controller.down = false;
        controller.isMouse1Down = false;
        controller.isMouse2Down = false;
        controller.isMouse3Down = false;

    }
}
