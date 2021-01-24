package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EnigmaPyramid extends EnigmaSkeleton {
    Texture enigmeTexture;

    public EnigmaPyramid(boolean isHost) {
        super(isHost);
        setSolution(144);
        setIndice("C'est une question de produit.\n"
        		+ "223=12");
        setNom("Le mystère de la pyramide");
        //setTitreTable(getNom()+"\n");
        setTpsBeforeIndice(40);
        setTpsBeforeSolution(120);
    }

    @Override
    public void load(Table enigmaManagerTable){
        // Chargement du titre
        enigmaManagerTable.add(new Label(getTitreTable(), enigmaManagerTable.getSkin()));
        enigmaManagerTable.row();

        // Chargement de la texture
        Image enigmeImageTexture;
        if (this.isHost()){
            enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/pyramid/pyramidHost.png"));
        } else {
            enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/pyramid/pyramidGuest.png"));
        }
        enigmeImageTexture = new Image(enigmeTexture);
        enigmaManagerTable.add(enigmeImageTexture);
    }

    @Override
    public void unload() {
        enigmeTexture.dispose();
    }

}