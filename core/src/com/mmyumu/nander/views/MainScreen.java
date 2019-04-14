package com.mmyumu.nander.views;

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
import com.mmyumu.nander.DFUtils;
import com.mmyumu.nander.LevelFactory;
import com.mmyumu.nander.NanderGame;
import com.mmyumu.nander.controller.KeyboardController;
import com.mmyumu.nander.entity.components.PlayerComponent;
import com.mmyumu.nander.entity.systems.AnimationSystem;
import com.mmyumu.nander.entity.systems.BulletSystem;
import com.mmyumu.nander.entity.systems.CollisionSystem;
import com.mmyumu.nander.entity.systems.EnemySystem;
import com.mmyumu.nander.entity.systems.LevelGenerationSystem;
import com.mmyumu.nander.entity.systems.ParticleEffectSystem;
import com.mmyumu.nander.entity.systems.PhysicsDebugSystem;
import com.mmyumu.nander.entity.systems.PhysicsSystem;
import com.mmyumu.nander.entity.systems.PlayerControlSystem;
import com.mmyumu.nander.entity.systems.RenderingSystem;
import com.mmyumu.nander.entity.systems.WallSystem;
import com.mmyumu.nander.entity.systems.WaterFloorSystem;


public class MainScreen implements Screen {
    private Entity player;
    private NanderGame parent;
    private OrthographicCamera cam;
    private KeyboardController controller;
    private SpriteBatch sb;
    private PooledEngine engine;
    private LevelFactory lvlFactory;

    private Sound ping;
    private Sound boing;
    private TextureAtlas atlas;


    /**
     * @param nanderGame
     */
    public MainScreen(NanderGame nanderGame) {
        parent = nanderGame;
        parent.assetManager.queueAddSounds();
        parent.assetManager.queueAddParticleEffects();
        parent.assetManager.manager.finishLoading();
//        atlas = parent.assetManager.manager.get("images/game.atlas", TextureAtlas.class);
        ping = parent.assetManager.manager.get("sounds/ping.wav", Sound.class);
        boing = parent.assetManager.manager.get("sounds/boing.wav", Sound.class);
        controller = new KeyboardController();
        engine = new PooledEngine();
        lvlFactory = new LevelFactory(engine, parent.assetManager);


        sb = new SpriteBatch();
        RenderingSystem renderingSystem = new RenderingSystem(sb);
        cam = renderingSystem.getCamera();
        ParticleEffectSystem particleSystem = new ParticleEffectSystem(sb, cam);
        sb.setProjectionMatrix(cam.combined);

        engine.addSystem(new AnimationSystem());
        engine.addSystem(new PhysicsSystem(lvlFactory.world));
        engine.addSystem(renderingSystem);
        engine.addSystem(particleSystem);
        engine.addSystem(new PhysicsDebugSystem(lvlFactory.world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller, lvlFactory));
        engine.addSystem(new EnemySystem());
        player = lvlFactory.createPlayer(cam);
        engine.addSystem(new WallSystem(lvlFactory));
        engine.addSystem(new WaterFloorSystem(lvlFactory));
        engine.addSystem(new BulletSystem(lvlFactory));
        engine.addSystem(new LevelGenerationSystem(lvlFactory));

        lvlFactory.createFloor();
        lvlFactory.createWaterFloor();

        int wallWidth = (int) (1 * RenderingSystem.PPM);
        int wallHeight = (int) (60 * RenderingSystem.PPM);
        TextureRegion wallRegion = DFUtils.makeTextureRegion(wallWidth, wallHeight, "222222FF");
        lvlFactory.createWalls(wallRegion); //TODO make some damn images for this stuff
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
        if(pc.isDead){
            DFUtils.log("YOU DIED : back to menu you go!");
            parent.lastScore = (int) pc.cam.position.y;
            parent.changeScreen(NanderGame.ENDGAME);
        }
    }

    @Override
    public void resize(int width, int height) {
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
    public void resetWorld(){
        System.out.println("Resetting world");
        engine.removeAllEntities();
        lvlFactory.resetWorld();

        player = lvlFactory.createPlayer(cam);
        lvlFactory.createFloor();
        lvlFactory.createWaterFloor();

        int wallWidth = (int) (1*RenderingSystem.PPM);
        int wallHeight = (int) (60*RenderingSystem.PPM);
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