package com.mmyumu.nandr.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class NAndRAssetManager {
    public final AssetManager manager = new AssetManager();

    public final String gameImages = "images/game.atlas";
    public final String loadingImages = "images/loading.atlas";

    public final String boingSound = "sounds/boing.wav";
    public final String pingSound = "sounds/ping.wav";

    public final String playingSong = "music/Noir - Salva Nos.mp3";

    public final String skin = "skin/glassy-ui.json";

    public void queueAddImages() {
        manager.load(gameImages, TextureAtlas.class);
    }

    // a small set of images used by the loading screen
    public void queueAddLoadingImages() {
        manager.load(loadingImages, TextureAtlas.class);
    }

    public void queueAddSounds() {
        manager.load(boingSound, Sound.class);
        manager.load(pingSound, Sound.class);
    }

    public void queueAddMusic() {
        manager.load(playingSong, Music.class);
    }

    public void queueAddSkin() {
        SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("skin/glassy-ui.atlas");
        manager.load(skin, Skin.class, params);
    }

    public void queueAddFonts() {
    }

    public void queueAddParticleEffects() {
    }
}
