package com.example.slides_controller.app;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private int operation;
    private ArrayList<Float> line_x;
    private ArrayList<Float> line_y;
    private byte[] image;
    private int screen_width;
    private int screen_height;
    private int vote_choice;
    private int vote_num;
    private ArrayList<Integer> client_ids;
    private ArrayList<String> client_names;
    private int request_speaker_id;
    public Message() {
        line_x = new ArrayList<Float>();
        line_y = new ArrayList<Float>();
        operation = 0;
        screen_height = 0;
        screen_width = 0;
        vote_choice = 0;
        request_speaker_id = 0;
        client_ids = new ArrayList<Integer>();
        client_names = new ArrayList<String>();
    }

    public int getScreenWidth() {
        return screen_width;
    }

    public void setScreenWidth(int screen_width) {
        this.screen_width = screen_width;
    }

    public int getScreenHeight() {
        return screen_height;
    }

    public void setScreenHeight(int screen_height) {
        this.screen_height = screen_height;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public ArrayList<Float> getLine_x() {
        return line_x;
    }

    public void setLine_x(ArrayList<Float> line_x) {
        this.line_x = line_x;
    }

    public ArrayList<Float> getLine_y() {
        return line_y;

    }

    public void setLine_y(ArrayList<Float> line_y) {
        this.line_y = line_y;
    }

    public byte[] getImageByteArray() {
        return image;
    }

    public void setImageByteArray(byte[] image) {
        this.image = image.clone();
    }

    public int getChoice() {
        return this.vote_choice;
    }

    public void setChoice(int choice) {
        this.vote_choice = choice;
    }

    public int getVoteNum() {
        return this.vote_num;
    }

    public void setVoteNum(int num) {
        this.vote_num = num;
    }

    public void addclient(int id, String name) {
        client_ids.add(id);
        client_names.add(name);
    }

    public ArrayList<Integer> getClientIds() {
        return client_ids;

    }

    public ArrayList<String> getClientNames() {
        return client_names;
    }

    public int getRequestId() {
        return request_speaker_id;
    }

    public void setRequestId(int id) {
        request_speaker_id = id;
    }
}
