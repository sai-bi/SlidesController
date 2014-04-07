package com.example.slides_controller.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class ShowSlide extends Activity {

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_slide);

        final Button start_button = (Button) (findViewById(R.id.start_button));
        final Button end_button = (Button) (findViewById(R.id.end_button));
        final Button next_button = (Button) (findViewById(R.id.next_button));
        final Button previous_button = (Button) (findViewById(R.id.previous_button));
        final Button white_screen_button = (Button) (findViewById(R.id.white_screen_button));
        final Button black_screen_button = (Button) (findViewById(R.id.black_screen_button));

        Intent intent = getIntent();
        final String server_ip = intent.getStringExtra("server_ip");
        final int port_number = intent.getIntExtra("port_number", 0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(server_ip, port_number);
                } catch (Exception e) {
                    Log.d("Error", "Cannot connect to server...");
                }
            }
        }).start();

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.START_DISPLAY);
            }
        });
        end_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.END_DISPLAY);
            }
        });
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.NEXT_SLIDE);
            }
        });
        previous_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.PREVIOUS_SLIDE);
            }
        });
        black_screen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.BLACK_SCREEN);
            }
        });
        white_screen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.WHITE_SCREEN);
            }
        });
    }

    private void sendMessage(int message_code) {
        Log.d("send", "send messages");
        try {
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(Integer.toString(message_code) + ";" + "hello world");
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", "Cannot send message");
        }
    }


}
