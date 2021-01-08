package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class EnigmaPyramid extends EnigmaSkeleton {

    public EnigmaPyramid(boolean isHost) {
        super(isHost);
        setSolution(144);
        setIndice("C'est une question de produit...");
    }

    @Override
    public Texture getTextureTableHost() {
        return new Texture(Gdx.files.internal("assetEnigme/pyramid/pyramidHost.png"));
    }

    @Override
    public Texture getTextureTableGuest() {
        return new Texture(Gdx.files.internal("assetEnigme/pyramid/pyramidGuest.png"));
    }

}