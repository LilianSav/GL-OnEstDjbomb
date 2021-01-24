package com.glhf.on_est_djbomb;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.glhf.on_est_djbomb.networking.GameSocket;
import com.glhf.on_est_djbomb.screens.MainMenuScreen;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;


public class OnEstDjbombGame extends Game {
    public static final int GAME_WIDTH = 1440;
    public static final int GAME_HEIGHT = 810;

    public SpriteBatch batch;
    public Skin skin;
    private GameSocket gameSocket;
    public Music music;
    public Preferences prefs;
    public ColorPicker colorPicker;

    public void create() {
        // Instanciation du batch
        batch = new SpriteBatch();

        // Instanciation du skin
        skin = new Skin(Gdx.files.internal("skincomposerui/skin-composer-ui.json"));

    	//instanciation préférences utilisateur
    	prefs = Gdx.app.getPreferences("My Preferences");

        // Dans le cas d'un premier lancement, on remplit les préférences
    	if(!prefs.contains("volumeMusique") || !prefs.contains("interfaceColorValue")) {
    		prefs.putFloat("volumeMusique", 100f);
    		prefs.putFloat("volumeEffetSonore", 100f);
    		prefs.putFloat("volumeChatVocal", 100f);
    		prefs.putString("pseudo", "Pseudo");
    		prefs.putInteger("meilleurTpsUtilise", 600);//volontairement eleve
    		prefs.putString("meilleurTpsUtilisePseudo1", "Bob");
    		prefs.putString("meilleurTpsUtilisePseudo2", "Dylan");
            prefs.putString("interfaceColorValue", "000000ff");
            prefs.putBoolean("interfaceColorChecked", true);
    	}

    	// Instanciation musique
        music=Gdx.audio.newMusic(Gdx.files.internal("audio/music_plongee_nocture.mp3"));
        music.play();
        music.setVolume(prefs.getFloat("volumeMusique")/100);
    	music.setLooping(true);

        // Création de VisUI pour les widgets avancés
        VisUI.load();
        // Initialisation du colorPicker, Ce composant lourd est ainsi initialisé une seule fois
        colorPicker = new ColorPicker(new ColorPickerAdapter() {
            @Override
            public void finished (Color newColor) {
                prefs.putString("interfaceColorValue", newColor.toString());
            }
        });
        colorPicker.setColor(Color.valueOf(prefs.getString("interfaceColorValue")));

        // Lancement du menu principal
        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        // Libère manuellement le batch
        batch.dispose();
        skin.dispose();
        music.dispose();

        // Libère les composants avancés de VISUI
        colorPicker.dispose();
        VisUI.dispose();
        
        //enregistre les paramètres utilisateurs
        prefs.flush();

        // Libère l'écran actif
        this.getScreen().dispose();
    }

    // Remplace le screen actif et dispose du précédent
    public void switchScreen(Screen newScreen) {
        Screen oldScreen = this.getScreen();
        this.setScreen(newScreen);
        oldScreen.dispose();
    }

    // Set GameSocket
    public void setGameSocket(GameSocket gameSocket) {
        this.gameSocket = gameSocket;
    }

    // Get GameSocket
    public GameSocket getGameSocket() {
        return this.gameSocket;
    }
}