package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

public class InformationsDialog extends Dialog {
    private Skin skin;
    public InformationsDialog(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
    }

    public void initContent(){
        // Section Content
        text("Informations");
        getContentTable().row();
        getContentTable().add(new Label("Quelques Informations ...", skin));

        // Section button
        button("Retour");
    }

    @Override
    protected void result(Object object) {

    }
}
