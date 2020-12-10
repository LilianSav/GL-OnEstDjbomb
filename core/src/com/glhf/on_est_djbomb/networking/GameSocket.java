package com.glhf.on_est_djbomb.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class GameSocket {
    protected ObjectOutputStream outputText;
    protected ObjectInputStream inputText;
    protected Socket connexionText;
    protected boolean isInitialized;
    protected String identifiant;
    protected String remoteIdentidiant;

    public GameSocket(String identifiant){
        this.isInitialized = false;
        this.identifiant = identifiant;
    }

    // Réception d'une chaîne de caractères
    public String receiveMessage() {
        String textMessage = "";
        try {
            textMessage = (String) inputText.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return textMessage;
    }

    // Envoie d'une chaîne de caractères
    public void sendMessage(String textMessage) {
        try {
            outputText.writeObject(textMessage);
            outputText.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fermeture des flux (Socket, InputStream et OutputStream)
    public void close() {
        try {
            // Si le socket s'est correctement initialisé
            if(isInitialized){
                // Fermeture Socket et Stream pour l'échange textuelle
                outputText.close();
                inputText.close();
                connexionText.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract String getInfoSocket();

    public String getIdentifiant(){
        return identifiant;
    }

    public String getRemoteIdentifiant(){
        return remoteIdentidiant;
    }
}
