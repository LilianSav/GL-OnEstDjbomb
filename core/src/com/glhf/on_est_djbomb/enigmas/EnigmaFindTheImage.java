package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EnigmaFindTheImage extends EnigmaSkeleton {
    Texture enigmeTexture;

    public EnigmaFindTheImage(boolean isHost) {
        super(isHost);
        setSolution(3);
        setIndice("Observez les contours");
        setNom("Retrouver la bonne image");
        setTitreTable(getNom() + "\n");
        setTpsBeforeIndice(20);
        setTpsBeforeSolution(50);
    }

    @Override
    public void load( Table enigmaManagerTable) {
        // Chargement du titre
        enigmaManagerTable.add(new Label(getTitreTable(), enigmaManagerTable.getSkin()));
        enigmaManagerTable.row();

        // Chargement de la texture
        Image enigmeImageTexture;
        if (this.isHost()) {
            enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/image/imageHost.png"));
        } else {
            enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/image/imageGuest.png"));
        }
        enigmeImageTexture = new Image(enigmeTexture);
        enigmaManagerTable.add(enigmeImageTexture);
    }

    @Override
    public void unload() {
        enigmeTexture.dispose();
    }
}
