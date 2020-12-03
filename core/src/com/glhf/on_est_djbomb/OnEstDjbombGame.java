package com.glhf.on_est_djbomb;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.glhf.on_est_djbomb.screens.MainMenuScreen;

public class OnEstDjbombGame extends Game {
    public SpriteBatch batch;

    public void create() {
        batch = new SpriteBatch();

        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        // "super" is important here
        super.render();
    }

    public void dispose() {
        batch.dispose();

        this.getScreen().dispose();
    }
}