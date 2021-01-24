package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

public class ClueDialog extends Dialog {
    private final Skin skin;
    Table clueContentTable = getContentTable();

    public ClueDialog(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
    }

    public void initContent() {
        /* Section Contenu */
        // Paramétrage du titre
        getTitleLabel().setAlignment(Align.center);

        // Section Content
        clueContentTable.row();

        /* Section Bouton */
        // Ajout du bouton Retour dans la boîte de dialogue
        TextButton txtBtnReturn = new TextButton("  Retour  ",skin,"title");
        txtBtnReturn.pad(5,30,5,30);
        button(txtBtnReturn).pad(30);
    }

    public void setText(String clue) {
        clueContentTable.clearChildren();

        // Ajout de la solution dans la boîte de dialogue
        Label lblSolution = new Label(clue, skin, "title");
        clueContentTable.add(lblSolution).pad(30);
    }

    @Override
    protected void result(Object object) {

    }
}
