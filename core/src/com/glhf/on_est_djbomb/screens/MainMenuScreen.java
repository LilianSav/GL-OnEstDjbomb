package com.glhf.on_est_djbomb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.dialogs.*;

public class MainMenuScreen implements Screen {
    private final Stage stage;

    public MainMenuScreen(final OnEstDjbombGame game) {
        // Instanciation du stage (Hiérarchie de nos acteurs)
        stage = new Stage(new ScreenViewport());
        // Liaison des Inputs au stage
        Gdx.input.setInputProcessor(stage);

        // Instanciation d'une table pour contenir nos acteurs
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Création des labels
        Label labelTitre = new Label("Guzny", game.skin);

        // Création des boutons
        TextButton newGameButton = new TextButton("Nouvelle partie", game.skin);
        TextButton optionsButton = new TextButton("Options", game.skin);
        TextButton informationsButton = new TextButton("Informations", game.skin);
        TextButton quitButton = new TextButton("Quitter", game.skin);

        // Ajout des acteurs à la Table
        root.add(labelTitre).expandY();
        root.row();
        root.add(newGameButton).expandY();
        root.row();
        root.add(optionsButton).expandY();
        root.row();
        root.add(informationsButton).expandY();
        root.row();
        root.add(quitButton).expandY();

        // Création des dialogues
        final OptionsDialog optionsDialog = new OptionsDialog("Options", game.skin);
        optionsDialog.initContent();
        final InformationsDialog informationsDialog = new InformationsDialog("Informations", game.skin);
        informationsDialog.initContent();
        final NewGameDialog newGameDialog = new NewGameDialog("Nouvelle partie", game, stage);
        newGameDialog.initContent();

        // Gestionnaire d'évènements des bouttons
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Affichage du dialogue pour une nouvelle partie
                newGameDialog.show(stage);
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Affichage du dialogue d'options
                optionsDialog.show(stage);
            }
        });

        informationsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Affichage du dialogue d'informations
                informationsDialog.show(stage);
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Quitte l'application
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        // Clear background
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        // Stage - act et draw
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
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
}