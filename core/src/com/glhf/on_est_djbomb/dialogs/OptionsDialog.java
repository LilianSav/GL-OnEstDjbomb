package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.glhf.on_est_djbomb.OnEstDjbombGame;

public class OptionsDialog extends Dialog {
    private final Skin skin;
    private Music music;
    private Slider sliderVolumeMusique;
    private Slider sliderVolumeEffetSonore;
    private Slider sliderVolumeChatVocal;
    private Preferences prefs;
    
    public OptionsDialog(String title, OnEstDjbombGame game) {
        super(title, game.skin);
        this.skin = game.skin;
        this.music=game.music;
        this.prefs=game.prefs;
    }

    //Procédure initContent, initialise le contenu de la boîte de dialogue d'options
    public void initContent(){

        /** Section Contenu **/
        Table informationsContentTable = getContentTable();

        // Paramétrage du titre
        this.getTitleLabel().setAlignment(Align.center);

        // Label Musique
        Label lblMusique = new Label("Musique", skin, "title");
        informationsContentTable.add(lblMusique).pad(10,30,10,30).left();

        // Slider volume Musique
        Slider sliderVolumeMusique = new Slider(0f, 100, 1f, false, skin);
        sliderVolumeMusique.setValue(prefs.getFloat("volumeMusique"));
        informationsContentTable.add(sliderVolumeMusique).pad(10,30,10,30);
        informationsContentTable.row();
        this.sliderVolumeMusique=sliderVolumeMusique;

        // Label Effets sonores
        Label lblEffetsSonores = new Label("Effets sonores", skin, "title");
        informationsContentTable.add(lblEffetsSonores).pad(10,30,10,30).left();

        // Slider volume Effets Sonores
        Slider sliderVolumeEffetSonore = new Slider(0f, 100, 1f, false, skin);
        sliderVolumeEffetSonore.setValue(prefs.getFloat("volumeEffetSonore"));
        informationsContentTable.add(sliderVolumeEffetSonore).pad(10,30,10,30);
        informationsContentTable.row();
        this.sliderVolumeEffetSonore=sliderVolumeEffetSonore;

        /** Section Boutons **/
        // Ajout du bouton retour dans la boîte de dialogue
        TextButton txtBtnReturn = new TextButton("  Retour  ",skin,"title");
        txtBtnReturn.pad(5,30,5,30);
        button(txtBtnReturn,1L).pad(30);

        // Ajout du bouton appliquer dans la boîte de dialogue
        TextButton txtBtnApply = new TextButton("Appliquer",skin,"title");
        txtBtnApply.pad(5,30,5,30);
        button(txtBtnApply,2L).pad(30);
    }

    @Override
    protected void result(Object object) {
    	if (object.equals(1L)) {
    		sliderVolumeMusique.setValue(prefs.getFloat("volumeMusique"));
    		sliderVolumeEffetSonore.setValue(prefs.getFloat("volumeEffetSonore"));
    	}
    	if (object.equals(2L)) {
    		prefs.putFloat("volumeMusique", sliderVolumeMusique.getValue());
    		prefs.putFloat("volumeEffetSonore", sliderVolumeEffetSonore.getValue());
    		music.setVolume(prefs.getFloat("volumeMusique")/100);
    	}
    }
}
