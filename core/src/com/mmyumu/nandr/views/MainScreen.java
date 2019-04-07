package com.mmyumu.nandr.views;

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
import com.mmyumu.nandr.DFUtils;
import com.mmyumu.nandr.LevelFactory;
import com.mmyumu.nandr.NAndRGame;
import com.mmyumu.nandr.controller.KeyboardController;
import com.mmyumu.nandr.entity.systems.AnimationSystem;
import com.mmyumu.nandr.entity.systems.CollisionSystem;
import com.mmyumu.nandr.entity.systems.LevelGenerationSystem;
import com.mmyumu.nandr.entity.systems.PhysicsDebugSystem;
import com.mmyumu.nandr.entity.systems.PhysicsSystem;
import com.mmyumu.nandr.entity.systems.PlayerControlSystem;
import com.mmyumu.nandr.entity.systems.RenderingSystem;
import com.mmyumu.nandr.entity.systems.WallSystem;
import com.mmyumu.nandr.entity.systems.WaterFloorSystem;


public class MainScreen implements Screen {
    private Entity player;
    private NAndRGame parent;
    private OrthographicCamera cam;
    private KeyboardController controller;
    private SpriteBatch sb;
    private PooledEngine engine;
    private LevelFactory lvlFactory;

    private Sound ping;
    private Sound boing;
    private TextureAtlas atlas;


    /**
     * @param nAndRGame
     */
    public MainScreen(NAndRGame nAndRGame) {
        parent = nAndRGame;
        parent.assetManager.queueAddSounds();
        parent.assetManager.manager.finishLoading();
        atlas = parent.assetManager.manager.get("images/game.atlas", TextureAtlas.class);
        ping = parent.assetManager.manager.get("sounds/ping.wav", Sound.class);
        boing = parent.assetManager.manager.get("sounds/boing.wav", Sound.class);
        controller = new KeyboardController();
        engine = new PooledEngine();
        lvlFactory = new LevelFactory(engine, atlas.findRegion("player"));


        sb = new SpriteBatch();
        RenderingSystem renderingSystem = new RenderingSystem(sb);
        cam = renderingSystem.getCamera();
        sb.setProjectionMatrix(cam.combined);

        engine.addSystem(new AnimationSystem());
        engine.addSystem(new PhysicsSystem(lvlFactory.world));
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsDebugSystem(lvlFactory.world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller));
        player = lvlFactory.createPlayer(atlas.findRegion("player"), cam);
        engine.addSystem(new WallSystem(player));
        engine.addSystem(new WaterFloorSystem(player));
        engine.addSystem(new LevelGenerationSystem(lvlFactory));

        int floorWidth = (int) (40 * RenderingSystem.PPM);
        int floorHeight = (int) (1 * RenderingSystem.PPM);
        TextureRegion floorRegion = DFUtils.makeTextureRegion(floorWidth, floorHeight, "11331180");
        lvlFactory.createFloor(floorRegion);

        int wFloorWidth = (int) (40 * RenderingSystem.PPM);
        int wFloorHeight = (int) (10 * RenderingSystem.PPM);
        TextureRegion wFloorRegion = DFUtils.makeTextureRegion(wFloorWidth, wFloorHeight, "11113380");
        lvlFactory.createWaterFloor(wFloorRegion);


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
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);

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

}