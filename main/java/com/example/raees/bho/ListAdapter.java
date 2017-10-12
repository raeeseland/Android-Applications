package com.example.raees.bho;

/**
 * Created by Raees on 2016-08-13.
 * A custom adapter class to create a view for viewing and sending videos
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Bitmap[] image;
    private boolean flag; // used to see if user wants to send or view videos
    public static String name;
    public static String dir = FileSystemv2.getAppDirectory("VID").getAbsolutePath() ;
    public static ArrayList<String> videos = new ArrayList<>();
    CheckState state = new CheckState();




    public ListAdapter(Activity context, String[] itemname, Bitmap[] image, boolean flag) {
        super(context, R.layout.video_list, itemname);
        this.context=context;
        this.itemname=itemname;
        this.image=image;
        this.flag=flag;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView;
        TextView txtTitle;
        ImageView imageView;
        final Button button;


        if(!flag) {
            // To view videos
            rowView = inflater.inflate(R.layout.video_list, null, true);
            txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
            imageView = (ImageView) rowView.findViewById(R.id.icon);
            Button blueTooth = (Button) rowView.findViewById(R.id.button);
            txtTitle.setText(itemname[position].replace(".mp4","").replace(AppConstants.APPLICATION_FILE_PREFIX,""));
            txtTitle.setTextColor(Color.BLACK);
            imageView.setImageBitmap(image[position]);

            blueTooth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name = itemname[position];
                    BluetoothActivity.dir = dir;
                    BluetoothActivity.fileNames.add(name);
                    BluetoothActivity.type = "video/*";
                    Intent intent = new Intent(getContext().getApplicationContext(),BluetoothActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().getApplicationContext().startActivity(intent);
                }
            });


        }

        else{
            //To send videos
            rowView = inflater.inflate(R.layout.share_video_list, null, true);
            txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
            imageView = (ImageView) rowView.findViewById(R.id.icon);
            button =(Button) rowView.findViewById(R.id.sendVideo);

            if(!state.inItems(getItem(position))){
                state.addItem(getItem(position));
            }

            if(state.getState(position)==false) {
                button.setBackgroundResource(R.drawable.circle);
            }

            else{
                button.setBackgroundResource(R.drawable.tick);
            }

            txtTitle.setText(itemname[position].replace("mp4",""));
            txtTitle.setTextColor(Color.BLACK);
            imageView.setImageBitmap(image[position]);


            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(state.getState(position)==false) {
                        button.setBackgroundResource(R.drawable.tick);
                        state.setState(position,true);
                        if(videos.contains(itemname[position])){
                        }
                        else
                            videos.add(itemname[position]);
                    }
                    else if(state.getState(position)==true) {
                        button.setBackgroundResource(R.drawable.circle);
                        state.setState(position,false);
                        if(videos.contains(itemname[position])){
                            videos.remove(itemname[position]);
                        }

                    }

                }
            });


        }

        return rowView;

    }


}
