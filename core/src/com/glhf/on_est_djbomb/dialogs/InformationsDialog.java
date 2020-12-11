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

        informationsContentTable.add(new Label("On est Djbomb est un escape game dans lequel la cooperation est l'element central du gameplay.\r\n" + 
        		"Dans ce jeu, une equipe separee en deux groupe doit travailler de concert pour desamorcer une bombe qui menace une des equipes dans une des \r\n"
        		+ "deux salles.\r\n" + 
        		"Au cours de la partie, les equipes seront confrontees à diverses enigmes qui pourront necessiter des interactions avec les elements de leur decor.\r\n" + 
        		"La communication est essentielle, donc au cours de la partie, les deux equipes auront la possibilite d'interagir à l'aide d'un chat ecrit et \r\n"
        		+ "d'un chat vocal.\r\n"
        		+ "\r\n" + 
        		"Enigmes :\r\n" + 
        		"Pour desamorcer la bombe, vous devrez resoudre plusieurs enigmes\r\n"
        		+ "\r\n"
        		+ "Credit musique : Hicham Chahidi - Plongee nocturne\r\n", skin));
        informationsContentTable.row();

        // Section button
        button("Retour");
    }

    @Override
    protected void result(Object object) {

    }
}
