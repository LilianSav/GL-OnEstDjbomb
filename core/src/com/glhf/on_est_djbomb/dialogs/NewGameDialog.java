package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
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
        if (object.equals(1L)) {
            game.switchScreen(new LobbyScreen(game));
        } else if (object.equals(2L)) {
            new Dialog("Partie à rejoindre", game.skin) {
                {
                    TextField adresseTextField = new TextField("Adresse IP", game.skin);
                    getContentTable().add(adresseTextField);

                    button("Retour", 1L);
                    button("Confirmer", adresseTextField.getText());
                }

                @Override
                protected void result(Object object) {
                    if(!object.equals(1L)){
                        System.out.println(object);
                    }
                }
            }.show(this.stage);
        }
    }
}
