package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class EnigmaFindThePath extends EnigmaSkeleton{
	
	public EnigmaFindThePath(boolean isHost) {
		super(isHost);
		setSolution(2865);
		setIndice("Suivez les fleches");
	}

	public Texture getEnigmeTableHost() {
	    return new Texture(Gdx.files.internal("assetEnigme/trajet/trajetHost.png"));
	}

	public Texture getEnigmeTableGuest() {
	    return new Texture(Gdx.files.internal("assetEnigme/trajet/trajetGuest.png"));
	}
}
