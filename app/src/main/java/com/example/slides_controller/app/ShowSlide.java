package com.example.slides_controller.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ShowSlide extends Activity {

    private Socket socket;
    private ObjectOutputStream ops;
    private ObjectInputStream ois;

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
        final Button pen_button = (Button) (findViewById(R.id.pen_button));
        final Button laser_button = (Button) (findViewById(R.id.laser_button));
        final Button highlighter_button = (Button) (findViewById(R.id.highlighter_button));
        final LinearLayout menu_layout = (LinearLayout) (findViewById(R.id.menu_layout));


        Intent intent = getIntent();
        final String server_ip = intent.getStringExtra("server_ip");
        final int port_number = intent.getIntExtra("port_number", 0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(server_ip, port_number);
                    ops = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                    listenToServer();
                } catch (Exception e) {
                    Log.d("Error", "Cannot connect to server...");
                }

            }
        }).start();

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.START_DISPLAY);
                menu_layout.setVisibility(View.GONE);
            }
        });
        end_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.END_DISPLAY);
                menu_layout.setVisibility(View.GONE);
            }
        });
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.NEXT_SLIDE);
                menu_layout.setVisibility(View.GONE);
            }
        });
        previous_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.PREVIOUS_SLIDE);
                menu_layout.setVisibility(View.GONE);
            }
        });
        black_screen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.BLACK_SCREEN);
                menu_layout.setVisibility(View.GONE);
            }
        });
        white_screen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.WHITE_SCREEN);
                menu_layout.setVisibility(View.GONE);
            }
        });

        pen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.PEN);
                menu_layout.setVisibility(View.GONE);
            }
        });

        laser_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.LASER);
                menu_layout.setVisibility(View.GONE);
            }
        });

        highlighter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.HIGHLIGHT);
                menu_layout.setVisibility(View.GONE);
            }
        });

    }

    private void sendMessage(int message_code) {
        Log.d("send", "send messages");
        try {
            Message message = new Message();
            message.setOperation(1);
            ops.writeObject(message);
            ops.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", "Cannot send message");
        }
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        if (keycode == KeyEvent.KEYCODE_MENU) {
            LinearLayout menu_layout = (LinearLayout) (findViewById(R.id.menu_layout));
            menu_layout.setVisibility(View.VISIBLE);
        }

        return true;
    }

    private void listenToServer() {
        try {
            while (true) {
                Message message = (Message) (ois.readObject());
                Log.d("Receive", "receive a message");
                switch (message.getOperation()) {
                    case Command.IMAGE:
                        displayImage(message);
                        break;
                    default:
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Receive error", "Error in receiving message");
        }
    }

    private void displayImage(Message message) {
        byte[] image = message.getImageByteArray();
        final Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        final ImageView slides_image_view = (ImageView) (findViewById(R.id.slides_image_view));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                slides_image_view.setImageBitmap(bitmap);
            }
        });

    }
}
