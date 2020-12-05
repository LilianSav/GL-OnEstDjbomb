package com.glhf.on_est_djbomb.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.glhf.on_est_djbomb.OnEstDjbombGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setTitle("On Est Djbomb");
		config.setWindowedMode(960, 540);
		config.setResizable(false);

		new Lwjgl3Application(new OnEstDjbombGame(), config);
	}
}