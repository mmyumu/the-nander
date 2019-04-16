package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TiledMapComponent implements Component {
    private OrthogonalTiledMapRenderer mapRenderer;


    public void setMapRenderer(OrthogonalTiledMapRenderer mapRenderer) {
        this.mapRenderer = mapRenderer;
    }

    public OrthogonalTiledMapRenderer getMapRenderer() {
        return mapRenderer;
    }
}
