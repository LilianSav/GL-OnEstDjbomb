package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EnigmaSum extends EnigmaSkeleton {
	Texture enigmeTexture;

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
	public void load( Table enigmaManagerTable) {
		// Chargement du titre
		enigmaManagerTable.add(new Label(getTitreTable(), enigmaManagerTable.getSkin(), "title"));
		enigmaManagerTable.row();

		// Chargement de la texture
		Image enigmeImageTexture;
		if (this.isHost()) {
			enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/sum/sumHost.png"));
		} else {
			enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/sum/sumGuest.png"));
		}
		enigmeImageTexture = new Image(enigmeTexture);
		enigmaManagerTable.add(enigmeImageTexture);
	}

	@Override
	public void unload() {
		enigmeTexture.dispose();
	}
}