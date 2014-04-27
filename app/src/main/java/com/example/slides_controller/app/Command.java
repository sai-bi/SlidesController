package com.example.slides_controller.app;

import java.net.Socket;

/**
 * Created by saibi on 4/7/14.
 */
public class Command {
    static public final int START_DISPLAY = 0;
    static public final int END_DISPLAY = 1;
    static public final int PREVIOUS_SLIDE = 2;
    static public final int NEXT_SLIDE = 3;
    static public final int BLACK_SCREEN = 4;
    static public final int EXIT_BLACK_SCREEN = 5;
    static public final int WHITE_SCREEN = 6;
    static public final int EXIT_WHITE_SCREEN = 7;
    static public final int PEN = 8;
    static public final int LASER = 9;
    static public final int HIGHLIGHT = 10;
    static public final int IMAGE = 11;
    static public final int LINE = 12;
    static public final int WATCHER = 15;
    static public final int SPEAKER = 16;
    static public final int CREATE_VOTE = 13;
    static public final int VOTE_CHOICE = 14;
    static public final int CLIENTINFO = 17;
    static public final int AUTHORIZE = 18;
    static public final int CANCEL_AUTHORIZE = 19;
    static public final int CLEAR = 20;
    static public final int REQUEST = 23;
    static public final int CLIENT_LOSE = 21;
    static public final int EXIT = 22;
    static public Socket server_socket = null;

}
