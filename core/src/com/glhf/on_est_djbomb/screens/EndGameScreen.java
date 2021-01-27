package com.glhf.on_est_djbomb.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.enigmas.EnigmaSkeleton;

public class EndGameScreen implements Screen{
	private OnEstDjbombGame game;
	private int tpsLeft;
	private int tpsInit;
	private final Stage stage;
	private ArrayList<EnigmaSkeleton> enigmes;

	public EndGameScreen(OnEstDjbombGame game, int tpsLeft, int tpsInit, ArrayList<EnigmaSkeleton> enigmes, boolean isHost) {
		super();
		this.game = game;
		this.tpsLeft = tpsLeft;
		this.tpsInit = tpsInit;
		this.enigmes=enigmes;
		
		// Instanciation du stage (Hiérarchie de nos acteurs)
        stage = new Stage(new ScreenViewport());
        
        // Liaison des Inputs au stage
        Gdx.input.setInputProcessor(stage);
        
        // Instanciation d'une table pour contenir nos Layouts (Énigmes, UI, Chat)
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        // Création des labels et ajout des acteurs à la table
        if((tpsInit-tpsLeft)==0) {
        	Label labelTitre = new Label("Game over !", game.skin, "title");
            root.add(labelTitre).expandY().left();
            root.row();
        	Label tpsUtilise = new Label("Temps écoulé, la bombe a explosé !", game.skin, "title");
            root.add(tpsUtilise).expandY().left();
            root.row();
        }else {
        	Label labelTitre = new Label("Félicitation !", game.skin, "title");
            root.add(labelTitre).expandY();
            root.row();
        	Label congrat = new Label("Vous avez désamorcé la bombe !", game.skin, "title");
            root.add(congrat).expandY().left();
            root.row();
        	Label tpsUtilise = new Label("Temps utilisé - "+secToMinSec(tpsInit-tpsLeft), game.skin, "title");
            root.add(tpsUtilise).expandY().left().padTop(100);
            root.row();
        }
        // Gestion meilleur temps
        String ancienMeilleurScore=secToMinSec(game.prefs.getInteger("meilleurTpsUtilise"));
        //String ancienMeilleurScoreAuteurs = "Joueurs : "+game.prefs.getString("meilleurTpsUtilisePseudo1")+" et "+game.prefs.getString("meilleurTpsUtilisePseudo2"); todo fix
        if(game.prefs.getInteger("meilleurTpsUtilise") >= tpsInit-tpsLeft){//new best score
        	//sauvegarde en local
        	if(isHost) {
        		game.prefs.putString("meilleurTpsUtilisePseudo1", game.getGameSocket().getIdentifiant());
        	}else {
        		game.prefs.putString("meilleurTpsUtilisePseudo2", game.getGameSocket().getIdentifiant());
        	}
        	game.prefs.putInteger("meilleurTpsUtilise", tpsInit-tpsLeft);
        	//affichage
        	Label meilleurTpsUtilise = new Label("Nouveau meilleur temps !", game.skin, "title");
            root.add(meilleurTpsUtilise).expandY().left();
            root.row();
            Label ancienMeilleurTpsUtilise = new Label("Ancien meilleur temps - "+ancienMeilleurScore, game.skin, "title");
            root.add(ancienMeilleurTpsUtilise).expandY().left();
            root.row();
        }else {
        	Label meilleurTpsUtilise = new Label("Meilleur temps - "+ancienMeilleurScore, game.skin, "title");
            root.add(meilleurTpsUtilise).expandY().left();
            root.row();
        }
        //Label ancienMeilleurTpsUtiliseJoueurs = new Label(ancienMeilleurScoreAuteurs, game.skin, "title");
        //root.add(ancienMeilleurTpsUtiliseJoueurs).expandY();
        //root.row();
        Label tpsInitial = new Label("Temps initial - "+secToMinSec(tpsInit), game.skin, "title");
        root.add(tpsInitial).expandY().left();
        root.row();
        Label tpsRestant = new Label("Temps restant - "+secToMinSec(tpsLeft), game.skin, "title");
        root.add(tpsRestant).expandY().left().padBottom(50);
        root.row();
        //afficher tps pour chaque enigmes --> todo ajouter scroll pane
        /*
        Label labelEnigmes = new Label("Temps utilisé par énigmes :", game.skin, "title");
    	root.add(labelEnigmes).expandY();        
    	root.row();
        for(EnigmaSkeleton enigme : enigmes) {
        	Label labelEnigme = new Label(enigme.getNom()+" : "+secToMinSec(enigme.getTpsUtilise()), game.skin, "title");
        	root.add(labelEnigme).expandY();
            root.row();
        }*/

        // Création du contenant
        TextButton menuPrincipalButton = new TextButton("Menu principal", game.skin, "title");
        Container<TextButton> ctnmenuPrincipalButton = new Container<TextButton>(menuPrincipalButton);
        // Paramétrage du TextBouton
        menuPrincipalButton.pad(10);
        menuPrincipalButton.getLabel().setFontScale(1.5f);
        // Paramétrage du contenant et ajout du TextBouton
        ctnmenuPrincipalButton.width(350);
        ctnmenuPrincipalButton.setOrigin(Align.center);
        ctnmenuPrincipalButton.center();
        ctnmenuPrincipalButton.setTransform(true);

        root.add(menuPrincipalButton).expandY();
        
        // Gestionnaire d'évènements des boutons
        menuPrincipalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	// On vide le gestionnaire de listeners
                game.getGameSocket().clearListeners();
                // Fermeture des flux
                game.getGameSocket().close();
                // Changement d'écran pour revenir au menu principal
            	game.switchScreen(new MainMenuScreen(game));
            }
        });
	}
	
	public String secToMinSec(int sec) {//affichage des secondes en minutes et secondes
		int secondes=sec%60;
		int minutes=sec/60;
		return String.format("%d:%02d minutes",minutes, secondes);
	}
	
	@Override
	public void show() {
	
	}

	@Override
	public void render(float delta) {
		// Clear background
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 0.8f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        // Stage - act et draw
        stage.act();
        stage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
    public void dispose() {
        stage.dispose();
    }

}
