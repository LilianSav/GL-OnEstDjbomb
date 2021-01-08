package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.networking.GameGuestSocket;
import com.glhf.on_est_djbomb.networking.GameHostSocket;
import com.glhf.on_est_djbomb.screens.LobbyScreen;

public class NewGameDialog extends Dialog {
    private final OnEstDjbombGame game;
    private final Stage gameStage;
    private TextField pseudoTextField;
    private TextField adresseTextField;

    public NewGameDialog(String title, OnEstDjbombGame game, Stage stage) {
        super(title, game.skin);
        this.game = game;
        gameStage = stage;
    }

    public void initContent() {
        // Section Content
        text("Nouvelle partie :");

        // Section button
        button("Heberger", 1L);
        button("Rejoindre", 2L);
        button("Retour", 3L);
    }

    @Override
    protected void result(Object object) {
        // Option "Hébergez"
        if (object.equals(1L)) {
            new Dialog("Heberger une partie", game.skin) {
                {
                    pseudoTextField = new TextField("Pseudo", game.skin);
                    getContentTable().add(pseudoTextField);

                    button("Retour", 1L);
                    button("Confirmer", 2L);
                }

                @Override
                protected void result(Object object) {
                    if (object.equals(2L)) {
                        // On observe si pseudoTextField n'est pas vide
                        if (!pseudoTextField.getText().isEmpty()) {
                            // Initialisation GameSocket
                            GameHostSocket gameSocket = new GameHostSocket(pseudoTextField.getText());

                            // Attente de connexion d'un client
                            new Thread(gameSocket::init).start();

                            // Changement d'écran
                            game.setGameSocket(gameSocket);
                            game.switchScreen(new LobbyScreen(game));
                        } else {
                            new Dialog("Pseudo invalide", game.skin) {
                                {
                                    getContentTable().add(new Label("Veuillez renseigner un pseudo", game.skin));
                                    button("Retour", 1L);
                                }
                            }.show(gameStage);
                        }
                    }
                }
            }.show(gameStage);
        }

        // Option "Rejoindre"
        else if (object.equals(2L)) {
            new Dialog("Partie à rejoindre", game.skin) {
                {
                    pseudoTextField = new TextField("Pseudo", game.skin);
                    adresseTextField = new TextField("IP:Port", game.skin);
                    getContentTable().add(pseudoTextField);
                    getContentTable().add(adresseTextField);

                    button("Retour", 1L);
                    button("Confirmer", 2L);
                }

                @Override
                protected void result(Object object) {
                    if (object.equals(2L)) {
                        // On observe si pseudoTextField et adresseTextField ne sont pas vides
                        if (!pseudoTextField.getText().isEmpty() && !adresseTextField.getText().isEmpty()) {
                            // Parse le résultat
                            String[] tokens = adresseTextField.getText().split(":");

                            // Initialisation GameSocket
                            GameGuestSocket gameSocket = new GameGuestSocket(pseudoTextField.getText());

                            // Connexion à l'host
                            gameSocket.init(tokens[0], Integer.parseInt(tokens[1]));

                            // Changement d'écran
                            game.setGameSocket(gameSocket);
                            game.switchScreen(new LobbyScreen(game));
                        } else {
                            new Dialog("Paramètres invalides", game.skin) {
                                {
                                    getContentTable().add(new Label("Le pseudo et/ou l'adresse ne sont pas renseignes", game.skin));
                                    button("Retour", 1L);
                                }
                            }.show(gameStage);
                        }
                    }
                }
            }.show(gameStage);
        }
    }
}
