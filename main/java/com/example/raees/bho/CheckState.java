package com.example.raees.bho;

import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Raees on 2016-09-07.
 */
public class CheckState {

    private ArrayList<String> items;
    private ArrayList<Boolean> position;


    CheckState(){
        items = new ArrayList<>();
        position = new ArrayList<>();
    }

    public void addItem(String item){
        items.add(item);
        position.add(false);

    }

    public void setState(int place,boolean state){
        position.add(place,state);
    }

    public boolean getState(int place){
        return position.get(place);
    }

    public String getItem(int place){
        return items.get(place);
    }

    public boolean inItems(String item){
        return items.contains(item);
    }
}
