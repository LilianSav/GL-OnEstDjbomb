package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class EnigmaSum extends EnigmaSkeleton {

	public EnigmaSum(boolean isHost) {
		super(isHost);
		setSolution(1539);
		setIndice("Résolvez toutes les sommes dans l'ordre ABCD");
		setNom("Une somme de sommes");
		setTitreTable(getNom()+"\n");
		setTpsBeforeIndice(30);
		setTpsBeforeSolution(100);
	}

	@Override
	public Texture getTextureTableHost() {
		return new Texture(Gdx.files.internal("assetEnigme/sum/sumHost.png"));
	}

	@Override
	public Texture getTextureTableGuest() {
		return new Texture(Gdx.files.internal("assetEnigme/sum/sumGuest.png"));
	}

}