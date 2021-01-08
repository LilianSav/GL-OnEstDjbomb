package com.glhf.on_est_djbomb.enigmas;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.dialogs.ClueDialog;

public class EnigmaManager extends Table{

	private final ArrayList<EnigmaSkeleton> enigmes;
	private EnigmaSkeleton enigmeCourante;
	private final boolean isHost;
	private ClueDialog clueDialog;
	private final OnEstDjbombGame game;
	private final Stage stage;
	private Texture enigmeImageTexture;
	
	public EnigmaManager(boolean isHost , OnEstDjbombGame game , Stage stage) {
		super();
		enigmes = new ArrayList<EnigmaSkeleton>();
		this.isHost=isHost;
		//ajout des enigmes
		EnigmaFindThePath enigme1 = new EnigmaFindThePath(isHost);
		enigmes.add(enigme1);
		EnigmaFindTheImage enigme2 = new EnigmaFindTheImage(isHost);
		enigmes.add(enigme2);
		EnigmaPyramid enigme3 = new EnigmaPyramid(isHost);
		enigmes.add(enigme3);
		enigmeCourante=enigme1;
		this.game=game;
		this.stage = stage;
		chargerImageEtIndice();
	}

	private void chargerImageEtIndice() {
		// Images du table
		if(isHost) {
			enigmeImageTexture = enigmeCourante.getTextureTableHost();
		}else {
			enigmeImageTexture = enigmeCourante.getTextureTableGuest();
		}
		Image enigmeImageWidget = new Image(enigmeImageTexture);
        this.add(enigmeImageWidget);
        
        // Intanciation du dialogue d'indice
        clueDialog = new ClueDialog("Indice", game.skin);
        clueDialog.initContent();
	}

	public void nextEnigme() {
		enigmeCourante=enigmes.get(enigmes.indexOf(enigmeCourante)+1);
		chargerImageEtIndice();
	}
	
	public int getSolution() {
		return enigmeCourante.getSolution();
	}
	
	public void getIndice() {
		// Affichage du dialogue d'informations
    	clueDialog.setClue(enigmeCourante.getIndice());
        clueDialog.show(stage);
	}
	
	public boolean isOver() {
		// Suppression de l'ancienne image
		enigmeImageTexture.dispose();
		this.clearChildren();
		if(enigmes.size()-(enigmes.indexOf(enigmeCourante)) == 1) {
			return true;
		}else {
			return false;
		}
	}

	public ArrayList<EnigmaSkeleton> getEnigmes() {
		return enigmes;
	}
	
	public void setTpsUtilise(int tps) {
		enigmeCourante.setTpsUtilise(tps);
	}
}
