package com.example.slides_controller.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ViewSwitcher;

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
    private int choice_number;
    private boolean vote_in_progress;
    private int chart_mode;
    private ArrayList<Integer> choice_count;
    private ArrayList<String> watcher_name_list;
    private ArrayList<Integer> watcher_id_list;
    private ArrayAdapter<String> watcher_listview_adapter;
    private int last_authorize_id;
    private boolean authorized;

    private Bitmap last_bitmap;
    private int role;
    // private List<Map<String, Object>> watcher_listview_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_slide);


        Intent intent = getIntent();
        final String server_ip = intent.getStringExtra("server_ip");
        final int port_number = intent.getIntExtra("port_number", 0);
        socket = Command.server_socket;
        pen_mode = false;

        role = intent.getIntExtra("role", Command.WATCHER);
        if (role == Command.SPEAKER) {
            authorized = true;
        } else {
            authorized = false;
        }

        choice_number = 2;
        vote_in_progress = false;
        chart_mode = Term.PIE_CHART;
        choice_count = new ArrayList<Integer>();

        watcher_name_list = new ArrayList<String>();
        watcher_id_list = new ArrayList<Integer>();
        last_authorize_id = -1;

        canvas = new Canvas();
        bitmap = null;
        last_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
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
        setWatcherListview();


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
        final Button highlighter_button = (Button) (findViewById(R.id.highlighter_button));
        final Button voting_button = (Button) (findViewById(R.id.voting_button));
        final Button start_voting_button = (Button) (findViewById(R.id.start_voting_button));

        final LinearLayout menu_layout = (LinearLayout) (findViewById(R.id.menu_layout));
        final LinearLayout slides_layout = (LinearLayout) (findViewById(R.id.slides_linear_layout));
        final LinearLayout chart_layout = (LinearLayout) (findViewById(R.id.chart_linear_layout));
        final Button pie_chart_button = (Button) (findViewById(R.id.pie_chart_button));
        final Button bar_chart_button = (Button) (findViewById(R.id.bar_chart_button));
        final Spinner choice_number_spinner = (Spinner) (findViewById(R.id.choice_num_spinner));

        final WebView chart_view = (WebView) (findViewById(R.id.chart_view));
        final Button exit_chart_button = (Button) (findViewById(R.id.exit_chart_button));
        final ScrollView menu_scroll_view = (ScrollView) (findViewById(R.id.menu_scroll_view));

        final Button menu_button = (Button) (findViewById(R.id.menu_button));
        final Button watcher_list_button = (Button) (findViewById(R.id.watcher_list_button));

        final ListView watcher_list_view = (ListView) (findViewById(R.id.watcher_list_view));
        final LinearLayout watcher_list_layout = (LinearLayout) (findViewById(R.id.watcher_list_layout));

        final Button clear_button = (Button) (findViewById(R.id.clear_button));
        final Button request_button = (Button) (findViewById(R.id.request_button));
