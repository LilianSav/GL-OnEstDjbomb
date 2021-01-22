package com.glhf.on_est_djbomb.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class GameGuestSocket extends GameSocket {

    // Constructeur
    public GameGuestSocket(String identifiant) {
        super(identifiant);
    }

    // Initialise la connexion avec Host
    public void init(String adresse, int port) {
        // Initialisation de la connexion (Guest) et des Streams
        try {
            connexion = new Socket(InetAddress.getByName(adresse), port);
            outputStream = new ObjectOutputStream(connexion.getOutputStream());
            outputStream.flush();
            intputStream = new ObjectInputStream(connexion.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // On modifie l'état de la connexion
        running = true;
        socketType = GameSocketConstant.GUEST;

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

    /** Modifié, à vérifier **/
/*
    @Override
    public String getInfoSocket() {
        String infoSocket = "";
        // Si le socket est connecté
        if (connexion.isConnected()) {
            infoSocket = "Connecté à " + connexion.getRemoteSocketAddress();
        }
        // Si le socket n'est pas connecté
        else {
            infoSocket = "Déconnecté";
        }
        return infoSocket;
    }
    */
    @Override
    public String getInfoIp() {
        String infoIpAdress = "";
        if (connexion.isConnected()) {
            infoIpAdress = "" + connexion.getInetAddress().getHostAddress();
            //infoIpAdress = "" + (((InetSocketAddress) connexion.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
        } else {
            infoIpAdress = "Déconnecté";
        }
        return infoIpAdress;
    }

    @Override
    public String getInfoPort(){
        String infoPort = "";
        if (connexion.isConnected()) {
            infoPort = "" + connexion.getPort();
            //infoPort = "" + (((InetSocketAddress) connexion.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
        } else {
            infoPort = "Déconnecté";
        }
        return infoPort;
    }
}
