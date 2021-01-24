package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EnigmaCutWire extends EnigmaSkeleton {
    Texture enigmeTexture;

    public EnigmaCutWire(boolean isHost) {
        super(isHost);
        setSolution(4);
        setIndice("Etudiez chaque affirmation l'une après l'autre, les fils bleu et noir sont sécurisés");
        setNom("Désamorçage");
        setTitreTable(getNom() + "\n");
        setTpsBeforeIndice(30);
        setTpsBeforeSolution(100);
    }

    @Override
    public void load(boolean isHost, Table enigmaManagerTable) {
        // Chargement du titre
        enigmaManagerTable.add(new Label(getTitreTable(), enigmaManagerTable.getSkin()));
        enigmaManagerTable.row();

        // Chargement de la texture
        Image enigmeImageTexture;
        if (isHost) {
            enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/cutWire/cutWireHost.png"));
        } else {
            enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/cutWire/cutWireGuest.png"));
        }
        enigmeImageTexture = new Image(enigmeTexture);
        enigmaManagerTable.add(enigmeImageTexture);
    }

    @Override
    public void unload() {
        enigmeTexture.dispose();
    }

}