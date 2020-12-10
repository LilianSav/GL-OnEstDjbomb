package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.*;

public class InformationsDialog extends Dialog {
    private final Skin skin;
    public InformationsDialog(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
    }

    public void initContent(){
        // Section Content
        Table informationsContentTable = getContentTable();

        informationsContentTable.add(new Label("Informations sur l'application", skin));
        informationsContentTable.row();

        // Section button
        button("Retour");
    }

    @Override
    protected void result(Object object) {

    }
}
