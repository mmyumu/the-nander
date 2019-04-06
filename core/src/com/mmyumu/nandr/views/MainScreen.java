package com.mmyumu.nandr.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.mmyumu.nandr.Model;
import com.mmyumu.nandr.NAndRGame;
import com.mmyumu.nandr.controller.KeyboardController;

public class MainScreen implements Screen {
    private final NAndRGame parent;
    private final Model model;
    private final OrthographicCamera cam;
    private final Box2DDebugRenderer debugRenderer;
    private final KeyboardController controller;
    private final SpriteBatch spriteBatch;
    private final TextureAtlas atlas;
    private final TextureAtlas.AtlasRegion playerTex;

    public MainScreen(NAndRGame nAndRGame) {
        parent = nAndRGame;
        cam = new OrthographicCamera(32, 24);
        controller = new KeyboardController();
        OrthographicCamera camera = new OrthographicCamera(32, 24);
        model = new Model(controller, camera, parent.assetManager);
        debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);

        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(cam.combined);

        atlas = parent.assetManager.manager.get("images/game.atlas", TextureAtlas.class);
        playerTex = atlas.findRegion("player");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        model.logicStep(delta);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(model.world, cam.combined);

        spriteBatch.begin();
        spriteBatch.draw(playerTex, model.player.getPosition().x - 1, model.player.getPosition().y - 1, 2, 2);
        spriteBatch.end();
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
}