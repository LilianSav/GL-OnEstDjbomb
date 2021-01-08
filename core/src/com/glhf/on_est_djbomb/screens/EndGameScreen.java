package com.glhf.on_est_djbomb.screens;

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

public class EndGameScreen implements Screen{
	private OnEstDjbombGame game;
	private int tpsLeft;
	private int tpsInit;
	private final Stage stage;

	public EndGameScreen(OnEstDjbombGame game, int tpsLeft, int tpsInit) {
		super();
		this.game = game;
		this.tpsLeft = tpsLeft;
		this.tpsInit = tpsInit;
		
		// Instanciation du stage (Hiérarchie de nos acteurs)
        stage = new Stage(new ScreenViewport());
        
        // Liaison des Inputs au stage
        Gdx.input.setInputProcessor(stage);
        
        // Instanciation d'une table pour contenir nos Layouts (Énigmes, UI, Chat)
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        // Gestion meilleur temps
        String meilleurScore;
        if(game.prefs.getInteger("meilleurTpsUtilise") > tpsLeft){//new best score
        	game.prefs.putInteger("meilleurTpsUtilise", tpsInit-tpsLeft);
        	meilleurScore="Nouveau meilleur temps !";
        }else {
        	meilleurScore = "Meilleur temps utilise : "+game.prefs.getInteger("meilleurTpsUtilise")+" s";
        }
        
        // Création des labels
        Label labelTitre = new Label("Fin de partie", game.skin);
        Label tpsInitial = new Label("Temps initial : "+tpsInit+" s", game.skin);
        Label tpsUtilise = new Label("Temps utilise : "+(tpsInit-tpsLeft)+" s", game.skin);
        Label meilleurTpsUtilise = new Label(meilleurScore, game.skin);
        Label tpsRestant = new Label("Temps restant : "+tpsLeft+" s", game.skin);
        
        //afficher tps pour chaque enigmes

        // Création des boutons
        TextButton menuPrincipalButton = new TextButton("Menu principal", game.skin);

        // Ajout des acteurs à la Table
        root.add(labelTitre).expandY();
        root.row();
        root.add(tpsInitial).expandY();
        root.row();
        root.add(tpsUtilise).expandY();
        root.row();
        root.add(meilleurTpsUtilise).expandY();
        root.row();
        root.add(tpsRestant).expandY();
        root.row();
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
            	System.out.println("a");
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
