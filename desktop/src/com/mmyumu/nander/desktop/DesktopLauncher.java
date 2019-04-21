package com.mmyumu.nander.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mmyumu.nander.NanderGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1140;
		config.height = 540;
		config.vSyncEnabled = false;
		config.backgroundFPS = 59;
		config.foregroundFPS = 59;
		new LwjglApplication(new NanderGame(), config);
	}
}
