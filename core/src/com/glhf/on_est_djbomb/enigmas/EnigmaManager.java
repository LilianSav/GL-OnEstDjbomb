package com.glhf.on_est_djbomb.enigmas;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.dialogs.ClueDialog;

public class EnigmaManager extends Table{

	private final ArrayList<EnigmaSkeleton> enigmes;// liste des énigmes
	private EnigmaSkeleton enigmeCourante;// classe de l'énigme en cours d'utilisation
	private final boolean isHost;
	private ClueDialog clueDialog;// dialogue d'information affichant l'indice de l'énigme courante
	private final OnEstDjbombGame game;
	private final Stage stage;
	private Texture enigmeImageTexture;// image affichée sur le Table

	public EnigmaManager(boolean isHost , OnEstDjbombGame game , Stage stage) {
		// initialisation
		super();
		enigmes = new ArrayList<EnigmaSkeleton>();
		this.isHost=isHost;
		this.game=game;
		this.stage = stage;
		chargerImage();
		chargerIndice();
		//ajout des enigmes
		EnigmaFindThePath enigme1 = new EnigmaFindThePath(isHost);
		enigmes.add(enigme1);
		EnigmaFindTheImage enigme2 = new EnigmaFindTheImage(isHost);
		enigmes.add(enigme2);
		EnigmaPyramid enigme3 = new EnigmaPyramid(isHost);
		enigmes.add(enigme3);
		enigmeCourante=enigme1;
	}
	
	// Récupération de l'images du table depuis la classe d'énigme
	private void chargerImage() {
		if(isHost) {
			enigmeImageTexture = enigmeCourante.getTextureTableHost();
		}else {
			enigmeImageTexture = enigmeCourante.getTextureTableGuest();
		}
		Image enigmeImageWidget = new Image(enigmeImageTexture);
        this.add(enigmeImageWidget);
	}
	
	// Intanciation du dialogue d'indice
	private void chargerIndice() {
        clueDialog = new ClueDialog("Indice", game.skin);
        clueDialog.initContent();
	}

	// passer à l'énigme suivante
	public void nextEnigme() {
		enigmeCourante=enigmes.get(enigmes.indexOf(enigmeCourante)+1);
		chargerImage();
		chargerIndice();
	}
	
	public int getSolution() {
		return enigmeCourante.getSolution();//todo implémenter
	}
	
	// Affichage du dialogue d'informations
	public void getIndice() {
    	clueDialog.setClue(enigmeCourante.getIndice());// récupération du texte d'indice de l'éngime courante
        clueDialog.show(stage);
	}
	
	// est-ce qu'il reste des énigmes dans la liste d'énigme ?
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
	
	// sauvegarde du tps pour le menu de fin de partie
	public void setTpsUtilise(int tps) {
		enigmeCourante.setTpsUtilise(tps);
	}
}
