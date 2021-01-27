package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EnigmaError extends EnigmaSkeleton {
    Texture enigmeTexture;

    public EnigmaError(boolean isHost) {
        super(isHost);
        setSolution(0);
        setIndice("Cette énigme n'est pas valable");
        setNom("Le manager d'énigme ne s'est pas initialisé correctement");
        setTitreTable(getNom()+"\n");
        setTpsBeforeIndice(5);
        setTpsBeforeSolution(10);
    }

    @Override
    public void load(Table enigmaManagerTable){
        // Chargement du titre
        enigmaManagerTable.add(new Label(getTitreTable(), enigmaManagerTable.getSkin(), "title"));
        enigmaManagerTable.row();

        // Chargement de la texture
        Image enigmeImageTexture;
        if (this.isHost()){
            enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/error/questionDice.png"));
        } else {
            enigmeTexture = new Texture(Gdx.files.internal("assetEnigme/error/questionDice.png"));
        }
        enigmeImageTexture = new Image(enigmeTexture);
        enigmaManagerTable.add(enigmeImageTexture);
    }

    @Override
    public void unload() {
        enigmeTexture.dispose();
    }

}