package com.glhf.on_est_djbomb.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.networking.GameGuestSocket;
import com.glhf.on_est_djbomb.networking.GameHostSocket;
import com.glhf.on_est_djbomb.networking.GameSocket;
import com.glhf.on_est_djbomb.screens.LobbyScreen;

public class NewGameDialog extends Dialog {
    private final OnEstDjbombGame game;
    private final Stage gameStage;
    private TextField pseudoTextField;
    private TextField ipTextField;
    private TextField portTextField;

    public NewGameDialog(String title, OnEstDjbombGame game, Stage stage) {
        super(title, game.skin);
        this.game = game;
        gameStage = stage;
    }

    //Procédure initContent, initialise le contenu de la boîte de dialogue Nouvelle partie
    public void initContent() {

        Table informationsContentTable = getContentTable();

        /** Section Contenu **/
        // Paramétrage du titre
        this.getTitleLabel().setAlignment(Align.center);

        // Label Host
        Label lblHost = new Label("Voulez-vous créer ou rejoindre une partie ?", game.skin, "title");
        informationsContentTable.add(lblHost).pad(30);
        informationsContentTable.row();

        /** Section Boutons **/
        // Ajout du bouton Héberger dans la boîte de dialogue
        TextButton txtBtnHost = new TextButton("  Héberger  ",game.skin,"title");
        txtBtnHost.pad(5,30,5,30);
        button(txtBtnHost,1L).pad(30);

        // Ajout du bouton Rejoindre dans la boîte de dialogue
        TextButton txtBtnJoin = new TextButton("Rejoindre",game.skin,"title");
        txtBtnJoin.pad(5,30,5,30);
        button(txtBtnJoin,2L).pad(30);

        // Ajout du bouton Retour dans la boîte de dialogue
        TextButton txtBtnReturn = new TextButton("Retour",game.skin,"title");
        txtBtnReturn.pad(5,30,5,30);
        button(txtBtnReturn,3L).pad(30);
    }

