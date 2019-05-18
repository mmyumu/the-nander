package com.mmyumu.nander.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class KeyboardController implements InputProcessor {
    private final Inputs inputs;

    public KeyboardController(Inputs inputs) {
        this.inputs = inputs;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) {
            case Input.Keys.LEFT:
                inputs.setLeft(true);
                keyProcessed = true;
                break;
            case Input.Keys.RIGHT:
                inputs.setRight(true);
                keyProcessed = true;
                break;
            case Input.Keys.UP:
                inputs.setUp(true);
                keyProcessed = true;
                break;
            case Input.Keys.DOWN:
                inputs.setDown(true);
                keyProcessed = true;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) {
            case Input.Keys.LEFT:
                inputs.setLeft(false);
                keyProcessed = true;
                break;
            case Input.Keys.RIGHT:
                inputs.setRight(false);
                keyProcessed = true;
                break;
            case Input.Keys.UP:
                inputs.setUp(false);
                keyProcessed = true;
                break;
            case Input.Keys.DOWN:
                inputs.setDown(false);
                keyProcessed = true;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            inputs.setShoot(true);
            inputs.setShootPosition(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        isDragged = false;
        //System.out.println(button);
        if (button == 0) {
            inputs.setShoot(false);
            inputs.setShootPosition(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        isDragged = true;
        inputs.setShootPosition(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}