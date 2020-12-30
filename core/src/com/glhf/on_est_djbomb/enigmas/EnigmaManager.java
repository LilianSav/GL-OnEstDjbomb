package com.glhf.on_est_djbomb.enigmas;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;

public class EnigmaManager{

	private ArrayList<EnigmaSkeleton> enigmes;
	private int enigmeCourante;
	private boolean isHost;
	
	public EnigmaManager(boolean isHost) {
		super();
		enigmes = new ArrayList<EnigmaSkeleton>();
		this.isHost=isHost;
		enigmeCourante=0;
		EnigmaFindThePath enigme1 = new EnigmaFindThePath(isHost);
		enigmes.add(enigme1);
	}

	public void nextEnigme() {
		enigmeCourante++;
	}
	
	public int getSolution() {
		return (enigmes.get(enigmeCourante)).getSolution();
	}
	
	public String getIndice() {
		return (enigmes.get(enigmeCourante)).getIndice();
	}

	public Texture getEnigmeTableHost() {
		return (enigmes.get(enigmeCourante)).getEnigmeTableHost();
	}

	public Texture getEnigmeTableGuest() {
		return (enigmes.get(enigmeCourante)).getEnigmeTableGuest();
	}

	

}
