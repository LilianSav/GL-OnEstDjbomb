package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class EnigmaSimilarities extends EnigmaSkeleton{

	public EnigmaSimilarities(boolean isHost) {
		super(isHost);
		setSolution(4123);
		setIndice("Chaque colonne possède une image en commun");
		setNom("Retrouvez les similitudes");
		setTitreTable(getNom()+"\n");
		setTpsBeforeIndice(20);
        setTpsBeforeSolution(50);
	}

	@Override
	public Texture getTextureTableHost() {
		return new Texture(Gdx.files.internal("assetEnigme/similitude/similitudeHost.png"));
	}

	@Override
	public Texture getTextureTableGuest() {
		return new Texture(Gdx.files.internal("assetEnigme/similitude/similitudeGuest.png"));
	}

}
