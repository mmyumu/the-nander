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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mmyumu.nandr.BodyFactory;
import com.mmyumu.nandr.NAndRContactListener;
import com.mmyumu.nandr.NAndRGame;
import com.mmyumu.nandr.components.B2dBodyComponent;
import com.mmyumu.nandr.components.CollisionComponent;
import com.mmyumu.nandr.components.PlayerComponent;
import com.mmyumu.nandr.components.StateComponent;
import com.mmyumu.nandr.components.TextureComponent;
import com.mmyumu.nandr.components.TransformComponent;
import com.mmyumu.nandr.components.TypeComponent;
import com.mmyumu.nandr.controller.KeyboardController;
import com.mmyumu.nandr.systems.AnimationSystem;
import com.mmyumu.nandr.systems.CollisionSystem;
import com.mmyumu.nandr.systems.PhysicsDebugSystem;
import com.mmyumu.nandr.systems.PhysicsSystem;
import com.mmyumu.nandr.systems.PlayerControlSystem;
import com.mmyumu.nandr.systems.RenderingSystem;

public class MainScreen implements Screen {
    private final NAndRGame parent;
    private final OrthographicCamera cam;
    private final KeyboardController controller;
    private final SpriteBatch spriteBatch;
    private final TextureAtlas atlas;
    private final PooledEngine engine;
    private final World world;
    private final BodyFactory bodyFactory;
    private final Sound ping;
    private final Sound boing;

    public MainScreen(NAndRGame nAndRGame) {
        parent = nAndRGame;
        controller = new KeyboardController();
        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new NAndRContactListener());
        bodyFactory = BodyFactory.getInstance(world);

        parent.assetManager.queueAddSounds();
        parent.assetManager.manager.finishLoading();
        atlas = parent.assetManager.manager.get("images/game.atlas", TextureAtlas.class);
        ping = parent.assetManager.manager.get("sounds/ping.wav", Sound.class);
        boing = parent.assetManager.manager.get("sounds/boing.wav", Sound.class);

        spriteBatch = new SpriteBatch();
        // Create our new rendering system
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        cam = renderingSystem.getCamera();
        spriteBatch.setProjectionMatrix(cam.combined);

        //create a pooled engine
        engine = new PooledEngine();

        // add all the relevant systems our engine should run
        engine.addSystem(new AnimationSystem());
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller));

        // create some game objects
        createPlayer();
        createPlatform(2, 2);
        createPlatform(2, 7);
        createPlatform(7, 2);
        createPlatform(7, 7);

        createFloor();

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
        // TODO Auto-generated method stub
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    private void createPlayer() {

        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);

        // create the data for the components and add them to the components
        b2dbody.body = bodyFactory.makeCirclePolyBody(10, 10, 1, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true);
        // set object position (x,y,z) z used to define draw order 0 first drawn
        position.position.set(10, 10, 0);
        texture.region = atlas.findRegion("player");
        type.type = TypeComponent.PLAYER;
        stateCom.set(StateComponent.STATE_NORMAL);
        b2dbody.body.setUserData(entity);

        // add the components to the entity
        entity.add(b2dbody);
        entity.add(position);
        entity.add(texture);
        entity.add(player);
        entity.add(colComp);
        entity.add(type);
        entity.add(stateCom);

        // add the entity to the engine
        engine.addEntity(entity);

    }

    private void createPlatform(float x, float y) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxPolyBody(x, y, 3, 0.2f, BodyFactory.STONE, BodyDef.BodyType.StaticBody);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = atlas.findRegion("player");
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;
        b2dbody.body.setUserData(entity);

        entity.add(b2dbody);
        entity.add(texture);
        entity.add(type);

        engine.addEntity(entity);

    }

    private void createFloor() {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxPolyBody(0, 0, 100, 0.2f, BodyFactory.STONE, BodyDef.BodyType.StaticBody);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = atlas.findRegion("player");
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;

        b2dbody.body.setUserData(entity);

        entity.add(b2dbody);
        entity.add(texture);
        entity.add(type);

        engine.addEntity(entity);
    }
}