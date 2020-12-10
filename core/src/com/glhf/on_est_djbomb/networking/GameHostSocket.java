package com.glhf.on_est_djbomb.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class GameHostSocket extends GameSocket {
    public ServerSocket server;

    public GameHostSocket(String identifiant) {
        super(identifiant);

        try {
            server = new ServerSocket(0, 50, InetAddress.getLocalHost());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try {
            // Socket Text
            connexionText = server.accept();
            outputText = new ObjectOutputStream(connexionText.getOutputStream());
            outputText.flush();
            inputText = new ObjectInputStream(connexionText.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendMessage(identifiant);
        new Thread( () -> {
            remoteIdentidiant = receiveMessage();
        }).start();

        isInitialized = true;
    }

    // Fermeture des flux
    public void close() {
        super.close();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getInfoSocket() {
        return server.getInetAddress().getHostAddress() + ":" + server.getLocalPort();
    }
}
