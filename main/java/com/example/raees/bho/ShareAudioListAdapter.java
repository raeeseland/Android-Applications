package com.example.raees.bho;

/**
 * Created by Raees on 2016-08-13.
 * A custom adapter class to create a view when
 * a recording wants to be sent via Bluetooth
 */
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ShareAudioListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;
    public static ArrayList<String> audioNames = new ArrayList<>();
    CheckState state = new CheckState();



    public ShareAudioListAdapter(Activity context, ArrayList<String> itemname) {
        super(context, R.layout.video_list, itemname);
        this.context=context;
        this.itemname=itemname;


    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView;
        TextView txtTitle;
        final Button button;


        rowView = inflater.inflate(R.layout.share_audio_list, null, true);
        txtTitle = (TextView) rowView.findViewById(R.id.text2);
        button = (Button) rowView.findViewById(R.id.sendAudio);

        if(!state.inItems(getItem(position))){
            state.addItem(getItem(position));
        }

        if(state.getState(position)==false) {
            button.setBackgroundResource(R.drawable.circle);
        }

        else{
            button.setBackgroundResource(R.drawable.tick);
        }



        txtTitle.setText(itemname.get(position).replace("3gpp","").replace("CHW_",""));
        txtTitle.setTextColor(Color.BLACK);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state.getState(position)==false) {
                    button.setBackgroundResource(R.drawable.tick);
                    state.setState(position,true);
                    if(audioNames.contains(itemname.get(position))){
                    }
                    else
                        audioNames.add(itemname.get(position));

                }
                else if(state.getState(position)==true) {
                    button.setBackgroundResource(R.drawable.circle);
                    state.setState(position,false);
                    if(audioNames.contains(itemname.get(position))){
                        audioNames.remove(itemname.get(position));
                    }

                }

            }
        });

        return rowView;

    };


}
