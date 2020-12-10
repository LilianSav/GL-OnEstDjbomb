package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.networking.GameGuestSocket;
import com.glhf.on_est_djbomb.networking.GameHostSocket;
import com.glhf.on_est_djbomb.networking.GameSocket;
import com.glhf.on_est_djbomb.screens.LobbyScreen;

public class NewGameDialog extends Dialog {
    private final OnEstDjbombGame game;
    private final Stage stage;

    public NewGameDialog(String title, OnEstDjbombGame game, Stage stage) {
        super(title, game.skin);
        this.game = game;
        this.stage = stage;
    }

    public void initContent() {
        // Section Content
        text("Nouvelle partie :");

        // Section button
        button("Hebergez", 1L);
        button("Rejoindre", 2L);
        button("Retour", 3L);
    }

    @Override
    protected void result(Object object) {
        // Option "Hébergez"
        if (object.equals(1L)) {
            // Initialisation GameSocket
            GameHostSocket gameSocket = new GameHostSocket("Host");

            // Attente de connexion d'un client
            new Thread(gameSocket::init).start();

            // Changement d'écran
            game.setGameSocket(gameSocket);
            game.switchScreen(new LobbyScreen(game));
        }
        // Option "Rejoindre"
        else if (object.equals(2L)) {
            new Dialog("Partie à rejoindre", game.skin) {
                {
                    TextField adresseTextField = new TextField("IP:Port", game.skin);
                    getContentTable().add(adresseTextField);

                    button("Retour", 1L);
                    button("Confirmer", adresseTextField);
                }

                @Override
                protected void result(Object object) {
                    if (!object.equals(1L) && object.getClass().getSimpleName().equals("TextField") ) {
                        // Parse le résultat
                        TextField textFieldAddress = (TextField) object;
                        String[] tokens = textFieldAddress.getText().split(":");

                        // Initialisation GameSocket
                        GameGuestSocket gameSocket = new GameGuestSocket("Guest");

                        // Connexion à l'host
                        gameSocket.init(tokens[0], Integer.parseInt(tokens[1]));

                        // Changement d'écran
                        game.setGameSocket(gameSocket);
                        game.switchScreen(new LobbyScreen(game));
                    }
                }
            }.show(this.stage);
        }
    }
}
