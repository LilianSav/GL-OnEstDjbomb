package com.glhf.on_est_djbomb.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class GameSocket {
    protected ObjectOutputStream output;
    protected ObjectInputStream input;
    protected Socket connexion;

    // Reception d'une chaîne de caractères
    public String receiveString() {
        String message = "";
        try {
            message = (String) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return message;
    }

    // Envoie d'une chaîne de caractères
    public void sendString(String message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        try {
            output.close();
            input.close();
            connexion.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
