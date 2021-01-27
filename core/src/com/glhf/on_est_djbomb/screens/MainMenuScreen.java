package com.glhf.on_est_djbomb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
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
        //Label labelTitre = new Label("On est Djbomb", game.skin, "title");
        Image imgTitre = new Image(new Texture(Gdx.files.internal("images/onEstDjbombTitre.png")));
        Container<Image> ctnImgTitre = new Container<Image>(imgTitre);
        //userInterfaceTable.add(ctnImgTitre).padTop(100).colspan(2).width(300).height(65);

        // Création des boutons
        TextButton newGameButton = new TextButton("Nouvelle partie", game.skin, "title");
        TextButton optionsButton = new TextButton("Options", game.skin, "title");
        TextButton informationsButton = new TextButton("Informations", game.skin, "title");
        TextButton quitButton = new TextButton("Quitter", game.skin, "title");

        // Ajout des acteurs à la Table
        root.add(ctnImgTitre).padTop(30).width(730).height(300);
        root.row();
        root.add(setContainer(newGameButton)).expandY();
        root.row();
        root.add(setContainer(optionsButton)).expandY();
        root.row();
        root.add(setContainer(informationsButton)).expandY();
        root.row();
        root.add(setContainer(quitButton)).expandY();

        // Création des dialogues
        OptionsDialog optionsDialog = new OptionsDialog("Options", game, stage);
        optionsDialog.initContent();
        InformationsDialog informationsDialog = new InformationsDialog("Informations", game.skin);
        informationsDialog.initContent();
        NewGameDialog newGameDialog = new NewGameDialog("Nouvelle partie", game, stage);
        newGameDialog.initContent();

        // Gestionnaire d'évènements des boutons
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

//        for(Actor actor : root.getChildren()){
//            actor.setColor(Color.BLUE);
//        }
    }

    // setContainer retourne un TextButton dans son contenant fonction utilisée pour dimensionner le bouton
    public Container<TextButton> setContainer(TextButton textButton) {
        // Création du contenant
        Container<TextButton> ctnNewGameButton = new Container<TextButton>(textButton);

        // Paramétrage du TextBouton
        textButton.pad(10);
        textButton.getLabel().setFontScale(1.5f);

        // Paramétrage du contenant et ajout du TextBouton
        ctnNewGameButton.width(350);
        ctnNewGameButton.setOrigin(Align.center);
        ctnNewGameButton.center();
        ctnNewGameButton.setTransform(true);

        return ctnNewGameButton;
    }

    // setContainer retourne un Label dans son contenant fonction utilisée pour dimensionner le bouton
    public Container<Label> setContainer(Label label) {
        // Création du contenant
        Container<Label> ctnLabel = new Container<Label>(label);

        // Paramétrage du Label
        label.setFontScale(1.5f);

        // Paramétrage du contenant et ajout du TextBouton
        ctnLabel.width(350);
        ctnLabel.setOrigin(Align.center);
        ctnLabel.getActor().setAlignment(Align.center);

        return ctnLabel;
    }

    @Override
    public void render(float delta) {
        // Clear background
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 0.8f);
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