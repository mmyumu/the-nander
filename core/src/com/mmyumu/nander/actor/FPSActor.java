package com.mmyumu.nander.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mmyumu.nander.views.Overlay;


public class FPSActor extends Label {
    public FPSActor(Skin skin) {
        super("", skin);


        setFontScale(3f);
        setBounds(1, Overlay.VIEWPORT_HEIGHT - getMinHeight() - 1, getMinWidth(), getMinHeight());
        updateFPS();
    }

    @Override
    public void act(float delta) {
        updateFPS();
        super.act(delta);
    }

    private void updateFPS() {
        setText("FPS: " + Gdx.graphics.getFramesPerSecond() + " fps");
    }
}
