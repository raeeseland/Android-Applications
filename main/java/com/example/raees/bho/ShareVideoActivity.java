package com.example.raees.bho;

/*
Activity that is created for sharing videos
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShareVideoActivity extends AppCompatActivity {

    public static final String dir = FileSystemv2.getAppDirectory("VID").getAbsolutePath();
    public static ArrayList<String> videoNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_video);

        String [] videos = VideoFragment.videos;
        Bitmap[] thumb = VideoFragment.thumb;



        Button sendAll = (Button) findViewById(R.id.all);
        Button sendSelected = (Button) findViewById(R.id.select);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        assert toolbar != null;
        toolbar.setTitle(getString(R.string.shareVideoTitle));
        setSupportActionBar(toolbar);


        ListView list = (ListView) findViewById(R.id.PlayList1);
        ListAdapter adapter = new ListAdapter(this, videos, thumb, true);
        assert list != null;
        list.setAdapter(adapter);

        assert sendSelected != null;
        sendSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoNames = ListAdapter.videos;
                if (videoNames.isEmpty()){
                    Toast.makeText(getApplicationContext(),getString(R.string.shareVideoTitle),Toast.LENGTH_LONG).show();
                }
                else {
                    sendViaBluetooth();
                }

            }
        });

        assert sendAll != null;
        sendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String i : VideoFragment.videos){
                    videoNames.add(i);
                }
                sendViaBluetooth();
            }
        });

    }

    private void sendViaBluetooth(){
        // directory in which the videos are stored
        BluetoothActivity.dir = dir;
        BluetoothActivity.fileNames = videoNames;
        BluetoothActivity.type = "video/*";
        Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        this.finish();

    }

}
