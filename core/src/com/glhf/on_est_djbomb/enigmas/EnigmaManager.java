package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.dialogs.ClueDialog;
import com.glhf.on_est_djbomb.dialogs.SolutionDialog;

import java.util.ArrayList;

public class EnigmaManager extends Table {

    private final ArrayList<EnigmaSkeleton> enigmes;// liste des énigmes
    private EnigmaSkeleton enigmeCourante;// classe de l'énigme en cours d'utilisation
    private final boolean isHost;
    private final ClueDialog clueDialog;// dialogue d'information affichant l'indice de l'énigme courante
    private final SolutionDialog solutionDialog;// dialogue d'information affichant la solution de l'énigme courante
    private final Stage stage;

    public EnigmaManager(boolean isHost, OnEstDjbombGame game, Stage stage) {
        // Initialisation
        super(game.skin);
        enigmes = new ArrayList<EnigmaSkeleton>();
        this.isHost = isHost;
        this.stage = stage;

        // Ajout des énigmes
        EnigmaFindThePath enigme1 = new EnigmaFindThePath(isHost);
        enigmes.add(enigme1);
        EnigmaSimilarities enigme2 = new EnigmaSimilarities(isHost);
        enigmes.add(enigme2);
        EnigmaLabyrinth enigme3 = new EnigmaLabyrinth(isHost, "labyrintheTutoBis.txt");
        enigmes.add(enigme3);
        EnigmaFindTheImage enigme4 = new EnigmaFindTheImage(isHost);
        enigmes.add(enigme4);
        EnigmaSum enigme5 = new EnigmaSum(isHost);
        enigmes.add(enigme5);
        EnigmaLabyrinth enigme6 = new EnigmaLabyrinth(isHost, "labyrintheIntermédiaire.txt");
        enigmes.add(enigme6);
        EnigmaPyramid enigme7 = new EnigmaPyramid(isHost);
        enigmes.add(enigme7);
        EnigmaCount enigme8 = new EnigmaCount(isHost);
        enigmes.add(enigme8);
        EnigmaLabyrinth enigme9 = new EnigmaLabyrinth(isHost, "labyrintheHard.txt");
        enigmes.add(enigme9);
        EnigmaCutWire enigme10 = new EnigmaCutWire(isHost);
        enigmes.add(enigme10);

        enigmeCourante = enigme1;

        // Création des dialogues d'indices et de solutions
        clueDialog = new ClueDialog("Indice", game.skin);
        clueDialog.initContent();
        solutionDialog = new SolutionDialog("Solution", game.skin);
        solutionDialog.initContent();

        // Chargement de la première énigme
        enigmeCourante.load(isHost, this);
    }

    public void nextEnigme() {
    	// On passe à l'énigme suivante
        enigmeCourante = enigmes.get(enigmes.indexOf(enigmeCourante) + 1);
        // On charge l'énigme courante
        enigmeCourante.load(isHost, this);
    }

    public int getSolution() {
        return enigmeCourante.getSolution();
    }

    // Affichage du dialogue d'informations
    public void getIndiceDialog() {
        // Si l'énigme courante est de type labyrinthe, on active l'aide
        if (enigmeCourante instanceof EnigmaLabyrinth) {
            ((EnigmaLabyrinth) enigmeCourante).chargerIndice();
        }
        // Récupération du texte d'indice de l'énigme courante, puis affichage
        clueDialog.setText(enigmeCourante.getIndice());
        clueDialog.show(stage);
    }

    // Affichage du dialogue d'informations
    public void getSolutionDialog() {
        solutionDialog.setText(String.valueOf(enigmeCourante.getSolution()));// récupération du texte d'indice de l'éngime courante
        solutionDialog.show(stage);
    }

    // est-ce qu'il reste des énigmes dans la liste d'énigme ?
    public boolean isOver() {
        // On vide la table
        this.clearChildren();

        // On décharge les ressources de l'énigme courante
        enigmeCourante.unload();

        // On renvoie si la partie est terminée
        if (enigmes.size() - (enigmes.indexOf(enigmeCourante)) == 1) {
            return true;
        } else {
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
        // Récupération du texte d'indice de l'énigme courante
        clueDialog.setText("Cherchez encore un peu !\nL'indice sera disponible plus tard si vous ne trouvez pas.");
        clueDialog.show(stage);
    }

    // informer l'utilisateur qu'il clique sur le bouton d'indice trop tôt
    public void getInstructionSolution() {
        // Récupération du texte de solution de l'énigme courante
        solutionDialog.setText("Cherchez encore un peu !\nLa solution sera disponible plus tard si vous ne trouvez pas.");
        solutionDialog.show(stage);
    }
}
