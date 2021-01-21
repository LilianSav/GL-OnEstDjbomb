package com.glhf.on_est_djbomb.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
        	Label labelTitre = new Label("Game over !", game.skin);
            root.add(labelTitre).expandY();
            root.row();
        	Label tpsUtilise = new Label("Temps écoulé : la bombe a explosé !", game.skin);
            root.add(tpsUtilise).expandY();
            root.row();
        }else {
        	Label labelTitre = new Label("Félicitation !", game.skin);
            root.add(labelTitre).expandY();
            root.row();
        	Label congrat = new Label("En complétant la dernière énigme, vous avez désamorcé la bombe.", game.skin);
            root.add(congrat).expandY();
            root.row();
        	Label tpsUtilise = new Label("Temps utilisé : "+(tpsInit-tpsLeft)+" s", game.skin);
            root.add(tpsUtilise).expandY();
            root.row();
        }
        // Gestion meilleur temps
        String ancienMeilleurScore=game.prefs.getInteger("meilleurTpsUtilise")+" s";
        String ancienMeilleurScoreAuteurs = "Joueurs : "+game.prefs.getString("meilleurTpsUtilisePseudo1")+" et "+game.prefs.getString("meilleurTpsUtilisePseudo2");
        if(game.prefs.getInteger("meilleurTpsUtilise") >= tpsInit-tpsLeft){//new best score
        	//sauvegarde en local
        	if(isHost) {
        		game.prefs.putString("meilleurTpsUtilisePseudo1", game.getGameSocket().getIdentifiant());
        	}else {
        		game.prefs.putString("meilleurTpsUtilisePseudo2", game.getGameSocket().getIdentifiant());
        	}
        	game.prefs.putInteger("meilleurTpsUtilise", tpsInit-tpsLeft);
        	//affichage
        	Label meilleurTpsUtilise = new Label("Nouveau meilleur temps !", game.skin);
            root.add(meilleurTpsUtilise).expandY();
            root.row();
            Label ancienMeilleurTpsUtilise = new Label("Ancien meilleur temps : "+ancienMeilleurScore, game.skin);
            root.add(ancienMeilleurTpsUtilise).expandY();
            root.row();
        }else {
        	Label meilleurTpsUtilise = new Label("Meilleur temps : "+ancienMeilleurScore, game.skin);
            root.add(meilleurTpsUtilise).expandY();
            root.row();
        }
        Label ancienMeilleurTpsUtiliseJoueurs = new Label(ancienMeilleurScoreAuteurs, game.skin);
        root.add(ancienMeilleurTpsUtiliseJoueurs).expandY();
        root.row();
        Label tpsInitial = new Label("Temps initial : "+tpsInit+" s", game.skin);
        root.add(tpsInitial).expandY();
        root.row();
        Label tpsRestant = new Label("Temps restant : "+tpsLeft+" s", game.skin);
        root.add(tpsRestant).expandY();
        root.row();
        // todo fix
        //afficher tps pour chaque enigmes
        /*
        Label labelEnigmes = new Label("Temps utilisé par énigmes :", game.skin);
    	root.add(labelEnigmes).expandY();        
        for(EnigmaSkeleton enigme : enigmes) {
        	Label labelEnigme = new Label(enigme.getNom()+" : "+enigme.getTpsUtilise()+" s", game.skin);
        	root.add(labelEnigme).expandY();
            root.row();
        }
        */
        TextButton menuPrincipalButton = new TextButton("Menu principal", game.skin);
        root.add(menuPrincipalButton).expandY();
        
        // Gestionnaire d'évènements des bouttons
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

	@Override
	public void show() {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void render(float delta) {
		// Clear background
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        // Stage - act et draw
        stage.act();
        stage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
    public void dispose() {
        stage.dispose();
    }

}
