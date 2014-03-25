package com.example.slides_controller.app;

import android.util.Log;

import java.net.Socket;

/**
 * Created by saibi on 3/21/14.
 */
public class ClientThread implements Runnable{
    private Socket socket;
    public ClientThread(String server_ip, int port){
        try{
            socket = new Socket(server_ip,port);
        }catch (Exception e){
            Log.d("connection","Connection error in com.example.slides_controller.app.ClientThread");
        }
    }
    @Override
    public void run() {
        Thread t = new Thread(new ClientHandler(socket));
        t.start();
    }
}
