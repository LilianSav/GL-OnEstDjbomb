package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.kotcrab.vis.ui.widget.color.ColorPicker;

public class OptionsDialog extends Dialog {
    private final Skin skin;
    private Music music;
    private Slider sliderVolumeMusique;
    private Slider sliderVolumeEffetSonore;
    private CheckBox checkBoxColor;
    private Preferences prefs;
    private Stage stage;
    private ColorPicker colorPicker;

    public OptionsDialog(String title, OnEstDjbombGame game, Stage stage) {
        super(title, game.skin);
        this.skin = game.skin;
        this.music = game.music;
        this.prefs = game.prefs;
        this.stage = stage;
        this.colorPicker = game.colorPicker;
    }

    //Procédure initContent, initialise le contenu de la boîte de dialogue d'options
    public void initContent() {

        /* Section Contenu */
        Table informationsContentTable = getContentTable();

        // Paramétrage du titre
        this.getTitleLabel().setAlignment(Align.center);

        // Label Musique
        Label lblMusique = new Label("Musique", skin, "title");
        informationsContentTable.add(lblMusique).pad(10, 30, 10, 30).left();

        // Slider volume Musique
        sliderVolumeMusique = new Slider(0f, 100, 1f, false, skin);
        sliderVolumeMusique.setValue(prefs.getFloat("volumeMusique"));
        informationsContentTable.add(sliderVolumeMusique).pad(10, 30, 10, 30);
        informationsContentTable.row();

        // Label Effets sonores
        Label lblEffetsSonores = new Label("Effets sonores", skin, "title");
        informationsContentTable.add(lblEffetsSonores).pad(10, 30, 10, 30).left();

        // Slider volume Effets Sonores
        sliderVolumeEffetSonore = new Slider(0f, 100, 1f, false, skin);
        sliderVolumeEffetSonore.setValue(prefs.getFloat("volumeEffetSonore"));
        informationsContentTable.add(sliderVolumeEffetSonore).pad(10, 30, 10, 30);
        informationsContentTable.row();

        // Label activation de la couleur personnalisée
        Label labelCheckBoxColor = new Label("Couleur d'interface personnalisée ", skin, "title");
        informationsContentTable.add(labelCheckBoxColor).pad(10, 30, 10, 30).left();

        // CheckBox pour activer la couleur personnalisée
        checkBoxColor = new CheckBox("", skin);
        checkBoxColor.setChecked(prefs.getBoolean("interfaceColorChecked"));
        informationsContentTable.add(checkBoxColor).pad(10, 30, 10, 30);
        informationsContentTable.row();

        // Label pour indiquer l'ouverture du colorPicker
        Label labelChoixColor = new Label("Choix de la couleur ", skin, "title");
        informationsContentTable.add(labelChoixColor).pad(10, 30, 10, 30).left();

        // Bouton pour ouvrir le colorPicker
        Button buttonChoixColor = new Button(skin, "colorwheel");
        informationsContentTable.add(buttonChoixColor).pad(10, 30, 10, 30);
        informationsContentTable.row();
        // Gestion événement du bouton
        buttonChoixColor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Affichage du ColorPicker
                stage.addActor(colorPicker.fadeIn());
            }
        });


        /* Section Boutons */
        // Ajout du bouton retour dans la boîte de dialogue
        TextButton txtBtnReturn = new TextButton("  Retour  ", skin, "title");
        txtBtnReturn.pad(5, 30, 5, 30);
        button(txtBtnReturn, 1L).pad(30);

        // Ajout du bouton appliquer dans la boîte de dialogue
        TextButton txtBtnApply = new TextButton("Appliquer", skin, "title");
        txtBtnApply.pad(5, 30, 5, 30);
        button(txtBtnApply, 2L).pad(30);
    }

    @Override
    protected void result(Object object) {
        if (object.equals(1L)) {
            sliderVolumeMusique.setValue(prefs.getFloat("volumeMusique"));
            sliderVolumeEffetSonore.setValue(prefs.getFloat("volumeEffetSonore"));
            checkBoxColor.setChecked(prefs.getBoolean("interfaceColorChecked"));
        }
        if (object.equals(2L)) {
            prefs.putFloat("volumeMusique", sliderVolumeMusique.getValue());
            prefs.putFloat("volumeEffetSonore", sliderVolumeEffetSonore.getValue());
            prefs.putBoolean("interfaceColorChecked", checkBoxColor.isChecked());
            music.setVolume(prefs.getFloat("volumeMusique") / 100);
        }
    }
}
