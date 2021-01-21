package com.glhf.on_est_djbomb.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.glhf.on_est_djbomb.OnEstDjbombGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Instanciation de la configuration Lwjgl3
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		// Modification de la configuration
		config.setTitle("On Est Djbomb");
		config.setWindowedMode(OnEstDjbombGame.GAME_WIDTH, OnEstDjbombGame.GAME_HEIGHT);
		config.setResizable(false);
		config.setWindowIcon(Files.FileType.Internal, "icons/glassIcon.png");

		// Lancement de l'application OnEstDjbombGame
		new Lwjgl3Application(new OnEstDjbombGame(), config);
	}
}