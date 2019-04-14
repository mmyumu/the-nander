package com.mmyumu.nander;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.mmyumu.nander.loader.NanderAssetManager;
import com.mmyumu.nander.views.EndScreen;
import com.mmyumu.nander.views.LoadingScreen;
import com.mmyumu.nander.views.MainScreen;
import com.mmyumu.nander.views.MenuScreen;
import com.mmyumu.nander.views.PreferencesScreen;

public class NanderGame extends Game {
    private AppPreferences preferences;

    private LoadingScreen loadingScreen;
    private PreferencesScreen preferencesScreen;
    private MenuScreen menuScreen;
    private MainScreen mainScreen;
    private EndScreen endScreen;

    public NanderAssetManager assetManager = new NanderAssetManager();

    public final static int MENU = 0;
    public final static int PREFERENCES = 1;
    public final static int APPLICATION = 2;
    public final static int ENDGAME = 3;

    private Music playingSong;

    public int lastScore = 0;

    @Override
    public void create() {
        preferences = new AppPreferences();

        loadingScreen = new LoadingScreen(this);
        setScreen(loadingScreen);

        // tells our asset manger that we want to load the images set in loadImages method
        assetManager.queueAddMusic();
        // tells the asset manager to load the images and wait until finished loading.
        assetManager.manager.finishLoading();
        // loads the 2 sounds we use
        playingSong = assetManager.manager.get("music/Noir - Salva Nos.mp3", Music.class);

        playingSong.play();
    }

    public void changeScreen(int screen) {
        switch (screen) {
            case MENU:
                if (menuScreen == null) menuScreen = new MenuScreen(this);
                this.setScreen(menuScreen);
                break;
            case PREFERENCES:
                if (preferencesScreen == null) preferencesScreen = new PreferencesScreen(this);
                this.setScreen(preferencesScreen);
                break;
            case APPLICATION:
                if (mainScreen == null) {
                    mainScreen = new MainScreen(this);
                } else {
                    mainScreen.resetWorld();
                }
                this.setScreen(mainScreen);
                break;
            case ENDGAME:
                if (endScreen == null) endScreen = new EndScreen(this);
                this.setScreen(endScreen);
                break;
        }
    }

    public AppPreferences getPreferences() {
        return this.preferences;
    }

    @Override
    public void dispose() {
        playingSong.dispose();
        assetManager.manager.dispose();
    }
}
