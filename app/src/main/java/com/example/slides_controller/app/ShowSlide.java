package com.example.slides_controller.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class ShowSlide extends Activity {

    private Socket socket;
    private ObjectOutputStream ops;
    private ObjectInputStream ois;
    private boolean pen_mode;
    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;
    private int screen_width;
    private int screen_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_slide);

        Intent intent = getIntent();
        final String server_ip = intent.getStringExtra("server_ip");
        final int port_number = intent.getIntExtra("port_number", 0);
        socket = Command.server_socket;
        pen_mode = true;
        canvas = new Canvas();
        bitmap = null;
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screen_height = displaymetrics.heightPixels;
        screen_width = displaymetrics.widthPixels;
        Log.d("width", Integer.toString(screen_width));
        Log.d("height", Integer.toString(screen_height));



        setButtonListener();
        setImageViewListener();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ops = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                    listenToServer();
                } catch (Exception e) {
                    Log.d("Error", "Cannot connect to server...");
                }

            }
        }).start();


    }


    private void setButtonListener() {
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
                pen_mode = true;
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

    private void setImageViewListener() {
        final ImageView slides_image_view = (ImageView) (findViewById(R.id.slides_image_view));
        final ArrayList<Float> line_x = new ArrayList<Float>();
        final ArrayList<Float> line_y = new ArrayList<Float>();


        slides_image_view.setOnTouchListener(new View.OnTouchListener() {
            float start_x;
            float start_y;
            ArrayList<Float> line_x;
            ArrayList<Float> line_y;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (pen_mode == false)
                    return true;
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (bitmap == null) {
                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
                            bitmap = Bitmap.createScaledBitmap(bitmap, screen_width, screen_height, false);
                            canvas.setBitmap(bitmap);
                        }
                        line_x = new ArrayList<Float>();
                        line_y = new ArrayList<Float>();
                        start_x = motionEvent.getX();
                        start_y = motionEvent.getY();
                        line_x.add(start_x);
                        line_y.add(start_y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float end_x = motionEvent.getX();
                        float end_y = motionEvent.getY();
                        canvas.drawLine(start_x, start_y, end_x, end_y, paint);
                        line_x.add(end_x);
                        line_y.add(end_y);
                        start_x = end_x;
                        start_y = end_y;
                        slides_image_view.setImageBitmap(bitmap);
                        break;
                    case MotionEvent.ACTION_UP:
                        Message message = new Message();
                        message.setOperation(Command.LINE);
                        message.setScreenHeight(screen_height);
                        message.setScreenWidth(screen_width);
                        message.setLine_x(line_x);
                        message.setLine_y(line_y);
                        sendMessageObject(message);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }


    private void sendMessage(int message_code) {
        Log.d("send", "send messages");
        try {
            Message message = new Message();
            message.setOperation(message_code);
            ops.writeObject(message);
            ops.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", "Cannot send message");
        }
    }

    private void sendMessageObject(Message message) {
        Log.d("send", "send message object");
        try {
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
            if (menu_layout.getVisibility() == View.VISIBLE) {
                menu_layout.setVisibility(View.GONE);
            } else {
                menu_layout.setVisibility(View.VISIBLE);
            }
        } else if (keycode == KeyEvent.KEYCODE_VOLUME_UP) {
            sendMessage(Command.PREVIOUS_SLIDE);
        } else if (keycode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            sendMessage(Command.NEXT_SLIDE);
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
                        System.out.println(message.getOperation());
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
        bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        final ImageView slides_image_view = (ImageView) (findViewById(R.id.slides_image_view));
        bitmap = Bitmap.createScaledBitmap(bitmap, screen_width, screen_height, false);
        canvas.setBitmap(bitmap);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                slides_image_view.setImageBitmap(bitmap);
            }
        });

    }

}
