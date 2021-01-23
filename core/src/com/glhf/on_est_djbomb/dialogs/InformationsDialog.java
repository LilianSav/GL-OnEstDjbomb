package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

public class InformationsDialog extends Dialog {
    private final Skin skin;

    public InformationsDialog(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
    }

    //Procédure initContent, initialise le contenu de la boîte de dialogue d'information
    public void initContent() {

        Table informationsContentTable = getContentTable();

        /** Section Contenu **/
        // Paramétrage du titre
        this.getTitleLabel().setAlignment(Align.center);

        // Ajout du texte dans la boîte de dialogue
        Label label = new Label("\"On est Djbomb\" est un escape game dans lequel la coopération est l'élément central du gameplay.\r\n\n" +
                "Dans ce jeu, une équipe séparée en deux groupes doit travailler de concert pour désamorcer une bombe qui\r\n" +
                "menace de détruire le bâtiment où ils se trouvent ! \r\n\n"+
                "Au cours de la partie, les équipes seront confrontées à diverses énigmes qui leur permettront de sauver leur vie.\r\n" +
                "La communication est essentielle, donc au cours de la partie, les deux équipes auront la possibilité d'interagir \r\n" +
                "à l'aide d'un chat écrit.\r\n" +
                "\r\n\n" +

                "Crédits\r\n\n" +
                "Musique : Hicham Chahidi - Plongée nocturne\r\n" +
                "Interface graphique : ray3k.wordpress.com\r\n" +
                "MOA : Équipe Guzny\r\n" +
                "MOE : Équipe Glhf\r\n", skin, "title");
        label.setFontScale(0.75f);

        informationsContentTable.add(label).pad(30);
        informationsContentTable.row();

        /** Section Bouton **/
        // Ajout du bouton retour dans la boîte de dialogue
        TextButton txtBtn = new TextButton("Retour",skin,"title");
        txtBtn.pad(15,30,15,30);
        button(txtBtn).pad(30);
    }

    @Override
    protected void result(Object object) {

    }
}
