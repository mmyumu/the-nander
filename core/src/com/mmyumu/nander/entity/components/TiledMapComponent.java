package com.mmyumu.nander.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;

import java.util.ArrayList;

public class TiledMapComponent implements Component {
    public OrthoCachedTiledMapRenderer mapRenderer;
    public int[] backgroundLayers;
    public int[] foregroundLayers;
}
