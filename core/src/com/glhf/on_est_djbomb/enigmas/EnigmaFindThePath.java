package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EnigmaFindThePath extends EnigmaSkeleton {
    Texture enigmeTexture;

    public EnigmaFindThePath(boolean isHost) {
        super(isHost);
        setSolution(2865);
        setIndice("Les premiers chiffres du code sont 28");
        setNom("Retrouvez le chemin");
        setTitreTable(getNom() + "\n");
        setTpsBeforeIndice(20);
        setTpsBeforeSolution(50);
    }

    @Override
    public void load( Table enigmaManagerTable) {
        // Chargement du titre
        enigmaManagerTable.add(new Label(getTitreTable(), enigmaManagerTable.getSkin(), "title"));
        enigmaManagerTable.row();

        // Chargement de la texture
        Image enigmeImageTexture;
        if (this.isHost()) {
            enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/trajet/trajetHost.png"));
        } else {
            enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/trajet/trajetGuest.png"));
        }
        enigmeImageTexture = new Image(enigmeTexture);
        enigmaManagerTable.add(enigmeImageTexture);
    }

    @Override
    public void unload() {
        enigmeTexture.dispose();
    }
}
