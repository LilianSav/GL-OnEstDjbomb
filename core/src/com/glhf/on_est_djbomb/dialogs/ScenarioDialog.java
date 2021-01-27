package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

public class ScenarioDialog extends Dialog {
    private final Skin skin;
    private String gameConfig;
    private TextField textScenario;

    public ScenarioDialog(String title, Skin skin, String gameConfig) {
        super(title, skin);
        this.skin = skin;
        this.gameConfig = gameConfig;
    }

    //Procédure initContent, initialise le contenu de la boîte de dialogue d'information
    public void initContent() {
        Table informationsContentTable = getContentTable();

        /* Section Contenu */
        // Paramétrage du titre
        this.getTitleLabel().setAlignment(Align.center);

        // Ajout du texte dans la boîte de dialogue
        Label label = new Label("Veuillez inscrire le nom des énigmes à utiliser pour ce scénario", skin, "title");
        label.setFontScale(0.75f);
        textScenario = new TextField(gameConfig, skin);

        informationsContentTable.add(label).align(Align.center);
        informationsContentTable.row();
        informationsContentTable.add(textScenario).align(Align.center).width(450);

        /* Section Bouton */
        // Ajout du bouton retour dans la boîte de dialogue
        TextButton cancelBtn = new TextButton("Retour", skin, "title");
        cancelBtn.pad(15, 30, 15, 30);
        button(cancelBtn, 1L).pad(30);
        TextButton applyBtn = new TextButton("Appliquer", skin, "title");
        applyBtn.pad(15, 30, 15, 30);
        button(applyBtn, 2L).pad(30);
    }

    @Override
    protected void result(Object object) {
        if (object.equals(2L)) {
            gameConfig = textScenario.getText();
        }
    }
}
