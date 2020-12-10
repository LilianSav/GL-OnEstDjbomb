package com.glhf.on_est_djbomb.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.glhf.on_est_djbomb.OnEstDjbombGame;

public class GameScreen implements Screen {
    private final Stage stage;
    private Texture enigmeImageTexture;

    public GameScreen(OnEstDjbombGame game) {
        // Instanciation du stage (Hiérarchie de nos acteurs)
        stage = new Stage(new ScreenViewport());
        // Liaison des Inputs au stage
        Gdx.input.setInputProcessor(stage);

        // Instanciation d'une table pour contenir nos Layouts (Énigmes, UI, Chat)
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Ajout d'une table pour l'énigme
        Table enigmeTable;
        if ( game.getGameSocket().getIdentifiant().equals("Host") ) {
            enigmeTable = getEnigmeTableHost();
        } else {
            enigmeTable = getEnigmeTableGuest();
        }
        root.add(enigmeTable).width(Value.percentWidth(0.70f, root)).height(Value.percentHeight(0.70f, root));
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

        // Création interface utilisateur
        TextButton quitterButton = new TextButton("Quitter", game.skin);
        Label timerLabel = new Label("30:00", game.skin);
        TextButton indiceButton = new TextButton("Indice", game.skin);
        TextButton solutionButton = new TextButton("Solution", game.skin);
        TextField verificationTextField = new TextField("", game.skin);
        TextButton verificationbutton = new TextButton("Ok", game.skin);

        userInterfaceTable.add(quitterButton).expand().colspan(2);
        userInterfaceTable.row();
        userInterfaceTable.add(timerLabel).expand().colspan(2);
        userInterfaceTable.row();
        userInterfaceTable.add(indiceButton).expand();
        userInterfaceTable.add(solutionButton).expand();
        userInterfaceTable.row();
        userInterfaceTable.add(verificationTextField).expand();
        userInterfaceTable.add(verificationbutton).expand();

        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Changement texte
                chatTextLabel.setText(chatTextLabel.getText() + "\n" + game.getGameSocket().getIdentifiant() + " - " + chatTextField.getText());
                game.getGameSocket().sendMessage("TEXT::" + chatTextField.getText());
                chatTextField.setText("");
            }
        });
        quitterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
                if (verificationTextField.getText().equals("2865")) {
                    game.getGameSocket().sendMessage("STATE::GOODEND");
                    new Dialog("Bonne reponse", game.skin){
                        {
                            text("Bonne reponse !");
                            button("Retour au menu principal");
                        }

                        @Override
                        protected void result(Object object) {
                            // Fermeture des flux
                            game.getGameSocket().close();
                            // Changement d'écran pour revenir au menu principal
                            game.switchScreen(new MainMenuScreen(game));
                        }
                    }.show(stage);
                } else {
                    new Dialog("Mauvaise reponse", game.skin){
                        {
                            text("La reponse donnee n'est pas correcte");
                            button("Retour");
                        }

                        @Override
                        protected void result(Object object) {

                        }
                    }.show(stage);
                }
            }
        });

        // Gestion Message et Game State
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean stop = false;
                String message = "";

                while(!stop){
                    message = game.getGameSocket().receiveMessage();
                    String[] tokens = message.split("::");

                    // Message destiné au chat textuel
                    if(tokens[0].equals("TEXT")){
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                chatTextLabel.setText(chatTextLabel.getText() + "\n" + game.getGameSocket().getRemoteIdentifiant() + " - " + tokens[1]);
                            }
                        });
                    }
                    // Message destiné à l'état de jeu
                    else if(tokens[0].equals("STATE")){
                        if(tokens[1].equals("GOODEND")){
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    new Dialog("Bonne reponse", game.skin){
                                        {
                                            text("Vos coequipiers ont trouves la bonne reponse !");
                                            button("Retour au menu principal");
                                        }

                                        @Override
                                        protected void result(Object object) {
                                            // Fermeture des flux
                                            game.getGameSocket().close();
                                            // Changement d'écran pour revenir au menu principal
                                            game.switchScreen(new MainMenuScreen(game));
                                        }
                                    }.show(stage);
                                }
                            });
                        }
                    }
                    // Message indéfini, on arrête la boucle
                    else {
                        stop = true;
                    }
                }
            }
        }).start();

    }

    private Table getEnigmeTableHost() {
        enigmeImageTexture = new Texture(Gdx.files.internal("assetEnigme/trajet/trajetHost.png"));
        Image enigmeImageWidget = new Image(enigmeImageTexture);
        Table enigmeTableHost = new Table();
        enigmeTableHost.add(enigmeImageWidget);

        return enigmeTableHost;
    }

    private Table getEnigmeTableGuest() {
        enigmeImageTexture = new Texture(Gdx.files.internal("assetEnigme/trajet/trajetGuest.png"));
        Image enigmeImageWidget = new Image(enigmeImageTexture);
        Table enigmeTableHost = new Table();
        enigmeTableHost.add(enigmeImageWidget);

        return enigmeTableHost;
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
        enigmeImageTexture.dispose();
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