    @Override
    protected void result(Object object) {
        // Option "Héberger"
        if (object.equals(1L)) {
            new Dialog("Héberger une partie", game.skin) {
                {
                    /** Section Contenu **/
                    // Paramétrage du titre
                    getTitleLabel().setAlignment(Align.center);

                    // Label Pseudonyme
                    Label lblPseudo = new Label("Pseudonyme :", game.skin, "title");
                    getContentTable().add(lblPseudo).pad(30);

                    // Paramétrage du champ réservé au texte
                    pseudoTextField = new TextField(game.prefs.getString("pseudo"), game.skin, "title");
                    getContentTable().add(pseudoTextField).pad(30).width(300);

                    /** Section Boutons **/
                    // Ajout du bouton Retour dans la boîte de dialogue
                    TextButton txtBtnReturn = new TextButton("  Retour  ",game.skin,"title");
                    txtBtnReturn.pad(5,30,5,30);
                    button(txtBtnReturn,1L).pad(30);

                    // Ajout du bouton Confirmer dans la boîte de dialogue
                    TextButton txtBtnConfirm = new TextButton("Confirmer",game.skin,"title");
                    txtBtnConfirm.pad(5,30,5,30);
                    button(txtBtnConfirm,2L).pad(30);
                }

                @Override
                protected void result(Object object) {
                    if (object.equals(2L)) {
                        // On observe si pseudoTextField n'est pas vide
                        if (!pseudoTextField.getText().isEmpty()) {
                        	// Initialisation GameSocket
                            GameHostSocket gameSocket = new GameHostSocket(pseudoTextField.getText());
                            
                            //preference utilisateur pseudo
                            game.prefs.putString("pseudo", pseudoTextField.getText());

                            // Attente de connexion d'un client
                            new Thread(gameSocket::init).start();

                            // Changement d'écran
                            game.setGameSocket(gameSocket);
                            game.switchScreen(new LobbyScreen(game, true));
                        } else {
                            new Dialog("Pseudo invalide", game.skin) {
                                {
                                    /** Section Contenu **/
                                    // Paramétrage du titre
                                    getTitleLabel().setAlignment(Align.center);

                                    // Label Message
                                    Label lblNeedTitle = new Label("Veuillez renseigner un pseudo", game.skin, "title");
                                    getContentTable().add(lblNeedTitle).pad(30);

                                    /** Section Boutons **/
                                    // Ajout du bouton Retour dans la boîte de dialogue
                                    TextButton txtBtnReturn = new TextButton("  Retour  ",game.skin,"title");
                                    txtBtnReturn.pad(5,30,5,30);
                                    button(txtBtnReturn,1L).pad(30);
                                }
                            }.show(gameStage);
                        }
                    }
                }
            }.show(gameStage);
        }

        // Option "Rejoindre"
        else if (object.equals(2L)) {
            new Dialog("Partie à rejoindre", game.skin) {
                {
                    /** Section Contenu **/
                    // Paramétrage du titre
                    getTitleLabel().setAlignment(Align.center);

                    // Label Pseudonyme
                    Label lblPseudo = new Label("Pseudonyme :", game.skin, "title");
                    getContentTable().add(lblPseudo).pad(30).left();

                    // Paramétrage du champ réservé au texte
                    pseudoTextField = new TextField(game.prefs.getString("pseudo"), game.skin, "title");
                    getContentTable().add(pseudoTextField).pad(30).width(300);

                    getContentTable().row();

                    // Label IP
                    Label lblIP = new Label("Adresse IP :", game.skin, "title");
                    getContentTable().add(lblIP).pad(30).left();

                    // Paramétrage du champ réservé au texte
                    ipTextField = new TextField(game.prefs.getString("IP"), game.skin, "title");
                    getContentTable().add(ipTextField).pad(30).width(300);

                    getContentTable().row();

                    // Label Port
                    Label lblPort = new Label("Clé de la salle :", game.skin, "title");
                    getContentTable().add(lblPort).pad(30).left();

                    // Paramétrage du champ réservé au texte
                    portTextField = new TextField(game.prefs.getString("Clef"), game.skin, "title");
                    getContentTable().add(portTextField).pad(30).width(300);

                    getContentTable().row();

                    /** Section Boutons **/
                    // Ajout du bouton Retour dans la boîte de dialogue
                    TextButton txtBtnReturn = new TextButton("  Retour  ",game.skin,"title");
                    txtBtnReturn.pad(5,30,5,30);
                    button(txtBtnReturn,1L).pad(30);

                    // Ajout du bouton Confirmer dans la boîte de dialogue
                    TextButton txtBtnConfirm = new TextButton("Confirmer",game.skin,"title");
                    txtBtnConfirm.pad(5,30,5,30);
                    button(txtBtnConfirm,2L).pad(30);
                }

                @Override
                protected void result(Object object) {
                    if (object.equals(2L)) {
                        // On observe si les champs textuels ne sont pas vides
                        if (!pseudoTextField.getText().isEmpty() && !ipTextField.getText().isEmpty() && !portTextField.getText().isEmpty()) {

                            // Initialisation GameSocket
                            GameGuestSocket gameSocket = new GameGuestSocket(pseudoTextField.getText());
                            game.prefs.putString("pseudo", pseudoTextField.getText());

                            // Connexion à l'host
                            gameSocket.init(ipTextField.getText(), Integer.parseInt(portTextField.getText()));

                            // Changement d'écran
                            game.setGameSocket(gameSocket);
                            game.switchScreen(new LobbyScreen(game, false));
                        } else {
                            new Dialog("Paramètres invalides", game.skin) {
                                {
                                    /** Section Contenu **/
                                    // Paramétrage du titre
                                    getTitleLabel().setAlignment(Align.center);

                                    // Label Message
                                    Label lblNeedParameter = new Label("Un des champs n'est pas renseigné", game.skin, "title");
                                    getContentTable().add(lblNeedParameter).pad(30);

                                    /** Section Boutons **/
                                    // Ajout du bouton Retour dans la boîte de dialogue
                                    TextButton txtBtnReturn = new TextButton("  Retour  ",game.skin,"title");
                                    txtBtnReturn.pad(5,30,5,30);
                                    button(txtBtnReturn,1L).pad(30);
                                }
                            }.show(gameStage);
                        }
                    }
                }
            }.show(gameStage);
        }
    }
}
