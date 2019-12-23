package com.mmyumu.nander.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mmyumu.nander.utils.Direction;

public class NanderAssetManager {
    public final AssetManager manager = new AssetManager();

    public final String gameImages = "images/game.atlas";
    public final String loadingImages = "images/loading.atlas";

    public final String onScreenPad = "onscreencontrols/shadedDark/shadedDark09.png";

    public final String boingSound = "sounds/boing.wav";
    public final String pingSound = "sounds/ping.wav";

    public final String playingSong = "music/Noir - Salva Nos.mp3";

    public final String skin = "skin/glassy-ui.json";

    public final String smokeEffect = "particles/smoke.pe";
    public final String waterEffect = "particles/water.pe";
    public final String fireEffect = "particles/fire.pe";
    public final String trailEffect = "particles/trail.pe";

    public NanderAssetManager() {
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    public void queueAddParticleEffects() {
        ParticleEffectLoader.ParticleEffectParameter pep = new ParticleEffectLoader.ParticleEffectParameter();
        pep.atlasFile = "images/game.atlas";
        manager.load(smokeEffect, ParticleEffect.class, pep);
        manager.load(waterEffect, ParticleEffect.class, pep);
        manager.load(fireEffect, ParticleEffect.class, pep);
        manager.load(trailEffect, ParticleEffect.class, pep);
    }

    public void queueAddImages() {
        manager.load(gameImages, TextureAtlas.class);
    }

    public void queueAddLoadingImages() {
        manager.load(loadingImages, TextureAtlas.class);
        manager.load(onScreenPad, Texture.class);

        for (Direction direction : Direction.values()) {
            manager.load("images/character30_" + direction.name() + ".png", Texture.class);
            manager.load("images/character30_" + direction.name() + "_fly.png", Texture.class);
        }

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

    public void queueAddMaps() {
        manager.load("maps/test2-4.tmx", TiledMap.class);
        manager.load("maps/test2-3.tmx", TiledMap.class);
    }
}