//        final ImageView slides_image_view = (ImageView) (findViewById(R.id.slides_image_view));
        final ImageSwitcher slides_image_view = (ImageSwitcher) (findViewById(R.id.slides_image_view));


        if (role == Command.WATCHER) {
            request_button.setVisibility(View.VISIBLE);
        } else if (role == Command.SPEAKER) {
            watcher_list_button.setVisibility(View.VISIBLE);
        }

        request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.REQUEST);
            }
        });


        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.START_DISPLAY);
                menuLayoutGone();

            }
        });
        end_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.END_DISPLAY);
                menuLayoutGone();
                menuLayoutGone();
            }
        });
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.NEXT_SLIDE);
                menuLayoutGone();
            }
        });
        previous_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.PREVIOUS_SLIDE);
                menuLayoutGone();
            }
        });
        black_screen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.BLACK_SCREEN);
                menuLayoutGone();
            }
        });
        white_screen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.WHITE_SCREEN);
                menuLayoutGone();
            }
        });

        pen_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.PEN);
                menuLayoutGone();
                pen_mode = true;
            }
        });


        highlighter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(Command.HIGHLIGHT);
                menuLayoutGone();
            }
        });

        voting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLayoutGone();
                slides_layout.setVisibility(View.GONE);
                chart_layout.setVisibility(View.VISIBLE);
            }
        });

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_button.clearAnimation();
                watcher_list_view.setVisibility(View.GONE);
                int visible = menu_scroll_view.getVisibility();

                if (visible == View.VISIBLE) {
                    Log.d("click", "visible");
                    menu_scroll_view.setVisibility(View.GONE);
                    Animation slide = AnimationUtils.loadAnimation(ShowSlide.this, R.anim.menu_to_left);
                    menu_scroll_view.setAnimation(slide);
                } else {
                    Log.d("click", "invisible");
                    menu_scroll_view.setVisibility(View.VISIBLE);
                    Animation slide = AnimationUtils.loadAnimation(ShowSlide.this, R.anim.menu_to_right);
                    menu_scroll_view.setAnimation(slide);
                }
            }
        });

        watcher_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_scroll_view.setVisibility(View.GONE);
                int visible = watcher_list_view.getVisibility();

                if (visible == View.VISIBLE) {

                    watcher_list_view.setVisibility(View.GONE);
                    Animation slide = AnimationUtils.loadAnimation(ShowSlide.this, R.anim.menu_to_left);
                    watcher_list_view.setAnimation(slide);
                } else {
                    watcher_list_view.setVisibility(View.VISIBLE);
                    Animation slide = AnimationUtils.loadAnimation(ShowSlide.this, R.anim.menu_to_right);
                    watcher_list_view.setAnimation(slide);
                }
            }
        });


        choice_number_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choice_number = i + 2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                choice_number = 2;
            }
        });

        start_voting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("start vote...");
                Log.d("vote", "start vote");


                if (start_voting_button.getText().toString().equals("Start vote")) {
                    start_voting_button.setText("End vote");
                    vote_in_progress = true;
                    Message message = new Message();
                    message.setOperation(Command.CREATE_VOTE);
                    message.setVoteNum(choice_number);
                    sendMessageObject(message);
                    //sendMessage(Command.CREATE_VOTE);
                    choice_count.clear();
                    for (int i = 0; i < choice_number; i++) {
                        choice_count.add(0);
                    }
                } else {
                    start_voting_button.setText("Start vote");
                    vote_in_progress = false;
                }

            }
        });

        pie_chart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart_mode = Term.PIE_CHART;
            }
        });

        bar_chart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart_mode = Term.BAR_CHART;
            }
        });

        exit_chart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_voting_button.setText("Start vote");
                chart_mode = Term.PIE_CHART;
                vote_in_progress = false;
                menu_scroll_view.setVisibility(View.GONE);
                chart_layout.setVisibility(View.GONE);
                slides_layout.setVisibility(View.VISIBLE);
            }
        });

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // bisai
                //slides_image_view.setImageBitmap(last_bitmap);
                Drawable drawable = new BitmapDrawable(last_bitmap);
                slides_image_view.setImageDrawable(drawable);



                sendMessage(Command.CLEAR);
            }
        });
    }

    private void menuLayoutGone() {
        //LinearLayout menu_layout = (LinearLayout)(findViewById(R.id.menu_layout));
        ScrollView menu_scroll_view = (ScrollView) (findViewById(R.id.menu_scroll_view));
        if (menu_scroll_view.getVisibility() == View.GONE)
            return;
        menu_scroll_view.setVisibility(View.GONE);
        Animation slide = AnimationUtils.loadAnimation(ShowSlide.this, R.anim.menu_to_left);
        menu_scroll_view.setAnimation(slide);
    }

    private void watcherListViewGone() {
        ListView watcher_list_view = (ListView) (findViewById(R.id.watcher_list_view));
        if (watcher_list_view.getVisibility() == View.GONE)
            return;
        watcher_list_view.setVisibility(View.GONE);
        Animation slide = AnimationUtils.loadAnimation(ShowSlide.this, R.anim.menu_to_left);
        watcher_list_view.setAnimation(slide);
    }


    private void setImageViewListener() {
//        final ImageView slides_image_view = (ImageView) (findViewById(R.id.slides_image_view));
        // bisai
        final ImageSwitcher slides_image_view = (ImageSwitcher) (findViewById(R.id.slides_image_view));

        slides_image_view.setInAnimation(AnimationUtils.loadAnimation(ShowSlide.this, R.anim.slide_in));
        slides_image_view.setOutAnimation(AnimationUtils.loadAnimation(ShowSlide.this, R.anim.slide_out));
        slides_image_view.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(ShowSlide.this);

                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(screen_width, screen_height));
                return imageView;
            }
        });

        final ArrayList<Float> line_x = new ArrayList<Float>();
        final ArrayList<Float> line_y = new ArrayList<Float>();


        slides_image_view.setOnTouchListener(new View.OnTouchListener() {
            float start_x;
            float start_y;
            ArrayList<Float> line_x;
            ArrayList<Float> line_y;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                menuLayoutGone();
                watcherListViewGone();
                if (pen_mode == false || authorized == false) {
                    return true;
                }
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
                        Drawable drawable = new BitmapDrawable(bitmap);
                        slides_image_view.setImageDrawable(drawable);
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
        if (vote_in_progress == true) {
            return false;
        }
        if (keycode == KeyEvent.KEYCODE_MENU) {

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
                    case Command.LINE:
                        drawLine(message);
                        break;
                    case Command.VOTE_CHOICE:
                        if (vote_in_progress == false) {
                            break;
                        }
                        int choice = message.getChoice();
                        int temp = choice_count.get(choice);
                        choice_count.set(choice, temp + 1);
                        updateChart();
                        break;
                    case Command.CREATE_VOTE:
                        showVoteDialog(message.getVoteNum());
                        break;
                    case Command.CLIENTINFO:
                        watcher_id_list.add(message.getWatcher_id());
                        watcher_name_list.add(message.getWatcher_name());
                        watcher_listview_adapter.notifyDataSetChanged();
                        break;
                    case Command.AUTHORIZE:
                        authorized = true;
                        // start animation

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Button menu_button = (Button) (findViewById(R.id.menu_button));
                                Animation fade = AnimationUtils.loadAnimation(ShowSlide.this, R.anim.fade_in_out);
                                menu_button.startAnimation(fade);
                            }
                        });
                        break;
                    case Command.CANCEL_AUTHORIZE:
                        authorized = false;
                        break;
                    case Command.CLIENT_LOSE:
                        int id = message.getWatcher_id();
                        int index = watcher_id_list.indexOf(id);
                        if (index != -1) {
                            watcher_id_list.remove(index);
                            watcher_name_list.remove(index);
                        }
                        break;
                    case Command.CLEAR:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ImageSwitcher slides_image_view = (ImageSwitcher) (findViewById(R.id.slides_image_view));
                                slides_image_view.setImageDrawable(new BitmapDrawable(last_bitmap));
                            }
                        });
                        break;
                    case Command.EXIT:
                        finishActivity(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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

    private void drawLine(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Float> line_x = message.getLine_x();
                ArrayList<Float> line_y = message.getLine_y();
                int origin_width = message.getScreenWidth();
                int origin_height = message.getScreenHeight();
                float height_ratio = screen_height / (float) origin_height;
                float width_ratio = screen_width / (float) origin_width;
//                final ImageView slides_image_view = (ImageView) (findViewById(R.id.slides_image_view));
                // bisai
                final ImageSwitcher slides_image_view = (ImageSwitcher) (findViewById(R.id.slides_image_view));


                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
                    bitmap = Bitmap.createScaledBitmap(bitmap, screen_width, screen_height, false);
                    canvas.setBitmap(bitmap);
                }

                for (int i = 1; i < line_x.size() && i < line_y.size(); i++) {
                    canvas.drawLine(line_x.get(i - 1) * width_ratio, line_y.get(i - 1) * height_ratio, line_x.get(i) * width_ratio, line_y.get(i) * height_ratio, paint);

                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // bisai
                    Drawable drawable = new BitmapDrawable(bitmap);

                    slides_image_view.setImageDrawable(drawable);
                }
            }
        });
    }

    private void showVoteDialog(int choice_number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowSlide.this);
        builder.setMessage("Choose an answer");
        String choice[] = new String[choice_number];
        for (int i = 0; i < choice_number; i++) {
            choice[i] = "" + (char) (65 + i);
        }

        builder.setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Message message = new Message();
                message.setOperation(Command.VOTE_CHOICE);
                message.setChoice(i);
                sendMessageObject(message);
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void displayImage(Message message) {
        byte[] image = message.getImageByteArray();
        bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
//        final ImageView slides_image_view = (ImageView) (findViewById(R.id.slides_image_view));
        // bisai
        final ImageSwitcher slides_image_view = (ImageSwitcher) (findViewById(R.id.slides_image_view));
        bitmap = Bitmap.createScaledBitmap(bitmap, screen_width, screen_height, false);


        last_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.setBitmap(bitmap);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                slides_image_view.setImageDrawable(new BitmapDrawable(bitmap));
            }
        });
    }

    private void updateChart() {
        final WebView chart_view = (WebView) (findViewById(R.id.chart_view));
        String url = "http://chart.apis.google.com/chart?";
        String cht = "cht=";
        String chd = "chd=t:";
        String chs = "chs=";
        String chl = "chl=";
        String chxt = "chxt=x,y";
        String chxl = "chxl=0:|";
        String chco = "chco=";
        String chbh = "chbh=a";
        String color[] = {"FFC6A5", "FFFF42", "DEF3BD", "00A5C6", "DEBDDE", "C6EFF7"};


        for (int i = 0; i < choice_number; i++) {
            chl = chl + (char) (65 + i);
            chxl = chxl + (char) (65 + i);
            chd = chd + choice_count.get(i).toString();
            chco = chco + color[i];
            if (i < choice_number - 1) {
                chl = chl + "|";
                chd = chd + ",";
                chxl = chxl + "|";
                chco = chco + "|";
            }
        }
        chs = "chs=500x300";

        if (chart_mode == Term.PIE_CHART) {
            cht = "cht=p";
            url = url + cht + "&" + chd + "&" + chs + "&" + chl + "&" + chco;
        } else {
            cht = "cht=bvg";
            url = url + cht + "&" + chd + "&" + chxl + "&" + chxt + "&" + chbh + "&" + chco + "&" + chs;
        }

        //System.out.println(url);
        Log.d("url", url);
        final String url_copy = url;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chart_view.loadUrl(url_copy);
            }
        });
    }


    private void setWatcherListview() {
        watcher_listview_adapter = new ArrayAdapter<String>(this, R.layout.watcher_item, R.id.watcher_name_text_view, watcher_name_list);

        final ListView watcher_list_view = (ListView) (findViewById(R.id.watcher_list_view));
        watcher_list_view.setAdapter(watcher_listview_adapter);

        watcher_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView imageView = (ImageView) (findViewById(R.id.watcher_image_view));
                if (watcher_id_list.get(i) == last_authorize_id) {
                    // cancel the authorization
                    imageView.setVisibility(View.INVISIBLE);
                    Message message = new Message();
                    message.setOperation(Command.CANCEL_AUTHORIZE);
                    message.setAuthorize_id(last_authorize_id);
                    sendMessageObject(message);
                    last_authorize_id = -1;
                } else if (last_authorize_id == -1) {
                    // give authorization
                    imageView.setBackgroundResource(R.drawable.authorize);
                    imageView.setVisibility(View.VISIBLE);
                    //sendMessage(Command.AUTHORIZE);
                    Message message = new Message();
                    message.setOperation(Command.AUTHORIZE);
                    message.setAuthorize_id(watcher_id_list.get(i));
                    sendMessageObject(message);
                    last_authorize_id = watcher_id_list.get(i);
                } else if (watcher_id_list.get(i) != last_authorize_id) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setBackgroundResource(R.drawable.authorize);
                    Message message = new Message();
                    message.setOperation(Command.AUTHORIZE);
                    message.setAuthorize_id(watcher_id_list.get(i));
                    sendMessageObject(message);
                    last_authorize_id = watcher_id_list.get(i);

                    int last_index = watcher_id_list.indexOf(last_authorize_id);
                    View v = watcher_list_view.getChildAt(last_index);
                    ImageView temp = (ImageView) (v.findViewById(R.id.watcher_image_view));
                    temp.setVisibility(View.GONE);
                    Message message1 = new Message();
                    message1.setOperation(Command.CANCEL_AUTHORIZE);
                    message1.setAuthorize_id(last_authorize_id);
                    sendMessageObject(message);
                    temp.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}
