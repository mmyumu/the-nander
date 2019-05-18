package com.mmyumu.nander.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.mmyumu.nander.controller.Inputs;
import com.mmyumu.nander.loader.NanderAssetManager;

public class OnScreenPadActor extends Image {

//    private static final int X = 5;
//    private static final int Y = 5;

    private static final float WIDTH = 600;
    private static final float HEIGHT = 600;

    private final float originX;
    private final float originY;

    private Inputs inputs;

    public OnScreenPadActor(Inputs inputs, NanderAssetManager assetManager) {
        super(new TextureRegionDrawable(new TextureRegion(assetManager.manager.get("onscreencontrols/shadedDark/shadedDark09.png", Texture.class))), Scaling.fit, Align.center);
        this.inputs = inputs;

        setTouchable(Touchable.enabled);

        // Bounds is bigger than the actual texture
//        setBounds(0, 0, 30 + 2 * X, 30 + 2 * Y);
        setBounds(0, 0, WIDTH, HEIGHT);
        setDebug(true);

//        originX = 30 / 2 + X;
//        originY = 30 / 2 + Y;
        originX = WIDTH / 2;
        originY = HEIGHT / 2;

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setInputs(x, y);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setInputs(false, false, false, false);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                setInputs(x, y);
            }
        });
    }

    private void setInputs(float x, float y) {
        float deltaX = x - originX;
        float deltaY = y - originY;
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        if (angle < 0) {
            angle += 360;
        }

        if (angle > 22.5 && angle <= 67.5) {
            setInputs(true, false, false, true);
        } else if (angle > 67.5 && angle <= 112.5) {
            setInputs(true, false, false, false);
        } else if (angle > 112.5 && angle <= 157.5) {
            setInputs(true, false, true, false);
        } else if (angle > 157.5 && angle <= 202.5) {
            setInputs(false, false, true, false);
        } else if (angle > 202.5 && angle <= 247.5) {
            setInputs(false, true, true, false);
        } else if (angle > 247.5 && angle <= 292.5) {
            setInputs(false, true, false, false);
        } else if (angle > 292.5 && angle <= 337.5) {
            setInputs(false, true, false, true);
        } else {
            setInputs(false, false, false, true);
        }
    }

    private void setInputs(boolean up, boolean down, boolean left, boolean right) {
        inputs.setUp(up);
        inputs.setDown(down);
        inputs.setLeft(left);
        inputs.setRight(right);
    }
}
