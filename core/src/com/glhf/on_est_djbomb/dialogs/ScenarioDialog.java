package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

public class ScenarioDialog extends Dialog {
    private final Skin skin;
    private final StringBuilder gameConfig;
    private TextField textScenario;

    public ScenarioDialog(String title, Skin skin, StringBuilder gameConfig) {
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

        informationsContentTable.add(label).align(Align.center).pad(10, 30, 10, 30);
        informationsContentTable.row();

        textScenario = new TextField(gameConfig.toString(), skin, "title");
        informationsContentTable.add(textScenario).align(Align.center).width(450).pad(10, 30, 10, 30);

        /* Section Bouton */
        // Ajout du bouton retour dans la boîte de dialogue
        TextButton cancelBtn = new TextButton(" Retour ", skin, "title");
        cancelBtn.pad(10, 40, 10, 40);
        button(cancelBtn, 1L).pad(40);
        TextButton applyBtn = new TextButton("Appliquer", skin, "title");
        applyBtn.pad(10, 40, 10, 40);
        button(applyBtn, 2L).pad(40);
    }

    @Override
    protected void result(Object object) {
        if (object.equals(2L)) {
            gameConfig.setLength(0);
            gameConfig.append(textScenario.getText());
            Gdx.app.log("ScenarioDialogTest", gameConfig.toString());
        }
    }
}
