package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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

    public void initContent(){
        // Section Content
        Table informationsContentTable = getContentTable();

        informationsContentTable.add(new Label("Musique : ", skin));
        Slider sliderVolumeMusique = new Slider(0f, 100, 1f, false, skin);
        sliderVolumeMusique.setValue(prefs.getFloat("volumeMusique"));
        informationsContentTable.add(sliderVolumeMusique);
        informationsContentTable.row();
        informationsContentTable.add(new Label("Effets sonores : ", skin));
        Slider sliderVolumeEffetSonore = new Slider(0f, 100, 1f, false, skin);
        sliderVolumeEffetSonore.setValue(prefs.getFloat("volumeEffetSonore"));
        informationsContentTable.add(sliderVolumeEffetSonore);
        informationsContentTable.row();
        /* futur implémentation
        informationsContentTable.add(new Label("Chat vocal : ", skin));
        Slider sliderVolumeChatVocal = new Slider(0f, 100, 1f, false, skin);
        sliderVolumeChatVocal.setValue(prefs.getFloat("volumeChatVocal"));
        informationsContentTable.add(sliderVolumeChatVocal);
        */
        
        this.sliderVolumeMusique=sliderVolumeMusique;
        this.sliderVolumeEffetSonore=sliderVolumeEffetSonore;
        //this.sliderVolumeChatVocal=sliderVolumeChatVocal;
        
        // Section button
        button("Retour",1L);
        button("Sauvegarder",2L);
    }

    @Override
    protected void result(Object object) {
    	if (object.equals(1L)) {
    		//todo remettre ancienne val
    		sliderVolumeMusique.setValue(prefs.getFloat("volumeMusique"));
    		sliderVolumeEffetSonore.setValue(prefs.getFloat("volumeEffetSonore"));
    		//sliderVolumeChatVocal.setValue(prefs.getFloat("volumeChatVocal"));
    	}
    	if (object.equals(2L)) {
    		prefs.putFloat("volumeMusique", sliderVolumeMusique.getValue());
    		prefs.putFloat("volumeEffetSonore", sliderVolumeEffetSonore.getValue());
    		//prefs.putFloat("volumeChatVocal", sliderVolumeChatVocal.getValue());
    		music.setVolume(prefs.getFloat("volumeMusique")/100);
    	}
    }
}
