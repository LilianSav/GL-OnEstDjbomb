package com.glhf.on_est_djbomb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.glhf.on_est_djbomb.OnEstDjbombGame;

public class LobbyScreen implements Screen {
    private final Stage stage;
    private boolean localReady = false;
    private boolean teammateReady = false;

    String statutReadyString;
    String localReadyString;
    String remoteReadyString;

    public LobbyScreen(final OnEstDjbombGame game) {
        // Instanciation du stage (Hiérarchie de nos acteurs)
        stage = new Stage(new ScreenViewport());
        // Liaison des Inputs au stage
        Gdx.input.setInputProcessor(stage);

        // Instanciation d'une table pour contenir nos acteurs
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Booléen indiquant si les joueurs sont prêts
        localReady = false;
        teammateReady = false;

        // Création des labels
        Label titreLabel = new Label("Lancement d'une partie", game.skin, "title");
        statutReadyString = "Statut : \n";
        localReadyString = game.getGameSocket().getIdentifiant() + " - Pas prêt\n";
        remoteReadyString = game.getGameSocket().getRemoteIdentifiant() + " - Pas prêt";
        Label readyLabel = new Label(statutReadyString + localReadyString + remoteReadyString, game.skin);

        // Avertissement de la connexion à l'hôte distant
        game.getGameSocket().sendMessage("STATE::NOTREADY");

        // Création des champs de texte
        Label motDePasseLabel = new Label("Mot de passe : ", game.skin);
        TextField infoSocketLabel = new TextField(game.getGameSocket().getInfoSocket(), game.skin);

        // Création des boutons
        TextButton retourButton = new TextButton("Retour", game.skin);
        TextButton commencerButton = new TextButton("Commencer", game.skin);
        commencerButton.setColor(Color.BLACK);
        TextButton pretButton = new TextButton("Prêt", game.skin);

        // Ajout des acteurs à la Table
        root.add(titreLabel).colspan(3).expand();
        root.row();
        root.add(motDePasseLabel).expand();
        root.add(infoSocketLabel).expand();
        root.add(readyLabel).expand();
        root.row();
        root.add(retourButton).expand();
        root.add(pretButton).expand();
        root.add(commencerButton).expand();

        // Gestionnaire d'évènements des bouttons
        retourButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // On vide le gestionnaire de listeners
                game.getGameSocket().clearListeners();
                // Fermeture des flux
                game.getGameSocket().close();
                // Changement d'écran pour revenir au menu principal
                game.switchScreen(new MainMenuScreen(game));
            }
        });
        pretButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // On inverse le statut du joueur local
                localReady = !localReady;
                // On indique le nouveau statut au joueur distant
                if (localReady) {
                    game.getGameSocket().sendMessage("STATE::READY");
                    localReadyString = game.getGameSocket().getIdentifiant() + " - Prêt\n";
                } else {
                    game.getGameSocket().sendMessage("STATE::NOTREADY");
                    localReadyString = game.getGameSocket().getIdentifiant() + " - Pas prêt\n";
                }
                // Si les deux joueurs sont prêts, on débloque le bouton "commencer"
                if (localReady && teammateReady) {
                    commencerButton.setColor(Color.WHITE);
                } else {
                    commencerButton.setColor(Color.BLACK);
                }
                // On modifie le statut prêt de l'équipe locale
                readyLabel.setText(statutReadyString + localReadyString + remoteReadyString);
            }
        });
        commencerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Si les deux joueurs sont prêts à commencer, on démarre la partie
                if(localReady && teammateReady){
                    // On indique le début de la partie à l'autre équipe
                    game.getGameSocket().sendMessage("STATE::START");
                    // On vide le gestionnaire de listeners
                    game.getGameSocket().clearListeners();
                    // Changement d'écran pour revenir au menu principal
                    game.switchScreen(new GameScreen(game));
                }
                // Sinon on indique à l'utilisateur que les deux équipes ne sont pas prêtes
                else {
                    new Dialog("Pseudo invalide", game.skin) {
                        {
                            getContentTable().add(new Label("L'une des deux équipes n'est pas prête", game.skin));
                            button("Ok", 1L);
                        }
                    }.show(stage);
                }
            }
        });

        // Gestion Game State
        game.getGameSocket().addListener(eventMessage -> {
            // Parse
            String[] tokens = eventMessage.split("::");

            if (tokens[0].equals("STATE")) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (tokens[1].equals("READY")) {
                            // On mémorise le statut du joueur distant
                            teammateReady = true;
                            // Si les deux joueurs sont prêts, on débloque le bouton "commencer"
                            if (localReady) {
                                commencerButton.setColor(Color.WHITE);
                            }
                            remoteReadyString = game.getGameSocket().getRemoteIdentifiant() + " - Prêt";
                            readyLabel.setText(statutReadyString + localReadyString + remoteReadyString);

                        } else if (tokens[1].equals("NOTREADY")) {
                            // On mémorise le statut du joueur distant
                            teammateReady = false;
                            // On bloque le bouton "commencer"
                            commencerButton.setColor(Color.BLACK);
                            // On modifie le statut de la partie
                            remoteReadyString = game.getGameSocket().getRemoteIdentifiant() + " - Pas prêt";
                            readyLabel.setText(statutReadyString + localReadyString + remoteReadyString);
                        } else if (tokens[1].equals("START")) {
                            // On vide le gestionnaire de listeners
                            game.getGameSocket().clearListeners();
                            // Changement d'écran pour revenir au menu principal
                            game.switchScreen(new GameScreen(game));
                        }
                    }
                });
            }
            // Flag non reconnu
            else {
                Gdx.app.log("SocketFlagError", "Le flag envoyé par le socket distant n'est pas reconnu");
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
