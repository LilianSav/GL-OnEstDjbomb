package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

public class OptionsDialog extends Dialog {
    private Skin skin;
    public OptionsDialog(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
    }

    public void initContent(){
        // Section Content
        text("Options");
        getContentTable().row();
        getContentTable().add(new Label("Musique : ", skin));
        getContentTable().add(new Slider(0f, 100, 1f, false, skin));
        getContentTable().row();
        getContentTable().add(new Label("Effets sonores : ", skin));
        getContentTable().add(new Slider(0f, 100, 1f, false, skin));
        getContentTable().row();
        getContentTable().add(new Label("Chat vocal : ", skin));
        getContentTable().add(new Slider(0f, 100, 1f, false, skin));

        // Section button
        button("Cancel");
        button("Apply");
    }

    @Override
    protected void result(Object object) {
        System.out.println(object);
    }
}
