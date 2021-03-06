package com.glhf.on_est_djbomb.enigmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.dialogs.ClueDialog;
import com.glhf.on_est_djbomb.dialogs.SolutionDialog;
import com.glhf.on_est_djbomb.networking.GameSocket;

import java.io.File;
import java.util.ArrayList;

public class EnigmaManager extends Table {

    private final ArrayList<EnigmaSkeleton> enigmes;// liste des énigmes
    private EnigmaSkeleton enigmeCourante;// classe de l'énigme en cours d'utilisation
    private final boolean isHost;
    private final ClueDialog clueDialog;// dialogue d'information affichant l'indice de l'énigme courante
    private final SolutionDialog solutionDialog;// dialogue d'information affichant la solution de l'énigme courante
    private final Stage stage;
    private GameSocket socket;

    private ArrayList<EnigmaLabyrinth> labyrinths;

    public EnigmaManager(boolean isHost, OnEstDjbombGame game, Stage stage, String gameConfig) {
        // Initialisation
        super(game.skin);
        enigmes = new ArrayList<EnigmaSkeleton>();
        this.isHost = isHost;
        this.stage = stage;
        this.socket = game.getGameSocket();

        labyrinths = new ArrayList<>();

        // Ajout des énigmes
        initEnigmeArray(isHost, game, gameConfig);

        // Création des dialogues d'indices et de solutions
        clueDialog = new ClueDialog("Indice", game.skin);
        clueDialog.initContent();
        solutionDialog = new SolutionDialog("Solution", game.skin);
        solutionDialog.initContent();

        // Chargement de la première énigme
        enigmeCourante.load(this);

        // Gestion Message et Game State
        game.getGameSocket().addListener(eventMessage -> {
            String[] tokens = eventMessage.split("::");
            // FLAG LABYRINTH
            if (tokens[0].equals("LABYRINTH")) {
                labyrinths.get(Character.getNumericValue(tokens[1].charAt(0))).traiterMessage(tokens[2]);
            }
        });
    }

    private void initEnigmeArray(boolean isHost, OnEstDjbombGame game, String gameConfig) {
        // Initialisation des variables
        String[] rows = gameConfig.split("-");
        int labyrintheNumber = 0;
        boolean errorManager = false;

        // Création des énigmes
        for (String row : rows) {
            String[] tokens = row.split("::");
            switch (tokens[0]) {
                case "FindThePath":
                    enigmes.add(new EnigmaFindThePath(isHost));
                    break;
                case "Similarities":
                    enigmes.add(new EnigmaSimilarities(isHost));
                    break;
                case "Labyrinthe":
                    // On cherche le labyrinthe.txt affilié
                    String labyrintheName = "labyrinthe" + tokens[1] + ".txt";
                    FileHandle file = Gdx.files.internal("assetEnigme/labyrinthe/" + labyrintheName);
                    // Si le fichier existe bien, on peut initialiser l'énigme
                    if (file.exists()) {
                        EnigmaLabyrinth newLabyrinthEnigma = new EnigmaLabyrinth(isHost, labyrintheName, labyrinths.size(), game.getGameSocket());
                        enigmes.add(newLabyrinthEnigma);
                        labyrinths.add(newLabyrinthEnigma);
                    } else {
                        errorManager = true;
                    }
                    break;
                case "FindTheImage":
                    enigmes.add(new EnigmaFindTheImage(isHost));
                    break;
                case "Sum":
                    enigmes.add(new EnigmaSum(isHost));
                    break;
                case "Pyramid":
                    enigmes.add(new EnigmaPyramid(isHost));
                    break;
                case "Count":
                    enigmes.add(new EnigmaCount(isHost));
                    break;
                case "CutWire":
                    enigmes.add(new EnigmaCutWire(isHost));
                    break;
                default:
                    errorManager = true;
                    break;
            }

            // Gestion des erreurs
            if (errorManager) {
                enigmes.clear();
                labyrinths.clear();
                enigmes.add(new EnigmaError(isHost));
            }

            enigmeCourante = enigmes.get(0);
        }
    }

    public void nextEnigme() {
        // On passe à l'énigme suivante
        enigmeCourante = enigmes.get(enigmes.indexOf(enigmeCourante) + 1);
        // On charge l'énigme courante
        enigmeCourante.load(this);
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

    public void dispose() {
        for (EnigmaLabyrinth lab : labyrinths) {
            lab.freeLock();
        }
        this.clearChildren();
        enigmeCourante.unload();
        socket.clearListeners();
        socket.close();
    }
}
