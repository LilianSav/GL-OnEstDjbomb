package com.glhf.on_est_djbomb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.glhf.on_est_djbomb.OnEstDjbombGame;
import com.glhf.on_est_djbomb.dialogs.OptionsDialog;
import com.glhf.on_est_djbomb.enigmas.EnigmaManager;
import com.glhf.on_est_djbomb.networking.GameSocket;

public class GameScreen implements Screen {
    private final Stage stage;
    private final Sound sound;
    private final Label timerLabel;
    private int tpsRestant;
    private final EnigmaManager enigmeManager;

    public GameScreen(OnEstDjbombGame game) {
        // Instanciation du stage (Hiérarchie de nos acteurs)
        stage = new Stage(new ScreenViewport());
        // Liaison des Inputs au stage
        Gdx.input.setInputProcessor(stage);

        // Instanciation d'un effet sonore
        sound = Gdx.audio.newSound(Gdx.files.internal("audio/bomb_has_been_planted.mp3"));
        sound.play(game.prefs.getFloat("volumeEffetSonore") / 100);

        // Instanciation du gestionnaire d'énigmes
        if (game.getGameSocket().getSocketType() == GameSocket.GameSocketConstant.HOST) {
            enigmeManager = new EnigmaManager(true, game, stage);
        } else {
            enigmeManager = new EnigmaManager(false, game, stage);
        }


        // Instanciation d'une table pour contenir nos Layouts (Énigmes, UI, Chat)
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Ajout d'une table pour l'énigme
        root.add(enigmeManager).width(Value.percentWidth(0.70f, root)).height(Value.percentHeight(0.70f, root));
        // Ajout d'une table pour l'interface utilisateur
        Table userInterfaceTable = new Table();
        root.add(userInterfaceTable).width(Value.percentWidth(0.20f, root)).height(Value.percentHeight(0.70f, root));
        // Ajout d'une table pour le chat textuel
        Table textChatTable = new Table();
        root.row();
        root.add(textChatTable).colspan(2).width(Value.percentWidth(0.9f, root)).height(Value.percentHeight(0.20f, root));

        // Création chat textuel
        TextField chatTextField = new TextField("", game.skin);
        TextButton sendButton = new TextButton("Send", game.skin);
        Label chatTextLabel = new Label("En attente de message ...", game.skin);
        ScrollPane scrollPaneText = new ScrollPane(chatTextLabel, game.skin);

        textChatTable.add(scrollPaneText).colspan(2).growX().height(Value.percentHeight(0.70f, textChatTable));
        textChatTable.row();
        textChatTable.add(chatTextField).width(Value.percentWidth(0.80f, textChatTable)).height(Value.percentHeight(0.20f, textChatTable));
        textChatTable.add(sendButton).width(Value.percentWidth(0.20f, textChatTable)).height(Value.percentHeight(0.20f, textChatTable));

        // Création interface utilisateur latérale
        TextButton optionsButton = new TextButton("Options", game.skin);
        OptionsDialog optionsDialog = new OptionsDialog("Options", game);
        optionsDialog.initContent();
        TextButton quitterButton = new TextButton("Quitter", game.skin);
        tpsRestant = 30;
        timerLabel = new Label(tpsRestant + " sec", game.skin);
        startTimer();
        TextButton indiceButton = new TextButton("Indice", game.skin);
        TextButton solutionButton = new TextButton("Solution", game.skin);
        TextField verificationTextField = new TextField("", game.skin);
        TextButton verificationbutton = new TextButton("Ok", game.skin);

        userInterfaceTable.add(optionsButton).expand();
        userInterfaceTable.add(quitterButton).expand();
        userInterfaceTable.row();
        userInterfaceTable.add(timerLabel).expand().colspan(2);
        userInterfaceTable.row();
        userInterfaceTable.add(indiceButton).expand();
        userInterfaceTable.add(solutionButton).expand();
        userInterfaceTable.row();
        userInterfaceTable.add(verificationTextField).expand();
        userInterfaceTable.add(verificationbutton).expand();

        // Gestion des événements
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Changement texte
                chatTextLabel.setText(chatTextLabel.getText() + "\n" + game.getGameSocket().getIdentifiant() + " - " + chatTextField.getText());
                game.getGameSocket().sendMessage("TEXT::" + chatTextField.getText());
                chatTextField.setText("");
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
                // Changement d'écran pour revenir au menu principal
                if (verificationTextField.getText().equals(String.valueOf(enigmeManager.getSolution()))) {
                    game.getGameSocket().sendMessage("STATE::GOODEND");
                    new Dialog("Bonne reponse", game.skin) {
                        {
                            text("Bonne reponse !");
                            if (enigmeManager.isOver()) {
                                button("Retour au menu principal", 1L);
                            } else {
                                button("Enigme suivante", 2L);
                            }

                        }

                        @Override
                        protected void result(Object object) {
                            if (object.equals(1L)) {
                                // On vide le gestionnaire de listeners
                                game.getGameSocket().clearListeners();
                                // Fermeture des flux
                                game.getGameSocket().close();
                                // Changement d'écran pour revenir au menu principal
                                game.switchScreen(new MainMenuScreen(game));
                            } else if (object.equals(2L)) {
                                enigmeManager.nextEnigme();
                            }
                        }
                    }.show(stage);
                } else {
                    new Dialog("Mauvaise reponse", game.skin) {
                        {
                            text("La reponse donnee n'est pas correcte");
                            button("Retour");
                        }
                    }.show(stage);
                }
            }
        });
        indiceButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                enigmeManager.getIndice();
            }
        });

        // Gestion Message et Game State
        game.getGameSocket().addListener(eventMessage -> {
            // Parse
            String[] tokens = eventMessage.split("::");

            // FLAG TEXT : On modifie le contenu du chat textuel
            if (tokens[0].equals("TEXT")) {
                Gdx.app.postRunnable(() -> chatTextLabel.setText(chatTextLabel.getText() + "\n" + game.getGameSocket().getRemoteIdentifiant() + " - " + tokens[1]));
            }
            // FLAG STATE : On modifie l'état du jeu
            else if (tokens[0].equals("STATE")) {
                if (tokens[1].equals("GOODEND")) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            new Dialog("Bonne reponse", game.skin) {
                                {
                                    text("Vos coequipiers ont trouves la bonne reponse !");
                                    if (enigmeManager.isOver()) {
                                        button("Retour au menu principal", 1L);
                                    } else {
                                        button("Enigme suivante", 2L);
                                    }

                                }

                                @Override
                                protected void result(Object object) {
                                    if (object.equals(1L)) {
                                        // On vide le gestionnaire de listeners
                                        game.getGameSocket().clearListeners();
                                        // Fermeture des flux
                                        game.getGameSocket().close();
                                        // Changement d'écran pour revenir au menu principal
                                        game.switchScreen(new MainMenuScreen(game));
                                    } else if (object.equals(2L)) {
                                        enigmeManager.nextEnigme();
                                    }
                                }
                            }.show(stage);
                        }
                    });
                }
            }
            // Flag non reconnu
            else {
                Gdx.app.log("SocketFlagError", "Le flag envoyé par le socket distant n'est pas reconnu");
            }
        });
    }

    private final Timer.Task myTimerTask = new Timer.Task() {
        @Override
        public void run() {
            tpsRestant--;
            timerLabel.setText(tpsRestant + " sec");
            if (tpsRestant == 0) {
                myTimerTask.cancel();
            }
        }
    };

    public void startTimer() {
        Timer.schedule(myTimerTask, 1f, 1f);
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
