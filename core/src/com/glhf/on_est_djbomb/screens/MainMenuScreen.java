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

public class MainMenuScreen implements Screen {
    private final OnEstDjbombGame game;
    private final Skin skin;
    private final Stage stage;

    public MainMenuScreen(final OnEstDjbombGame game_) {
        // On récupère la référence de notre objet OnEstDjbombGame
        game = game_;

        // Instanciation du skin
        skin = new Skin(Gdx.files.internal("metalui/metal-ui.json"));

        // Instanciation du stage (Hiérarchie de nos acteurs)
        stage = new Stage(new ScreenViewport());
        // Liaison des Inputs sur le stage
        Gdx.input.setInputProcessor(stage);

        // Instanciation d'une table pour contenir nos acteurs
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // On organise le layout
        // Affichage du titre
        root.add(new Label("Guzny", skin)).expandY();
        root.row();

        // Création des boutons
        TextButton newGameButton = new TextButton("Nouvelle partie", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton informationsButton = new TextButton("Informations", skin);
        TextButton quitButton = new TextButton("Quitter", skin);

        // Ajout des boutons à la Table
        root.add(newGameButton).expandY();
        root.row();
        root.add(optionsButton).expandY();
        root.row();
        root.add(informationsButton).expandY();
        root.row();
        root.add(quitButton).expandY();

        // Création des dialogues
        final Dialog optionsDialog = newOptionsDialog();

        // Gestionnaire d'évènements
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Screen nouvelle partie
                new Dialog("Nouvelle partie", skin).button("Retour").show(stage);
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
                new Dialog("Informations", skin).button("Retour").show(stage);
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

        // Stage - act and draw
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        skin.dispose();
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

    private Dialog newOptionsDialog() {
        return new Dialog("Options", skin) {
            {
                // Section Content
                text("Options");
                getContentTable().row();
                getContentTable().add(new Label("Musique : ", skin));
                getContentTable().add(new Slider(0f, 100, 1f, false, skin));
                getContentTable().row();
                getContentTable().add(new Label("Effets sonores : ", skin));
                getContentTable().add(new Slider(0f, 100, 1f, false, skin));
                getContentTable().row();
                getContentTable().add(new Label("Chat vocal : ", skin));
                getContentTable().add(new Slider(0f, 100, 1f, false, skin));

                // Section button
                button("Cancel");
                button("Apply");
            }

            @Override
            protected void result(Object object) {
                super.result(object);
            }
        };
    }
}