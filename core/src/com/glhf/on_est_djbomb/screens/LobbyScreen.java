package com.glhf.on_est_djbomb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.glhf.on_est_djbomb.OnEstDjbombGame;

public class LobbyScreen implements Screen {
    private final Stage stage;

    public LobbyScreen(final OnEstDjbombGame game) {
        // Instanciation du stage (Hiérarchie de nos acteurs)
        stage = new Stage(new ScreenViewport());
        // Liaison des Inputs au stage
        Gdx.input.setInputProcessor(stage);

        // Instanciation d'une table pour contenir nos acteurs
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Création des labels
        Label titreLabel = new Label("Lancement d'une partie", game.skin);
        TextField infoSocketLabel = new TextField(game.getGameSocket().getInfoSocket(), game.skin);

        // Création des boutons
        TextButton retourButton = new TextButton("Retour", game.skin);
        TextButton commencerButton = new TextButton("Commencer", game.skin);
        commencerButton.setDisabled(true);
        TextButton pretButton = new TextButton("Pret", game.skin);

        // Ajout des acteurs à la Table
        root.add(titreLabel).expandY().expandX();
        root.row();
        root.add(infoSocketLabel).expandY().expandX();
        root.row();
        root.add(retourButton).expandY().expandX();
        //root.add(pretButton).expandY().expandX();
        root.add(commencerButton).expandY().expandX();

        // Gestionnaire d'évènements des bouttons
        retourButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Fermeture des flux
                game.getGameSocket().close();
                // Changement d'écran pour revenir au menu principal
                game.switchScreen(new MainMenuScreen(game));
            }
        });
        commencerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Changement d'écran pour revenir au menu principal
                game.switchScreen(new GameScreen(game));
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
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
