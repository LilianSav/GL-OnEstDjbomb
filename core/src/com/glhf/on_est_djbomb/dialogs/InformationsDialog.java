package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

public class InformationsDialog extends Dialog {
    private final Skin skin;

    public InformationsDialog(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;
    }

    public void initContent() {
        // Section Content
        Table informationsContentTable = getContentTable();

        Label label = new Label("On est Djbomb est un escape game dans lequel la coopération est l'élément central du gameplay.\r\n" +
                "Dans ce jeu, une équipe separée en deux groupes doit travailler de concert pour désamorcer une bombe qui menace de détruire le batîment \r\n"+
        		"où ils se trouvent ! \r\n"+    
                "Au cours de la partie, les équipes seront confrontées à diverses énigmes qui leur permettront de sauver leur vie.\r\n" +
                "La communication est essentielle, donc au cours de la partie, les deux équipes auront la possibilité d'interagir à l'aide d'un chat écrit.\r\n" +
                "\r\n" +
                "Crédit musique : Hicham Chahidi - Plongée nocturne\r\n"
                + "\r\n"
                + "Ce logiciel est réalisé dans un but pédagogique dans le cadre du module génie logiciel enseigné à l'ENSSAT.\r\n"
                + "Développeurs : SAVONA Lilian, GARREAU Erwan, SERENA Jérémy, SEREE Valentin, MARTIN Baptiste\r\n"
                + "MOA : GUZZO Nicolas, TRAN Sovanny\r\n"
                + "Enseignants : MOREAU Aurélien, PICOGNA Alexandre", skin);

        label.setAlignment(Align.center);
        informationsContentTable.add(label);
        informationsContentTable.row();

        // Section button
        button("Retour");
    }

    @Override
    protected void result(Object object) {

    }
}
