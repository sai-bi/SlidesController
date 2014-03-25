package com.example.slides_controller.app;
import android.graphics.Point;
import java.util.ArrayList;

/**
 * Created by saibi on 3/25/14.
 */
public class Message {
    private int operation;
    private ArrayList<MyPoint> line;

    public Message(){
        line = new ArrayList<MyPoint>();
        operation = 0;
    }
    public void setOperation(int operation){
        this.operation = operation;
    }
    public void addPoint(MyPoint v){
        line.add(v);
    }
    public int getOperation(){
        return operation;
    }
    public ArrayList<MyPoint> getLine(){
        return line;
    }

}
