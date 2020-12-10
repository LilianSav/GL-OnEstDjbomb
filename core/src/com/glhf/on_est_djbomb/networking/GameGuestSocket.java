package com.glhf.on_est_djbomb.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class GameGuestSocket extends GameSocket {

    public GameGuestSocket(String identifiant) {
        super(identifiant);
    }

    public void init(String adresse, int port) {
        try {
            // Socket Text
            connexionText = new Socket(InetAddress.getByName(adresse), port);
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

    @Override
    public String getInfoSocket() {
        String infoSocket = "";
        // Si le socket est connecté
        if (connexionText.isConnected()) {
            infoSocket = "Connected to " + connexionText.getRemoteSocketAddress();
        }
        // Si le socket n'est pas connecté
        else {
            infoSocket = "Disconnected";
        }
        return infoSocket;
    }
}
