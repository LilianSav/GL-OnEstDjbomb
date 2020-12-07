package com.glhf.on_est_djbomb.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameGuestSocket extends GameSocket {
    public void init(String adresse, int port) {
        try {
            connexion = new Socket(InetAddress.getByName(adresse), port);
            output = new ObjectOutputStream(connexion.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connexion.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
