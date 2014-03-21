package com.example.slides_controller.app;

import com.example.slides_controller.app.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.net.Socket;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Connect extends Activity {

    // 0 for watcher, 1 for talker
    private int role_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);


        final Button connect_btn;
        final RadioGroup role_radio_group;
        final EditText password_input;
        final EditText server_ip;
        final EditText port_num;

        role_id = 0;
        //talker_btn = (RadioButton)(findViewById(R.id.talker_btn));
        //watcher_btn = (RadioButton)(findViewById(R.id.watcher_btn));
        connect_btn = (Button)(findViewById(R.id.connect_btn));
        role_radio_group = (RadioGroup)(findViewById(R.id.role_group));
        password_input = (EditText)(findViewById(R.id.password));
        server_ip = (EditText)(findViewById(R.id.server_ip_input));
        port_num = (EditText)(findViewById(R.id.port_input));

        role_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == 0){
                    // set gone
                    password_input.setVisibility(2);
                    role_id = 0;
                }else{
                    // set visible
                    password_input.setVisibility(0);
                    role_id = 1;
                }
            }
        });

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(server_ip.getText() == null){
                    return;
                }
                else if(port_num.getText() == null){
                    return;
                }
                else if(role_id == 1 && password_input.getText() == null){
                    return;
                }

                try{
                    String server_dst = server_ip.getText().toString();
                    int port = Integer.parseInt(port_num.getText().toString());
                    Socket client = new Socket(server_dst,port);

                }catch(Exception e){
                    // cannot establish connection
                }
            }
        });
    }






























}
