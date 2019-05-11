package com.mmyumu.nander.controller;

import com.badlogic.gdx.math.Vector2;

public class Inputs {
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;

    private boolean shoot;
    private Vector2 shootPosition = new Vector2();

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isShoot() {
        return shoot;
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

    public void setShootPosition(float x, float y) {
        shootPosition.x = x;
        shootPosition.y = y;
    }

    public Vector2 getShootPosition() {
        return shootPosition;
    }

    public void reset() {
        left = false;
        right = false;
        up = false;
        down = false;
        shoot = false;
    }
}
