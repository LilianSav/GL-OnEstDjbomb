package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.*;

public class OptionsDialog extends Dialog {
    private final Skin skin;
    public OptionsDialog(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
    }

    public void initContent(){
        // Section Content
        Table informationsContentTable = getContentTable();

        informationsContentTable.add(new Label("Options", skin));
        informationsContentTable.row();
        informationsContentTable.add(new Label("Musique : ", skin));
        informationsContentTable.add(new Slider(0f, 100, 1f, false, skin));
        informationsContentTable.row();
        informationsContentTable.add(new Label("Effets sonores : ", skin));
        informationsContentTable.add(new Slider(0f, 100, 1f, false, skin));
        informationsContentTable.row();
        informationsContentTable.add(new Label("Chat vocal : ", skin));
        informationsContentTable.add(new Slider(0f, 100, 1f, false, skin));

        // Section button
        button("Cancel");
        button("Apply");
    }

    @Override
    protected void result(Object object) {

    }
}
