package com.mmyumu.nandr.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mmyumu.nandr.NAndRGame;

public class LoadingScreen implements Screen {

    private final NAndRGame parent;
    private final Stage stage;
    private TextureAtlas atlas;
    private TextureAtlas.AtlasRegion title;
    private TextureAtlas.AtlasRegion dash;

    public final int IMAGE = 0;        // loading images
    public final int FONT = 1;        // loading fonts
    public final int PARTY = 2;        // loading particle effects
    public final int SOUND = 3;        // loading sounds
    public final int MUSIC = 4;        // loading music

    private int currentLoadingStage = 0;

    // timer for exiting loading screen
    public float countDown = 5f; // 5 seconds of waiting before menu screen
    private Animation<TextureAtlas.AtlasRegion> flameAnimation;
    private Table table;
    private Table loadingTable;
    private TextureAtlas.AtlasRegion background;
    private TextureAtlas.AtlasRegion copyright;

    public LoadingScreen(NAndRGame nAndRGame) {
        parent = nAndRGame;
        stage = new Stage(new ScreenViewport());

        loadAssets();
        // initiate queueing of images but don't start loading
        parent.assetManager.queueAddImages();
        System.out.println("Loading images....");
    }

    @Override
    public void show() {

        Image titleImage = new Image(title);
        Image copyrightImage = new Image(copyright);

        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        table.setBackground(new TiledDrawable(background));

        loadingTable = new Table();
        loadingTable.add(new LoadingBarPart(dash, flameAnimation));
        loadingTable.add(new LoadingBarPart(dash, flameAnimation));
        loadingTable.add(new LoadingBarPart(dash, flameAnimation));
        loadingTable.add(new LoadingBarPart(dash, flameAnimation));
        loadingTable.add(new LoadingBarPart(dash, flameAnimation));
        loadingTable.add(new LoadingBarPart(dash, flameAnimation));
        loadingTable.add(new LoadingBarPart(dash, flameAnimation));
        loadingTable.add(new LoadingBarPart(dash, flameAnimation));
        loadingTable.add(new LoadingBarPart(dash, flameAnimation));
        loadingTable.add(new LoadingBarPart(dash, flameAnimation));


        table.add(titleImage).align(Align.center).pad(10, 0, 0, 0).colspan(10);
        table.row(); // move to next row
        table.add(loadingTable).width(400);
        table.row();
        table.add(copyrightImage).align(Align.center).pad(200, 0, 0, 0).colspan(10);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (parent.assetManager.manager.update()) { // Load some, will return true if done loading
            currentLoadingStage += 1;
            if (currentLoadingStage <= 5) {
                loadingTable.getCells().get((currentLoadingStage - 1) * 2).getActor().setVisible(true);  // new
                loadingTable.getCells().get((currentLoadingStage - 1) * 2 + 1).getActor().setVisible(true);
            }
            switch (currentLoadingStage) {
                case FONT:
                    System.out.println("Loading fonts....");
                    parent.assetManager.queueAddFonts();
                    break;
                case PARTY:
                    System.out.println("Loading Particle Effects....");
                    parent.assetManager.queueAddParticleEffects();
                    break;
                case SOUND:
                    System.out.println("Loading Sounds....");
                    parent.assetManager.queueAddSounds();
                    break;
                case MUSIC:
                    System.out.println("Loading fonts....");
                    parent.assetManager.queueAddMusic();
                    break;
                case 5:
                    System.out.println("Finished");
                    break;
            }
            if (currentLoadingStage > 5) {
                countDown -= delta;
                currentLoadingStage = 5;
                if (countDown < 0) {
                    parent.changeScreen(NAndRGame.MENU);
                }
            }
        }

        stage.act();
        stage.draw();
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

    private void loadAssets() {
        // load loading images and wait until finished
        parent.assetManager.queueAddLoadingImages();
        parent.assetManager.manager.finishLoading();

        // get images used to display loading progress
        atlas = parent.assetManager.manager.get("images/loading.atlas");
        title = atlas.findRegion("staying-alight-logo");
        dash = atlas.findRegion("loading-dash");
        flameAnimation = new Animation<TextureAtlas.AtlasRegion>(0.07f, atlas.findRegions("flames/flames"), Animation.PlayMode.LOOP);

        background = atlas.findRegion("flamebackground");
        copyright = atlas.findRegion("copyright");
    }
}