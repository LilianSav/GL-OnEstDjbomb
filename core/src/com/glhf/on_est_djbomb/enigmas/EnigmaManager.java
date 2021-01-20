package com.glhf.on_est_djbomb.enigmas;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.dialogs.ClueDialog;
import com.glhf.on_est_djbomb.dialogs.SolutionDialog;

public class EnigmaManager extends Table{

	private final ArrayList<EnigmaSkeleton> enigmes;// liste des énigmes
	private EnigmaSkeleton enigmeCourante;// classe de l'énigme en cours d'utilisation
	private final boolean isHost;
	private ClueDialog clueDialog;// dialogue d'information affichant l'indice de l'énigme courante
	private SolutionDialog solutionDialog;// dialogue d'information affichant la solution de l'énigme courante
	private final OnEstDjbombGame game;
	private final Stage stage;
	private Texture enigmeImageTexture;// image affichée sur le Table
	private Label titreLabel;

	public EnigmaManager(boolean isHost , OnEstDjbombGame game , Stage stage) throws FileNotFoundException {
		// initialisation
		super();
		enigmes = new ArrayList<EnigmaSkeleton>();
		this.isHost=isHost;
		this.game=game;
		this.stage = stage;
		//ajout des enigmes
		EnigmaLabyrinth l1 = new EnigmaLabyrinth(isHost, "labyrinthe.txt");
		enigmes.add(l1);
		EnigmaFindThePath enigme1 = new EnigmaFindThePath(isHost);
		enigmes.add(enigme1);/*
		EnigmaFindTheImage enigme2 = new EnigmaFindTheImage(isHost);
		enigmes.add(enigme2);
		EnigmaPyramid enigme3 = new EnigmaPyramid(isHost);
		enigmes.add(enigme3);
		*/
		enigmeCourante=l1;

		// charger data des enigmes
		chargerTitre();
		chargerImage();
		chargerIndice();
		chargerSolution();
	}
	
	// Récupération et affichage de l'images du table depuis la classe d'énigme
	private void chargerImage() {
		if(enigmeCourante instanceof EnigmaLabyrinth){
			((EnigmaLabyrinth)enigmeCourante).load(isHost, this);
		}
		else
			{
			if(isHost) {
				enigmeImageTexture = enigmeCourante.getTextureTableHost();
			}else {
				enigmeImageTexture = enigmeCourante.getTextureTableGuest();
			}
			Image enigmeImageWidget = new Image(enigmeImageTexture);
			this.add(enigmeImageWidget);
		}

	}
	
	// Intanciation du dialogue d'indice
	private void chargerIndice() {
        clueDialog = new ClueDialog("Indice", game.skin);
        clueDialog.initContent();
	}
	
	// Intanciation du dialogue d'indice
	private void chargerSolution() {
        solutionDialog = new SolutionDialog("Solution", game.skin);
        solutionDialog.initContent();
	}
	
	// Intanciation du titre du table (composé du nom de l'énigme et d'une explication lorsque nécessaire)
	private void chargerTitre() {
		titreLabel = new Label(enigmeCourante.getTitreTable(),game.skin);
		this.add(titreLabel);
		this.row();
	}

	// passer à l'énigme suivante
	public void nextEnigme() {
		enigmeCourante=enigmes.get(enigmes.indexOf(enigmeCourante)+1);
		chargerTitre();
		chargerImage();
		chargerIndice();
		chargerSolution();
	}
	
	public int getSolution() {
		return enigmeCourante.getSolution();
	}
	
	// Affichage du dialogue d'informations
	public void getIndiceDialog() {
    	clueDialog.setClue(enigmeCourante.getIndice());// récupération du texte d'indice de l'éngime courante
        clueDialog.show(stage);
	}
	
	// Affichage du dialogue d'informations
	public void getSolutionDialog() {
    	solutionDialog.setSolution(enigmeCourante.getSolution()+"");// récupération du texte d'indice de l'éngime courante
    	solutionDialog.show(stage);
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

	// nombre de secondes minimum de l'énigme avant d'autoriser l'utilisateur à afficher l'indice
	public int getTpsBeforeIndice() {
		return enigmeCourante.getTpsBeforeIndice();
	}
	
	// nombre de secondes minimum de l'énigme avant d'autoriser l'utilisateur à afficher la solution
	public int getTpsBeforeSolution() {
		return enigmeCourante.getTpsBeforeSolution();
	}

	// informer l'utilisateur qu'il clique sur le bouton d'indice trop tôt
	public void getInstructionIndice() {
		clueDialog.setClue("Cherchez encore un peu !\nL'indice sera disponible plus tard si vous ne trouvez pas.");// récupération du texte d'indice de l'éngime courante
        clueDialog.show(stage);
	}
	
	// informer l'utilisateur qu'il clique sur le bouton d'indice trop tôt
	public void getInstructionSolution() {
		solutionDialog.setSolution("Cherchez encore un peu !\nLa solution sera disponible plus tard si vous ne trouvez pas.");// récupération du texte d'indice de l'éngime courante
		solutionDialog.show(stage);
	}
}
