package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class EnigmaFindTheImage extends EnigmaSkeleton{

	public EnigmaFindTheImage(boolean isHost) {
		super(isHost);
		setSolution(3);
		setIndice("Observez les contours");
		setNom("Retrouver la bonne image");
		setTitreTable(getNom()+"\n");
		setTpsBeforeIndice(20);
        setTpsBeforeSolution(50);
	}

	@Override
	public Texture getTextureTableHost() {
		return new Texture(Gdx.files.internal("assetEnigme/image/imageHost.png"));
	}

	@Override
	public Texture getTextureTableGuest() {
		return new Texture(Gdx.files.internal("assetEnigme/image/imageGuest.png"));
	}

}
