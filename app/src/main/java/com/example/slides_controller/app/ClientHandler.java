package com.example.slides_controller.app;

import android.util.Log;

import com.example.slides_controller.app.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

/**
 * Created by saibi on 3/21/14.
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader buffered_reader;
    private ObjectInputStream object_input;

    public ClientHandler(Socket s){
        socket = s;
        try {
            object_input = new ObjectInputStream(socket.getInputStream());
        }catch(Exception e) {
            Log.e("connection", "connection error in com.example.slides_controller.app.ClientHandler");
        }
    }
    @Override
    public void run() {
        Message input_message;
        try{
            while(true){
                input_message = (Message)(object_input.readObject());
                if(input_message == null){
                    continue;
                }
                int operation = input_message.getOperation();
                switch(operation){
                    case 0:
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
        }catch(Exception e){
            Log.e("connection","connection error in receiving message");
        }

    }
}
