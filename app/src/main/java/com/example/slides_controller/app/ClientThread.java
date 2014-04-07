package com.example.slides_controller.app;

import java.net.Socket;

/**
 * Created by saibi on 3/21/14.
 */
public class ClientThread implements Runnable{
    private Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        Thread t = new Thread(new ClientHandler(socket));
        t.start();
    }
}
