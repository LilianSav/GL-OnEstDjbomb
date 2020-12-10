package com.glhf.on_est_djbomb.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

public class GameHostSocket extends GameSocket {
    ServerSocket server;

    public void init() {
        try {
            server = new ServerSocket(0);
            connexion = server.accept();
            output = new ObjectOutputStream(connexion.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connexion.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return server.getLocalPort();
    }

    public void close() {
        super.close();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
