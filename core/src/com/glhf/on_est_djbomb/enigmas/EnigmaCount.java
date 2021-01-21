package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class EnigmaCount extends EnigmaSkeleton {

	public EnigmaCount(boolean isHost) {
		super(isHost);
		setSolution(81967);
		setIndice("Collaborez pour trouver le résultat de l'équipe 2 (code : ABCDE)");
		setNom("Le compte est bon");
		setTitreTable(getNom()+"\n");
		setTpsBeforeIndice(30);
		setTpsBeforeSolution(100);
	}

	@Override
	public Texture getTextureTableHost() {
		return new Texture(Gdx.files.internal("assetEnigme/count/countHost.png"));
	}

	@Override
	public Texture getTextureTableGuest() {
		return new Texture(Gdx.files.internal("assetEnigme/count/countGuest.png"));
	}

}