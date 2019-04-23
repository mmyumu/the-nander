package com.mmyumu.nander.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mmyumu.nander.NanderGame;

public class LoadingScreen implements Screen {

    private final NanderGame parent;
    private final Stage stage;
    private TextureAtlas atlas;
    private TextureAtlas.AtlasRegion title;
    private TextureAtlas.AtlasRegion dash;

    public final int IMAGE = 0;
    public final int FONT = 1;
    public final int PARTY = 2;
    public final int SOUND = 3;
    public final int MUSIC = 4;
    public final int MAPS = 5;

    private int currentLoadingStage = 0;

    // timer for exiting loading screen
    public float countDown = 0.1f;
    private Animation<TextureAtlas.AtlasRegion> flameAnimation;
    private Table table;
    private Table loadingTable;
    private TextureAtlas.AtlasRegion background;
    private TextureAtlas.AtlasRegion copyright;


    class LoadingBarPart extends Actor {
        private final TextureAtlas.AtlasRegion image;
        private final Animation<TextureAtlas.AtlasRegion> flameAnimation;
        private float stateTime;
        private TextureAtlas.AtlasRegion currentFrame;

        public LoadingBarPart(TextureAtlas.AtlasRegion ar, Animation<TextureAtlas.AtlasRegion> an) {
            super();
            image = ar;
            flameAnimation = an;
            this.setWidth(30);
            this.setHeight(25);
            this.setVisible(false);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            batch.draw(image, getX(), getY(), 30, 30);
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            batch.draw(currentFrame, getX() - 5, getY(), 40, 40);
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            stateTime += delta; // Accumulate elapsed animation time
            currentFrame = flameAnimation.getKeyFrame(stateTime, true);
        }
    }

    public LoadingScreen(NanderGame nanderGame) {
        parent = nanderGame;
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
            if (currentLoadingStage <= 6) {
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
                case MAPS:
                    System.out.println("Loading maps....");
                    parent.assetManager.queueAddMaps();
                    break;
                case 6:
                    System.out.println("Finished");
                    break;
            }
            if (currentLoadingStage > 6) {
                countDown -= delta;
                currentLoadingStage = 6;
                if (countDown < 0) {
                    parent.changeScreen(NanderGame.MENU);
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
        background = atlas.findRegion("flamebackground");
        copyright = atlas.findRegion("copyright");
        flameAnimation = new Animation<>(0.07f, atlas.findRegions("flames/flames"), Animation.PlayMode.LOOP);
    }
}