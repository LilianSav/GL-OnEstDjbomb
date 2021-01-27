package com.glhf.on_est_djbomb.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;

public class GameHostSocket extends GameSocket {
    // ServerSocket pour héberger la partie
    public ServerSocket server;

    // Constructeur
    public GameHostSocket(String identifiant) {
        super(identifiant);

        try {
            server = new ServerSocket(0, 50, InetAddress.getLocalHost());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initialise la connexion avec Guest
    public void init() {
        // Initialisation de la connexion (Host) et des Streams
        try {
            connexion = server.accept();
            outputStream = new ObjectOutputStream(connexion.getOutputStream());
            outputStream.flush();
            intputStream = new ObjectInputStream(connexion.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // On modifie l'état de la connexion
        running = true;
        socketType = GameSocketConstant.HOST;

        // Premier contact pour récupérer les identifiants
        sendMessage(identifiant);
        remoteIdentidiant = receiveMessage();


        // On délègue la lecture des flux entrants à un Thread appellant des listeners
        new Thread(() -> {
            // On observe les messages arrivant
            String message = "";
            while (getRunning()) {
                try {
                    message = (String) intputStream.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    setRunning(false);
                }
                if(!message.equals("")){
                    notifyListeners(message);
                    message = "";
                }
            }
        }).start();
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
    public String getInfoIp(){
        return server.getInetAddress().getHostAddress();
    }
    @Override
    public String getInfoPort(){
        return (""+server.getLocalPort());
    }
}
