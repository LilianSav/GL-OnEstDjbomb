package com.glhf.on_est_djbomb;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.glhf.on_est_djbomb.screens.MainMenuScreen;

public class OnEstDjbombGame extends Game {
    public SpriteBatch batch;

    public void create() {
        // Instanciation du batch
        batch = new SpriteBatch();
        // Lancement du menu principal
        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        // Libère manuellement le batch
        batch.dispose();

        // Libère l'écran actif
        this.getScreen().dispose();
    }
}