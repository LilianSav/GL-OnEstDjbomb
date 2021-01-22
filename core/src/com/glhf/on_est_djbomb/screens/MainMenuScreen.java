package com.glhf.on_est_djbomb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.dialogs.*;

import javax.xml.soap.Text;

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
        Label labelTitre = new Label("On est djbomb", game.skin, "title");

        // Création des boutons
        TextButton newGameButton = new TextButton("Nouvelle partie", game.skin, "title");
        TextButton optionsButton = new TextButton("Options", game.skin, "title");
        TextButton informationsButton = new TextButton("Informations", game.skin, "title");
        TextButton quitButton = new TextButton("Quitter", game.skin, "title");


        // @modif test
        root.add(setContainer(labelTitre)).expandY();
        root.row();

        root.add(setContainer(newGameButton)).expandY();
        root.row();
        root.add(setContainer(optionsButton)).expandY();
        root.row();
        root.add(setContainer(informationsButton)).expandY();
        root.row();
        root.add(setContainer(quitButton)).expandY();

        /*
        // Ajout des acteurs à la Table
        root.add(labelTitre).expandY();
        root.row();
        root.add(newGameButton).expandY();
        root.row();
        root.add(optionsButton).expandY();
        root.row();
        root.add(informationsButton).expandY();
        root.row();
        root.add(quitButton).expandY();*/

        // Création des dialogues
        OptionsDialog optionsDialog = new OptionsDialog("Options", game);
        optionsDialog.initContent();
        InformationsDialog informationsDialog = new InformationsDialog("Informations", game.skin);
        informationsDialog.initContent();
        NewGameDialog newGameDialog = new NewGameDialog("Nouvelle partie", game, stage);
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

    public Container<TextButton> setContainer(TextButton textButton){
        Container<TextButton> ctnNewGameButton = new Container<TextButton>(textButton);
        //ctnNewGameButton.width(250);
        textButton.pad(10);
        textButton.getLabel().setFontScale(1.5f);
        //newGameButton.getLabel().setSize((float)(newGameButton.getLabel().getWidth()*1.5), (float)(newGameButton.getLabel().getHeight()*1.5));
        //newGameButton.setSize((float)(newGameButton.getWidth()*1.5), (float)(newGameButton.getHeight()*1.5));
        //newGameButton.setScale(1.5f);
        //newGameButton.setTransform(true);
        //ctnNewGameButton.setScale(2f);
        ctnNewGameButton.width(350);
        ctnNewGameButton.setOrigin(Align.center);
        ctnNewGameButton.center();
        ctnNewGameButton.setTransform(true);

        return ctnNewGameButton;
    }

    public Container<Label> setContainer(Label label){
        Container<Label> ctnLabel = new Container<Label>(label);
        //ctnNewGameButton.width(250);
        //label.pad(10);
        label.setFontScale(1.5f);
        //newGameButton.getLabel().setSize((float)(newGameButton.getLabel().getWidth()*1.5), (float)(newGameButton.getLabel().getHeight()*1.5));
        //newGameButton.setSize((float)(newGameButton.getWidth()*1.5), (float)(newGameButton.getHeight()*1.5));
        //newGameButton.setScale(1.5f);
        //newGameButton.setTransform(true);
        //ctnNewGameButton.setScale(2f);
        ctnLabel.width(350);
        ctnLabel.setOrigin(Align.center);
        ctnLabel.center();
        ctnLabel.setTransform(true);

        return ctnLabel;
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