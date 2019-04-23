package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TiledMapComponent implements Component {
    private OrthoCachedTiledMapRenderer mapRenderer;


    public void setMapRenderer(OrthoCachedTiledMapRenderer mapRenderer) {
        this.mapRenderer = mapRenderer;
    }

    public OrthoCachedTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }
}
