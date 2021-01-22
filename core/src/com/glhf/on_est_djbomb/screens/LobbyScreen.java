package com.glhf.on_est_djbomb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.networking.GameSocket;

public class LobbyScreen implements Screen {
    private final Stage stage;
    private boolean localReady = false;
    private boolean teammateReady = false;

    public LobbyScreen(final OnEstDjbombGame game, boolean host) {
        // Instanciation du stage (Hiérarchie de nos acteurs)
        stage = new Stage(new ScreenViewport());
        // Liaison des Inputs au stage
        Gdx.input.setInputProcessor(stage);

        // Instanciation d'une table pour contenir nos acteurs
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Booléen indiquant si les joueurs sont prêts
        localReady = false;
        teammateReady = false;

        /** Création des labels **/
        // Titre du Lobby
        Label titreLabel = new Label("Lancement d'une partie", game.skin, "title");
        // Label Statut
        Label lblReadStatut = new Label("Statut", game.skin, "title");

        //Label Hôte/Guest
        Label lblLocalHostGuest;
        Label lblRemoteHostGuest;
        if(host = true){
            lblLocalHostGuest = new Label("Hébergeur :",game.skin, "title");
            lblRemoteHostGuest = new Label("Invité :",game.skin, "title");
        }else{
            lblLocalHostGuest = new Label("Invité :",game.skin, "title");
            lblRemoteHostGuest = new Label("Hébergeur :",game.skin, "title");
            System.out.println(GameSocket.GameSocketConstant.HOST);
            System.out.println(game.getGameSocket().getSocketType());
        }

        /** Local **/
        // Label Identifiant Local
        Label lblLocalId = new Label(game.getGameSocket().getIdentifiant()+"",game.skin, "title");
        // Label État de la connexion Local
        Label lblLocalReady = new Label("Pas prêt", game.skin, "title");

        /** Remote **/
        // Label Identifiant Partenaire
        Label lblRemoteId = new Label(game.getGameSocket().getRemoteIdentifiant()+"",game.skin, "title");
        // Label État de la connexion du Partenaire
        Label lblRemoteReady = new Label("Pas prêt", game.skin, "title");

        /** Champs Ip et port **/
        // Label Adress IP
        Label lblIp = new Label("Adresse IP du salon :",game.skin, "title");
        // Label Port
        Label lblPort = new Label("Clef du salon :", game.skin, "title");

        // Avertissement de la connexion à l'hôte distant
        game.getGameSocket().sendMessage("STATE::NOTREADY");

        /** Création des TextBox **/
        // Paramétrage du champ réservé à l'IP
        TextField textIp = new TextField(game.getGameSocket().getInfoIp(), game.skin, "title");
        // Paramétrage du champ réservé au Port
        TextField textPort = new TextField(game.getGameSocket().getInfoPort(),game.skin,"title");

        /** Création des Boutons **/
        TextButton retourButton = new TextButton("Retour", game.skin, "title");
        TextButton commencerButton = new TextButton("Commencer", game.skin, "title");
        TextButton pretButton = new TextButton("Prêt", game.skin, "title");
        commencerButton.setColor(Color.LIGHT_GRAY);

        /** Ajout des acteurs **/
        // Titre
        root.add(setContainer(titreLabel)).colspan(3).expand();
        root.row();

        /** Création d'une table contenant les informations **/
        Table infoLobby = new Table();
        root.add(infoLobby).fillX().colspan(3);

        //Statut
        infoLobby.add(lblReadStatut).pad(10,150,10,20).left();
        infoLobby.row();

        // Joueur Local
        infoLobby.add(lblLocalHostGuest).pad(10,150,10,20).left();
        infoLobby.add(lblLocalId).pad(10,20,10,20).left();
        infoLobby.add(lblLocalReady).pad(10,20,10,150).left();
        infoLobby.row();

        // Partenaire
        infoLobby.add(lblRemoteHostGuest).pad(10,150,10,20).left();
        infoLobby.add(lblRemoteId).pad(10,20,10,20).left();
        infoLobby.add(lblRemoteReady).pad(10,20,10,150).left();
        infoLobby.row();

        // Informations Ip Salon
        infoLobby.add(lblIp).pad(70,150,10,20).left();
        infoLobby.add(textIp).pad(70,20,10,150).left().width(430).colspan(2).left();
        infoLobby.row();

        // Informations Port Salon
        infoLobby.add(lblPort).pad(10,150,10,20).left();
        infoLobby.add(textPort).pad(10,20,10,150).left().width(430).colspan(2).left();

        root.row();

        /** Ajout des boutons **/
        //root.add().pad(10,250,10,20);
        root.add(setContainer(retourButton)).pad(10,20,10,20).expandY();//.expand();
        root.add(setContainer(pretButton)).expandY();//.expand();
        root.add(setContainer(commencerButton)).expandY();

        // Gestionnaire d'évènements des bouttons
        retourButton.addListener(new ClickListener() {
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
        pretButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // On inverse le statut du joueur local
                localReady = !localReady;
                // On indique le nouveau statut au joueur distant
                if (localReady) {
                    game.getGameSocket().sendMessage("STATE::READY");
                    lblLocalReady.setText("Prêt");
                } else {
                    game.getGameSocket().sendMessage("STATE::NOTREADY");
                    lblLocalReady.setText("Pas prêt");
                }
                // Si les deux joueurs sont prêts, on débloque le bouton "commencer"
                if (localReady && teammateReady) {
                    commencerButton.setColor(Color.WHITE);
                } else {
                    commencerButton.setColor(Color.LIGHT_GRAY);
                }
            }
        });
        commencerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Si les deux joueurs sont prêts à commencer, on démarre la partie
                if(localReady && teammateReady){
                    // On indique le début de la partie à l'autre équipe
                    game.getGameSocket().sendMessage("STATE::START");
                    // On vide le gestionnaire de listeners
                    game.getGameSocket().clearListeners();
                    // Changement d'écran pour revenir au menu principal
                    game.switchScreen(new GameScreen(game));
                }
                // Sinon on indique à l'utilisateur que les deux équipes ne sont pas prêtes
                else {
                    new Dialog("En attente", game.skin) {
                        {
                            /** Section Contenu **/
                            // Paramétrage du titre
                            getTitleLabel().setAlignment(Align.center);

                            // Label Message
                            Label lblNeedParameter = new Label("L'une des deux équipes n'est pas prête", game.skin, "title");
                            getContentTable().add(lblNeedParameter).pad(30);

                            /** Section Boutons **/
                            // Ajout du bouton Retour dans la boîte de dialogue
                            TextButton txtBtnOk = new TextButton("   Ok   ",game.skin,"title");
                            txtBtnOk.pad(5,30,5,30);
                            button(txtBtnOk,1L).pad(30);
                        }
                    }.show(stage);
                }
            }
        });

        // Gestion Game State
        game.getGameSocket().addListener(eventMessage -> {
            // Parse
            String[] tokens = eventMessage.split("::");

            if (tokens[0].equals("STATE")) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (tokens[1].equals("READY")) {
                            // On mémorise le statut du joueur distant
                            teammateReady = true;
                            // Si les deux joueurs sont prêts, on débloque le bouton "commencer"
                            if (localReady) {
                                commencerButton.setColor(Color.WHITE);
                            }
                            lblRemoteReady.setText("Prêt");
                            lblRemoteId.setText(game.getGameSocket().getRemoteIdentifiant());

                        } else if (tokens[1].equals("NOTREADY")) {
                            // On mémorise le statut du joueur distant
                            teammateReady = false;
                            // On bloque le bouton "commencer"
                            commencerButton.setColor(Color.LIGHT_GRAY);
                            // On modifie le statut de la partie
                            lblRemoteReady.setText("Pas prêt");
                            lblRemoteId.setText(game.getGameSocket().getRemoteIdentifiant());

                        } else if (tokens[1].equals("START")) {
                            // On vide le gestionnaire de listeners
                            game.getGameSocket().clearListeners();
                            // Changement d'écran pour revenir au menu principal
                            game.switchScreen(new GameScreen(game));
                        }
                    }
                });
            }
            // Flag non reconnu
            else {
                Gdx.app.log("SocketFlagError", "Le flag envoyé par le socket distant n'est pas reconnu");
            }
        });
    }

    // setContainer retourne un TextButton dans son contenant fonction utilisée pour dimensionner le bouton
    public Container<TextButton> setContainer(TextButton textButton){
        // Création du contenant
        Container<TextButton> ctnNewGameButton = new Container<TextButton>(textButton);

        // Paramétrage du TextBouton
        textButton.pad(10);
        textButton.getLabel().setFontScale(1.5f);

        // Paramétrage du contenant et ajout du TextBouton
        ctnNewGameButton.width(300);
        ctnNewGameButton.setOrigin(Align.center);
        ctnNewGameButton.center();
        ctnNewGameButton.setTransform(true);

        return ctnNewGameButton;
    }

    // setContainer retourne un Label dans son contenant fonction utilisée pour dimensionner le bouton
    public Container<Label> setContainer(Label label){
        // Création du contenant
        Container<Label> ctnLabel = new Container<Label>(label);

        // Paramétrage du Label
        label.setFontScale(1.5f);

        // Paramétrage du contenant et ajout du TextBouton
        ctnLabel.width(350);
        ctnLabel.setOrigin(Align.center);
        ctnLabel.getActor().setAlignment(Align.center);

        return ctnLabel;
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
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {

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
}
