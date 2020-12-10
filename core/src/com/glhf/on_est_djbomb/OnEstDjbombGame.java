package com.glhf.on_est_djbomb;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.glhf.on_est_djbomb.screens.MainMenuScreen;

public class OnEstDjbombGame extends Game {
    public static final int GAME_WIDTH = 960;
    public static final int GAME_HEIGHT = 540;

    public SpriteBatch batch;
    public Skin skin;

    public void create() {
        // Instanciation du batch
        batch = new SpriteBatch();
        // Instaciation du skin
        skin = new Skin(Gdx.files.internal("metalui/metal-ui.json"));
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

    // Remplace le screen actif et dispose du précédent
    public void switchScreen(Screen newScreen){
        Screen oldScreen = this.getScreen();
        this.setScreen(newScreen);
        oldScreen.dispose();

    }
}