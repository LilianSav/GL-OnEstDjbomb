package com.glhf.on_est_djbomb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.glhf.on_est_djbomb.OnEstDjbombGame;

public class MainMenuScreen implements Screen {
    final OnEstDjbombGame game;
    private final Skin skin;
    private final Stage stage;

    public MainMenuScreen(final OnEstDjbombGame game_) {
        // Game
        game = game_;

        // Skin
        skin = new Skin(Gdx.files.internal("metalui/metal-ui.json"));

        // Stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Table
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        //Begin layout
        root.add(new Label("Guzny", skin)).expandY();
        root.row();

        // Boutons
        root.add(new TextButton("Nouvelle partie", skin)).expandY();
        root.row();
        root.add(new TextButton("Options", skin)).expandY();
        root.row();
        root.add(new TextButton("Informations", skin)).expandY();

        // Boutton "Quitter"
        root.row();
        TextButton quitButton = new TextButton("Quitter", skin);
        quitButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });
        root.add(quitButton).expandY();

    }

    @Override
    public void render(float delta) {
        // Clear background
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        // Stage - act and draw
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}