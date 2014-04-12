package com.example.slides_controller.app;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private int operation;
    private ArrayList<Float> line_x;
    private ArrayList<Float> line_y;
    private byte[] image;

    public Message() {
        line_x = new ArrayList<Float>();
        line_y = new ArrayList<Float>();
        operation = 0;
    }

    public void addPoint(float x, float y) {
        line_x.add(x);
        line_y.add(y);
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

    public ArrayList<Float> getLine_y() {
        return line_y;
    }

    public byte[] getImageByteArray() {
        return image;
    }

    public void setImageByteArray(byte[] image) {
        this.image = image.clone();
    }
}





