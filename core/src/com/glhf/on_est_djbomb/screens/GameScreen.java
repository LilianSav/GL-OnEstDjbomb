package com.glhf.on_est_djbomb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.dialogs.OptionsDialog;
import com.glhf.on_est_djbomb.enigmas.EnigmaManager;
import com.glhf.on_est_djbomb.networking.GameSocket;

public class GameScreen implements Screen {
    private final Stage stage;
    private Sound sound;
    private final Label timerLabel;
    private int tpsRestant;
    private int tpsInitial;
    private int tpsInitialEnigme;
    private final EnigmaManager enigmeManager;
    private OnEstDjbombGame game;
    private boolean isOver;
    private TextButton solutionButton;
    private TextButton indiceButton;
    private boolean isUnder30s;

    // Champs textuels initiaux
    private final String PSEUDO_INIT = "Discussion";
    private final String MESSAGE_INIT = "En attente de message ...";

    public GameScreen(OnEstDjbombGame game, String gameConfig) {
        this.game = game;
        isOver = false;

        // Instanciation du stage (Hiérarchie de nos acteurs)
        stage = new Stage(new ScreenViewport());
        // Liaison des Inputs au stage
        Gdx.input.setInputProcessor(stage);

        // Instanciation d'un effet sonore
        sound = Gdx.audio.newSound(Gdx.files.internal("audio/bomb_has_been_planted.mp3"));
        sound.play(game.prefs.getFloat("volumeEffetSonore") / 100);

        // Ajout de la table contenant les éléments de GameScreen
        // Instanciation de la table pour contenir les Layouts (Énigmes, UI, Chat)
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Ajout de la table réservée aux énigmes
        // Instanciation du gestionnaire d'énigmes
        if (game.getGameSocket().getSocketType() == GameSocket.GameSocketConstant.HOST) {
            enigmeManager = new EnigmaManager(true, game, stage, gameConfig);
        } else {
            enigmeManager = new EnigmaManager(false, game, stage, gameConfig);
        }
        root.add(enigmeManager).width(Value.percentWidth(0.70f, root)).height(Value.percentHeight(0.70f, root));

        // Ajout de la table réservée à l'interface utilisateur
        Table userInterfaceTable = new Table();
        root.add(userInterfaceTable).width(Value.percentWidth(0.20f, root)).height(Value.percentHeight(0.70f, root));
        root.row();

        // Ajout de la table réservée au chat textuel
        Table textChatTable = new Table();
        root.add(textChatTable).colspan(2).width(Value.percentWidth(0.9f, root)).height(Value.percentHeight(0.25f, root));

        // Remplissage de la table chat textuel
        // Initialisation et paramétrage des labels
        Label lblPseudo = new Label(PSEUDO_INIT + " :", game.skin, "title");
        lblPseudo.setAlignment(Align.left);
        lblPseudo.setFillParent(true);
        Label lblMessage = new Label(MESSAGE_INIT, game.skin, "title");
        lblMessage.setAlignment(Align.left);
        lblMessage.setFillParent(true);
        // Conteneurs des labels
        Container<Label> ctnLblPseudo = new Container<Label>(lblPseudo);
        ctnLblPseudo.pad(10).setOrigin(Align.center);
        ctnLblPseudo.setTransform(true);
        Container<Label> ctnLblMessage = new Container<Label>(lblMessage);
        ctnLblMessage.pad(10).setOrigin(Align.center);
        ctnLblMessage.setTransform(true);

        // Table des conteneurs
        Table tableMessage = new Table();
        tableMessage.add(ctnLblPseudo).left();
        tableMessage.add(ctnLblMessage).expandX().left();
        // Initialisation du ScrollPane
        ScrollPane scrollPaneMessage = new ScrollPane(tableMessage, game.skin);
        scrollPaneMessage.setFadeScrollBars(false);
        scrollPaneMessage.setScrollbarsVisible(true);
        scrollPaneMessage.setFlickScroll(true);
        scrollPaneMessage.setForceScroll(false, true);

        // Table contenant le scrollPane
        Table tableScrollPane = new Table();
        tableScrollPane.add(scrollPaneMessage).left().width(Value.percentWidth(1f, tableScrollPane)).height(Value.percentHeight(1f, tableScrollPane));

        textChatTable.add(tableScrollPane).height(Value.percentHeight(0.75f, textChatTable)).width(Value.percentWidth(1f, textChatTable));

        // Bouton et saisie textuelle
        // Table des conteneurs
        Table tableTextBox = new Table();
        // Création chat textuel
        TextField chatTextField = new TextField("", game.skin, "title");
        chatTextField.setFillParent(true);
        TextButton sendButton = new TextButton("Envoyer", game.skin, "title");
        sendButton.setFillParent(true);
        sendButton.pad(5, 20, 5, 20);
        // Conteneurs
        Container<TextField> ctnChatTextField = new Container<TextField>(chatTextField);
        Container<TextButton> ctnSendButton = new Container<TextButton>(sendButton);
        ctnChatTextField.setOrigin(Align.left);
        ctnChatTextField.left();
        ctnChatTextField.setTransform(true);

        // Ajout à la table
        tableTextBox.add(ctnChatTextField).expandX().fill().pad(10, 10, 5, 10);
        tableTextBox.add(ctnSendButton);

        textChatTable.row();
        textChatTable.add(tableTextBox).height(Value.percentHeight(0.25f, textChatTable)).width(Value.percentWidth(1f, textChatTable));

        // Paramétrage du timer
        tpsInitial = 1800; // 30 minutes
        tpsRestant = tpsInitial;
        tpsInitialEnigme = tpsInitial;
        isUnder30s = tpsRestant < 30;
        startTimer();

        // Création interface utilisateur latérale
        TextButton optionsButton = new TextButton("Options", game.skin, "title");
        TextButton quitterButton = new TextButton("Quitter", game.skin, "title");

        Image imgBombeL = new Image(new Texture(Gdx.files.internal("images/bombe.png")));
        Container<Image> ctnImgBombeL = new Container<Image>(imgBombeL);
        timerLabel = new Label(secToMinSec(tpsRestant), game.skin, "title");
        Image imgBombeR = new Image(new Texture(Gdx.files.internal("images/bombe.png")));
        Container<Image> ctnImgBombeR = new Container<Image>(imgBombeR);
        Table tableTimer = new Table();
        tableTimer.add(ctnImgBombeL).width(50).height(50).pad(10);
        tableTimer.add(timerLabel).expand().pad(10);
        tableTimer.add(ctnImgBombeR).width(50).height(50).pad(10);

        indiceButton = new TextButton(" Indice  ", game.skin, "title");
        solutionButton = new TextButton("Solution", game.skin, "title");

        Image imgCode = new Image(new Texture(Gdx.files.internal("images/codeLabel.png")));
        Container<Image> ctnImgCode = new Container<Image>(imgCode);

        TextField verificationTextField = new TextField("", game.skin, "title");
        TextButton verificationbutton = new TextButton("Ok", game.skin, "title");

        Table tableValidationCode = new Table();
        tableValidationCode.add(verificationTextField).width(200);
        tableValidationCode.add(verificationbutton).width(80).padLeft(20);

        optionsButton.pad(5, 20, 5, 20);
        quitterButton.pad(5, 20, 5, 20);
        indiceButton.pad(5, 20, 5, 20);
        solutionButton.pad(5, 20, 5, 20);
        sendButton.pad(5, 20, 5, 20);
        verificationbutton.pad(5, 20, 5, 20);

        // Paramétrages boutons
        indiceButton.setColor(Color.LIGHT_GRAY);
        solutionButton.setColor(Color.LIGHT_GRAY);

        // Création boîte de dialogue option
        OptionsDialog optionsDialog = new OptionsDialog("Options", game, stage);
        optionsDialog.initContent();

        userInterfaceTable.add(optionsButton).expand();
        userInterfaceTable.add(quitterButton).expand();
        userInterfaceTable.row();
        userInterfaceTable.add(tableTimer).colspan(2).pad(10);
        userInterfaceTable.row();
        userInterfaceTable.add(indiceButton).expand();
        userInterfaceTable.add(solutionButton).expand();
        userInterfaceTable.row();
        userInterfaceTable.add(ctnImgCode).padTop(100).colspan(2).width(300).height(65);
        userInterfaceTable.row();
        userInterfaceTable.add(tableValidationCode).pad(20, 20, 50, 20).colspan(2);

        // Gestion des événements
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // On vérifie que le champ de texte n'est pas vide
                if(!chatTextField.getText().equals("")){
                    if (lblPseudo.getText().toString().equals(PSEUDO_INIT + " :") && lblMessage.getText().toString().equals(MESSAGE_INIT)) {
                        // Changement texte
                        lblPseudo.setText(game.getGameSocket().getIdentifiant() + " :");
                        lblMessage.setText(chatTextField.getText());
                    } else {
                        // Ajout texte
                        lblPseudo.setText(lblPseudo.getText() + "\n" + game.getGameSocket().getIdentifiant() + " :");
                        lblMessage.setText(lblMessage.getText() + "\n" + chatTextField.getText());

                    }
                    scrollPaneMessage.scrollTo(0, 0, 0, 0);

                    // Envoi de l'information à l'autre machine
                    game.getGameSocket().sendMessage("TEXT::" + chatTextField.getText());
                    chatTextField.setText("");
                }
            }
        });
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Affichage du dialogue d'options
                optionsDialog.show(stage);
            }
        });
        quitterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stopTimer();
                // On vide le gestionnaire de listeners
                game.getGameSocket().clearListeners();
                // Fermeture des flux
                game.getGameSocket().close();
                // Changement d'écran pour revenir au menu principal
                game.switchScreen(new MainMenuScreen(game));
            }
        });
        verificationbutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean repTrouve = verificationTextField.getText().equals(String.valueOf(enigmeManager.getSolution()));
                finDePartie(repTrouve, false);
            }
        });
        indiceButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (tpsRestant <= tpsInitialEnigme - enigmeManager.getTpsBeforeIndice()) {
                    enigmeManager.getIndiceDialog();
                } else {
                    enigmeManager.getInstructionIndice();
                }
            }
        });
        solutionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (tpsRestant <= tpsInitialEnigme - enigmeManager.getTpsBeforeSolution()) {
                    enigmeManager.getSolutionDialog();
                } else {
                    enigmeManager.getInstructionSolution();
                }
            }
        });


        // Gestion Message et Game State
        game.getGameSocket().addListener(eventMessage -> {
            // Parse
            String[] tokens = eventMessage.split("::");

            // FLAG TEXT : On modifie le contenu du chat textuel
            if (tokens[0].equals("TEXT")) {
                if (lblPseudo.getText().toString().equals(PSEUDO_INIT + " :") && lblMessage.getText().toString().equals(MESSAGE_INIT)) {
                    Gdx.app.postRunnable(() -> lblPseudo.setText(game.getGameSocket().getRemoteIdentifiant() + " :"));
                    Gdx.app.postRunnable(() -> lblMessage.setText(tokens[1]));
                } else {
                    Gdx.app.postRunnable(() -> lblPseudo.setText(lblPseudo.getText() + "\n" + game.getGameSocket().getRemoteIdentifiant() + " :"));
                    Gdx.app.postRunnable(() -> lblMessage.setText(lblMessage.getText() + "\n" + tokens[1]));
                }
                scrollPaneMessage.scrollTo(0, 0, 0, 0);
            }
            // FLAG STATE : On modifie l'état du jeu
            else if (tokens[0].equals("STATE")) {
                if (tokens[1].equals("GOODEND")) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            new Dialog("Bonne reponse", game.skin) {
                                {
                                    // Paramétrage du titre
                                    getTitleLabel().setAlignment(Align.center);

                                    // Indication que l'autre équipe a trouvé la réponse
                                    Label lblMessage = new Label("Vos coéquipiers ont trouvés la bonne réponse !", game.skin, "title");
                                    getContentTable().add(lblMessage).pad(30);

                                    // Effet sonore
                                    sound = Gdx.audio.newSound(Gdx.files.internal("audio/correct_sound_effect.mp3"));
                                    sound.play(game.prefs.getFloat("volumeEffetSonore") / 100);
                                    //mise à jour tps utilisé
                                    tpsInitialEnigme = tpsRestant;
                                    enigmeManager.setTpsUtilise(tpsInitialEnigme);
                                    if (enigmeManager.isOver()) {
                                        stopTimer();
                                        TextButton txtBtnEndgame = new TextButton("Menu de fin de partie", game.skin, "title");
                                        txtBtnEndgame.pad(5, 30, 5, 30);
                                        button(txtBtnEndgame, 1L).pad(30);
                                    } else {
                                        TextButton txtBtnEndgame = new TextButton("Enigme suivante", game.skin, "title");
                                        txtBtnEndgame.pad(5, 30, 5, 30);
                                        button(txtBtnEndgame, 2L).pad(30);
                                    }

                                }

                                @Override
                                protected void result(Object object) {
                                    if (object.equals(1L)) {//fin de partie
                                        // Changement d'écran pour revenir au menu principal
                                        game.switchScreen(new EndGameScreen(game, tpsRestant, tpsInitial, enigmeManager.getEnigmes(), game.getGameSocket().getSocketType() == GameSocket.GameSocketConstant.HOST));
                                    } else if (object.equals(2L)) {//enigme suivante
                                        enigmeManager.nextEnigme();
                                        //gère la couleur des boutons
                                        indiceButton.setColor(Color.LIGHT_GRAY);
                                        solutionButton.setColor(Color.LIGHT_GRAY);
                                    }
                                }
                            }.show(stage);
                        }
                    });
                }
            }
        });
    }

    protected void finDePartie(boolean repTrouve, boolean timer) {//suivant
        // Changement d'écran pour revenir au menu principal
        if (repTrouve || timer) {
            isOver = true;
            String dialogTitle = "Bonne réponse";
            if (timer) {
                dialogTitle = "Timer fini";
            }
            new Dialog(dialogTitle, game.skin) {
                {
                    String texteContenu = "";
                    //mise à jour tps utilisé
                    tpsInitialEnigme = tpsRestant;
                    enigmeManager.setTpsUtilise(tpsInitialEnigme);
                    if (timer) {//tps écoulé
                        texteContenu = "Vous n'avez pas terminé à temps, la bombe a explosé !";
                        //effet sonore
                        sound = Gdx.audio.newSound(Gdx.files.internal("audio/bomb_exploding_sound_effect.mp3"));
                        sound.play(game.prefs.getFloat("volumeEffetSonore") / 100);
                        enigmeManager.setTpsUtilise(tpsInitialEnigme - tpsRestant);
                        stopTimer();

                        // Ajout du bouton Fin de partie dans la boîte de dialogue
                        TextButton txtBtnEndgame = new TextButton("Menu de fin de partie", game.skin, "title");
                        txtBtnEndgame.pad(5, 30, 5, 30);
                        button(txtBtnEndgame, 1L).pad(30);
                    } else {//énigme suivante
                        isOver = true;
                        texteContenu = "Vous avez trouvé la solution !";
                        game.getGameSocket().sendMessage("STATE::GOODEND");
                        //effet sonore
                        sound = Gdx.audio.newSound(Gdx.files.internal("audio/correct_sound_effect.mp3"));
                        sound.play(game.prefs.getFloat("volumeEffetSonore") / 100);
                        if (enigmeManager.isOver()) {//plus d'énigmes suivantes
                            // Ajout du bouton Fin de partie dans la boîte de dialogue
                            TextButton txtBtnEndgame = new TextButton("Menu de fin de partie", game.skin, "title");
                            txtBtnEndgame.pad(5, 30, 5, 30);
                            button(txtBtnEndgame, 1L).pad(30);
                            stopTimer();
                        } else {
                            // Ajout du bouton Enigme suivante dans la boîte de dialogue
                            TextButton txtBtnNextenigma = new TextButton("Enigme suivante", game.skin, "title");
                            txtBtnNextenigma.pad(5, 30, 5, 30);
                            button(txtBtnNextenigma, 2L).pad(30);
                        }
                    }
                    // Paramétrage du titre
                    getTitleLabel().setAlignment(Align.center);

                    // Label Message
                    Label lblMessage = new Label(texteContenu, game.skin, "title");
                    getContentTable().add(lblMessage).pad(30);
                }

                @Override
                protected void result(Object object) {
                    if (object.equals(1L)) {//fin de partie
                        // Changement d'écran pour revenir au menu principal
                        game.switchScreen(new EndGameScreen(game, tpsRestant, tpsInitial, enigmeManager.getEnigmes(), game.getGameSocket().getSocketType() == GameSocket.GameSocketConstant.HOST));
                    } else if (object.equals(2L)) {//enigme suivante
                        enigmeManager.nextEnigme();
                        //gère la couleur des boutons
                        indiceButton.setColor(Color.LIGHT_GRAY);
                        solutionButton.setColor(Color.LIGHT_GRAY);
                    }
                }
            }.show(stage);
        } else {
            new Dialog("Mauvaise réponse", game.skin) {
                {
                    // Paramétrage du titre
                    getTitleLabel().setAlignment(Align.center);

                    // Indication que l'autre équipe a trouvé la réponse
                    Label lblMessage = new Label("La réponse donnée n'est pas correcte !", game.skin, "title");
                    getContentTable().add(lblMessage).pad(30);

                    //effet sonore
                    sound = Gdx.audio.newSound(Gdx.files.internal("audio/wrong_sound_effect.mp3"));
                    sound.play(game.prefs.getFloat("volumeEffetSonore") / 100);

                    TextButton txtBtnRetour = new TextButton("Retour", game.skin, "title");
                    txtBtnRetour.pad(5, 30, 5, 30);
                    button(txtBtnRetour).pad(30);
                }
            }.show(stage);
        }
    }

    private final Timer.Task myTimerTask = new Timer.Task() {
        @Override
        public void run() {
            tpsRestant--;
            //gère la couleur des boutons
            if (tpsRestant == tpsInitialEnigme - enigmeManager.getTpsBeforeIndice()) {
                indiceButton.setColor(Color.WHITE);
            }
            if (tpsRestant == tpsInitialEnigme - enigmeManager.getTpsBeforeSolution()) {
                solutionButton.setColor(Color.WHITE);
            }
            if (tpsRestant == 30) {
                clignoteTimer();
                isUnder30s = true;
            }
            if (tpsRestant == 10) {
                stopClignoteTimer();
                clignoteRapideTimer();
            }
            if (!isUnder30s) {
                timerLabel.setText(secToMinSec(tpsRestant));
            }
            //temps écoulé
            if (tpsRestant == 0 && !isOver) {
                myTimerTask.cancel();
                finDePartie(false, true);
            }
        }
    };

    private final Timer.Task myTimerTask2 = new Timer.Task() {
        @Override
        public void run() {
            timerLabel.setText("");
            //temps écoulé
            if (tpsRestant == 0 && !isOver) {
                myTimerTask2.cancel();
            }
        }
    };

    private final Timer.Task myTimerTask3 = new Timer.Task() {
        @Override
        public void run() {
            timerLabel.setText(secToMinSec(tpsRestant));
            //temps écoulé
            if (tpsRestant == 0 && !isOver) {
                myTimerTask3.cancel();
            }
        }
    };

    public void startTimer() {
        Timer.schedule(myTimerTask, 1f, 1f);
    }

    public void clignoteTimer() {
        Timer.schedule(myTimerTask2, 0f, 1f);
        Timer.schedule(myTimerTask3, 0.1f, 1f);
        // Instanciation d'un effet sonore
        sound = Gdx.audio.newSound(Gdx.files.internal("audio/bomb timer.mp3"));
        sound.play(game.prefs.getFloat("volumeEffetSonore") / 100);
    }

    public void stopClignoteTimer() {
        myTimerTask2.cancel();
        myTimerTask3.cancel();
    }

    public void clignoteRapideTimer() {
        Timer.schedule(myTimerTask2, 0f, 0.5f);
        Timer.schedule(myTimerTask3, 0.1f, 0.5f);
        // Instanciation d'un effet sonore
        sound = Gdx.audio.newSound(Gdx.files.internal("audio/bomb timer.mp3"));
        sound.play(game.prefs.getFloat("volumeEffetSonore") / 100);
    }

    public void stopTimer() {
        myTimerTask.cancel();
        myTimerTask2.cancel();
        myTimerTask3.cancel();
    }
    
    //affichage des secondes en minutes et secondes
    public String secToMinSec(int sec) {
		int secondes=sec%60;
		int minutes=sec/60;
		return minutes+":"+secondes+" min";
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
    public void dispose() {
        stopTimer();
        enigmeManager.dispose();
        stage.dispose();
        sound.dispose();
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
