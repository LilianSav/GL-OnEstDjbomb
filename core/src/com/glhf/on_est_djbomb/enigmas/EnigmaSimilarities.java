package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EnigmaSimilarities extends EnigmaSkeleton{
	Texture enigmeTexture;

	public EnigmaSimilarities(boolean isHost) {
		super(isHost);
		setSolution(4123);
		setIndice("Les premiers chiffres du code sont 41");
		setNom("Retrouvez l'image en commun de chaque colonne, le code suit l'ordre ABCD");
		setTitreTable(getNom()+"\n");
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
			enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/similitude/similitudeHost.png"));
		} else {
			enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/similitude/similitudeGuest.png"));
		}
		enigmeImageTexture = new Image(enigmeTexture);
		enigmaManagerTable.add(enigmeImageTexture);
	}

	@Override
	public void unload() {
		enigmeTexture.dispose();
	}

}
