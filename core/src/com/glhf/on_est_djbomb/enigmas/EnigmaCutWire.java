package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class EnigmaCutWire extends EnigmaSkeleton {

	public EnigmaCutWire(boolean isHost) {
		super(isHost);
		setSolution(4);
		setIndice("Etudiez chaque affirmation l'une après l'autre, les fils bleu et noir sont sécurisés");
		setNom("Désamorçage");
		setTitreTable(getNom()+"\n");
		setTpsBeforeIndice(30);
		setTpsBeforeSolution(100);
	}

	@Override
	public Texture getTextureTableHost() {
		return new Texture(Gdx.files.internal("assetEnigme/cutWire/cutWireHost.png"));
	}

	@Override
	public Texture getTextureTableGuest() {
		return new Texture(Gdx.files.internal("assetEnigme/cutWire/cutWireGuest.png"));
	}

}