package com.example.slides_controller.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.Socket;


public class Connect extends Activity {


    private int role_id;
    private String server_ip;
    private int port_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);


        final Button connect_btn;
        final TextView speaker_text_view;
        final TextView audience_text_view;
        final EditText password_input;
        final EditText server_ip_input;
        final EditText port_num_input;


        role_id = Command.WATCHER;
        connect_btn = (Button) (findViewById(R.id.connect_btn));


        server_ip_input = (EditText) (findViewById(R.id.server_ip_input));
        port_num_input = (EditText) (findViewById(R.id.port_input));
        speaker_text_view = (TextView) (findViewById(R.id.speaker));
        audience_text_view = (TextView) (findViewById(R.id.audience));

        speaker_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speaker_text_view.setBackgroundResource(R.drawable.rectangle);
                audience_text_view.setBackgroundResource(R.drawable.rectangle1);
                speaker_text_view.setTextColor(Color.parseColor("#FFFFFF"));
                audience_text_view.setTextColor(Color.parseColor("#ACACAD"));
                role_id = Command.SPEAKER;

            }
        });

        audience_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audience_text_view.setBackgroundResource(R.drawable.rectangle);
                speaker_text_view.setBackgroundResource(R.drawable.rectangle1);
                audience_text_view.setTextColor(Color.parseColor("#FFFFFF"));
                speaker_text_view.setTextColor(Color.parseColor("#ACACAD"));
                role_id = Command.WATCHER;
            }
        });






        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);


        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                server_ip = "202.189.127.46";
                port_number = 52315;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket(server_ip, port_number);
                            // check password
                            Command.server_socket = socket;
                            Intent intent = new Intent(Connect.this, ShowSlide.class);
                            intent.putExtra("server_ip", server_ip);
                            intent.putExtra("port_number", port_number);
                            intent.putExtra("role", role_id);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("error", "cannot connect to server");
                        }
                    }
                }).start();
            }
        });


    }

}
