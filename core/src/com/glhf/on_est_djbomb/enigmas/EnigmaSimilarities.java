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
		setIndice("Retrouvez l'image en commun de chaque colonne dans l'ordre ABCD");
		setNom("Retrouvez les similitudes");
		setTitreTable(getNom()+"\n");
		setTpsBeforeIndice(20);
        setTpsBeforeSolution(50);
	}

	@Override
	public void load(boolean isHost, Table enigmaManagerTable) {
		// Chargement du titre
		enigmaManagerTable.add(new Label(getTitreTable(), enigmaManagerTable.getSkin()));
		enigmaManagerTable.row();

		// Chargement de la texture
		Image enigmeImageTexture;
		if (isHost) {
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
