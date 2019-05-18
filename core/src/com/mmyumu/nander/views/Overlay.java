package com.mmyumu.nander.views;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mmyumu.nander.actor.FPSActor;
import com.mmyumu.nander.actor.OnScreenPadActor;
import com.mmyumu.nander.controller.Inputs;
import com.mmyumu.nander.loader.NanderAssetManager;

public class Overlay extends Stage {
    public static final int VIEWPORT_WIDTH = 3200;
    public static final int VIEWPORT_HEIGHT = 1500;

    public Overlay(Inputs inputs, NanderAssetManager assetManager) {
        super(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        Skin skin = assetManager.manager.get("skin/glassy-ui.json", Skin.class);

        addActor(new OnScreenPadActor(inputs, assetManager));
        addActor(new FPSActor(skin));
    }
}
