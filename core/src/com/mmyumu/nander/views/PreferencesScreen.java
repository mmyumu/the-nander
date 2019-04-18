package com.mmyumu.nander.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mmyumu.nander.NanderGame;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PreferencesScreen implements Screen {
    private final Stage stage;
    private final NanderGame game;

    public PreferencesScreen(NanderGame nanderGame) {
        game = nanderGame;
        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();
        table.setFillParent(true);
//        table.setDebug(true);
        stage.addActor(table);

        Label titleLabel = new Label("Preferences", skin);
        Label musicVolumeLabel = new Label("Music Volume", skin);
        Label soundVolumeLabel = new Label("Sound Volume", skin);
        Label musicOnOffLabel = new Label("Music", skin);
        Label soundOnOffLabel = new Label("Sound", skin);
        Label fpsOnOffLabel = new Label("Display FPS", skin);

        final Slider musicVolumeSlider = buildSlider(game.getPreferences()::getMusicVolume, game.getPreferences()::setMusicVolume, skin);
        final Slider soundVolumeSlider = buildSlider(game.getPreferences()::getSoundVolume, game.getPreferences()::setSoundVolume, skin);


        final CheckBox musicCheckbox = buildCheckBox(game.getPreferences()::isMusicEnabled, game.getPreferences()::setMusicEnabled, skin);
        final CheckBox soundEffectsCheckbox = buildCheckBox(game.getPreferences()::isSoundEffectsEnabled, game.getPreferences()::setSoundEffectsEnabled, skin);
        final CheckBox fpsCheckbox = buildCheckBox(game.getPreferences()::isFpsEnabled, game.getPreferences()::setFpsEnabled, skin);

        // return to main screen button
        final TextButton backButton = new TextButton("Back", skin, "small"); // the extra argument here "small" is used to set the button to the smaller version instead of the big default version
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(NanderGame.MENU);
            }
        });

        table.add(titleLabel).colspan(2);
        table.row().pad(10, 0, 0, 10);
        table.add(musicVolumeLabel).left();
        table.add(musicVolumeSlider);
        table.row().pad(10, 0, 0, 10);
        table.add(musicOnOffLabel).left();
        table.add(musicCheckbox);
        table.row().pad(10, 0, 0, 10);
        table.add(soundVolumeLabel).left();
        table.add(soundVolumeSlider);
        table.row().pad(10, 0, 0, 10);
        table.add(soundOnOffLabel).left();
        table.add(soundEffectsCheckbox);
        table.row().pad(10, 0, 0, 10);
        table.add(fpsOnOffLabel).left();
        table.add(fpsCheckbox);
        table.row().pad(10, 0, 0, 10);
        table.add(backButton).colspan(2);
    }

    private CheckBox buildCheckBox(Supplier<Boolean> getter, Consumer<Boolean> setter, Skin skin) {
        final CheckBox checkbox = new CheckBox(null, skin);
        checkbox.setChecked(getter.get());
        checkbox.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean enabled = checkbox.isChecked();
                setter.accept(enabled);
            }
        });
        return checkbox;
    }

    private Slider buildSlider(Supplier<Float> getter, Consumer<Float> setter, Skin skin) {
        final Slider slider = new Slider(0f, 1f, 0.1f, false, skin);
        slider.setValue(getter.get());
        slider.addListener(event -> {
            setter.accept(slider.getValue());
            return false;
        });
        return slider;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}