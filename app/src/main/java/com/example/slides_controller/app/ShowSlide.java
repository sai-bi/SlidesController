package com.example.slides_controller.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.net.Socket;


public class ShowSlide extends Activity {

    private Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_slide);
        Intent intent = getIntent();
        String server_dst = intent.getStringExtra("SERVER_DST");
        int port = intent.getIntExtra("PORT_NUM",0);
        try{
            socket = new Socket(server_dst,port);
            Thread t = new Thread();

        }catch(Exception e){
            Log.e("connection", "Cannot connect to server!");
        }
    }


}
