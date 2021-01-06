package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.graphics.Texture;

public abstract class EnigmaSkeleton {

	private int solution;
	private String indice;
	private boolean isHost;
	
	public EnigmaSkeleton(boolean isHost) {
		super();
		setHost(isHost);
	}
	
	public abstract Texture getTextureTableHost();

	public abstract Texture getTextureTableGuest();
	
	public int getSolution() {
		return solution;
	}
	public void setSolution(int solution) {
		this.solution = solution;
	}
	public String getIndice() {
		return indice;
	}
	public void setIndice(String indice) {
		this.indice = indice;
	}
	public boolean isHost() {
		return isHost;
	}
	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}
	
}
