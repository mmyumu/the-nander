package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;

import java.util.ArrayList;

public class TiledMapComponent implements Component {
    private OrthoCachedTiledMapRenderer mapRenderer;
    private int[] backgroundLayers;
    private int[] foregroundLayers;

    public void setMapRenderer(OrthoCachedTiledMapRenderer mapRenderer) {
        this.mapRenderer = mapRenderer;
    }

    public OrthoCachedTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }

    public int[] getBackgroundLayers() {
        return backgroundLayers;
    }

    public void setBackgroundLayers(int[] backgroundLayers) {
        this.backgroundLayers = backgroundLayers;
    }

    public int[] getForegroundLayers() {
        return foregroundLayers;
    }

    public void setForegroundLayers(int[] foregroundLayers) {
        this.foregroundLayers = foregroundLayers;
    }
}
