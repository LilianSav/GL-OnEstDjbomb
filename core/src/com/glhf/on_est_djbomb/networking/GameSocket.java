package com.glhf.on_est_djbomb.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public abstract class GameSocket {
    // Socket pour connexion distante
    protected Socket connexion;
    // Stream pour envoyer et recevoir des objets (String)
    protected ObjectOutputStream outputStream;
    protected ObjectInputStream intputStream;
    // Identifiants
    protected String identifiant;
    protected String remoteIdentidiant;
    // Gestion de l'état de la connexion
    protected boolean running;
    protected GameSocketConstant socketType;

    public enum GameSocketConstant {
        HOST,
        GUEST
    }

    // Gestion des listeners
    protected final ArrayList<SocketListener> listeners = new ArrayList<>();
    // Gestion de synchronization
    protected final Object lockRunning;
    protected final Object lockListeners;

    // Constructeur
    public GameSocket(String identifiant) {
        this.identifiant = identifiant;
        this.remoteIdentidiant = "Connexion en attente...";
        lockRunning = new Object();
        lockListeners = new Object();
    }

    // Réception d'une chaîne de caractères
    public synchronized String receiveMessage() {
        String textMessage = "";
        try {
            textMessage = (String) intputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return textMessage;
    }

    // Envoie d'une chaîne de caractères
    public void sendMessage(String textMessage) {
        try {
            // On vérifie que l'hôte distant est bien connecté
            if(getRunning()){
                outputStream.writeObject(textMessage);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fermeture des flux (Socket, InputStream et OutputStream)
    public void close() {
        clearListeners();
        try {
            // Si le socket s'est connecté à un hôte distant, on ferme les flux
            // Sinon l'initialisation ne s'est pas réalisée et il ne faut pas appeler les fonctions de fermeture
            if (getRunning()) {
                // On met fin à la boucle d'échanges
                setRunning(false);
                // Fermeture du Socket et des Stream
                outputStream.close();
                intputStream.close();
                connexion.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fonction à implémenter dans les sous-classes pour afficher les informations pertinentes du Socket
    public abstract String getInfoSocket();

    // Ajoute un SocketListener à notre liste
    public void addListener(SocketListener listener) {
        synchronized (lockListeners) {
            listeners.add(listener);
        }
    }

    // Vide notre liste de SocketListener
    public void clearListeners() {
        synchronized (lockListeners) {
            listeners.clear();
        }
    }

    // Notifie tous les SocketListener pour qu'il traite l'événement
    public void notifyListeners(String eventMessage) {
        ArrayList<SocketListener> listenersCopy;
        synchronized (lockListeners) {
            listenersCopy = new ArrayList<>(listeners);
        }
        listenersCopy.forEach(listener -> listener.update(eventMessage));
    }

    // Getters et Setters
    public String getIdentifiant() {
        return identifiant;
    }

    public String getRemoteIdentifiant() {
        return remoteIdentidiant;
    }

    public boolean getRunning() {
        synchronized (lockRunning) {
            return running;
        }
    }

    public void setRunning(boolean running) {
        synchronized (lockRunning) {
            this.running = running;
        }
    }

    public GameSocketConstant getSocketType(){
        return this.socketType;
    }
}
