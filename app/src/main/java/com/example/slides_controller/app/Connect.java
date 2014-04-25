package com.example.slides_controller.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

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
        final RadioGroup role_radio_group;
        final EditText password_input;
        final EditText server_ip_input;
        final EditText port_num_input;


        role_id = Command.WATCHER;
        connect_btn = (Button) (findViewById(R.id.connect_btn));
        role_radio_group = (RadioGroup) (findViewById(R.id.role_group));
        password_input = (EditText) (findViewById(R.id.password));
        server_ip_input = (EditText) (findViewById(R.id.server_ip_input));
        port_num_input = (EditText) (findViewById(R.id.port_input));

        role_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (role_id == Command.SPEAKER) {
                    // set gone
                    password_input.setVisibility(View.GONE);
                    role_id = Command.WATCHER;
                } else {
                    // set visible
                    password_input.setVisibility(View.VISIBLE);
                    role_id = Command.SPEAKER;
                }
            }
        });

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);


        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                server_ip = "202.189.127.221";
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
