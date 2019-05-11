package com.mmyumu.nander.views;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mmyumu.nander.actor.FPSActor;
import com.mmyumu.nander.actor.OnScreenPadActor;
import com.mmyumu.nander.controller.Inputs;
import com.mmyumu.nander.loader.NanderAssetManager;

public class Overlay {
    public static final int VIEWPORT_WIDTH = 3200;
    public static final int VIEWPORT_HEIGHT = 1500;

    private Stage stage;

    public Overlay(Inputs inputs, NanderAssetManager assetManager) {
        Skin skin = assetManager.manager.get("skin/glassy-ui.json", Skin.class);

        Viewport testViewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        testViewport.getCamera().update();

        stage = new Stage(testViewport);
        stage.addActor(new OnScreenPadActor(inputs, assetManager));
        stage.addActor(new FPSActor(skin));
    }

    public Stage getStage() {
        return stage;
    }
}
