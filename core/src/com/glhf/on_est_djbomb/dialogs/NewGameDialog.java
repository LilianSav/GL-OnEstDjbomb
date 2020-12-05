package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.screens.LobbyScreen;
import com.glhf.on_est_djbomb.screens.MainMenuScreen;

public class NewGameDialog extends Dialog {
    private final OnEstDjbombGame game;

    public NewGameDialog(String title, OnEstDjbombGame game) {
        super(title, game.skin);
        this.game = game;
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
            System.out.println("1");
            System.out.println("Changing screen !");
            game.switchScreen(new LobbyScreen(game));
        } else if (object.equals(2L)) {
            System.out.println("2");
        }

        // game.switchScreen(new MainMenuScreen(game));
    }
}
