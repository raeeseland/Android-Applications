package com.example.raees.bho;

/*
Activity that is created for sharing recordings
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShareAudioActivity extends AppCompatActivity {

    public static String dir = FileSystemv2.getAppDirectory("REC").getAbsolutePath();
    public static ArrayList<String> sendAudio = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> audio = AudioFragment.RecordingsPath;

        final ArrayList<String> audioFullName = AudioFragment.RecordingsPath;
        setContentView(R.layout.activity_share_audio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        toolbar.setTitle(getString(R.string.shareAudioTitle));
        setSupportActionBar(toolbar);



        ListView list = (ListView) findViewById(R.id.PlayList1);


        ShareAudioListAdapter adapter = new ShareAudioListAdapter(this, audio);
        Button sendAll = (Button) findViewById(R.id.all);
        Button sendSelected = (Button) findViewById(R.id.select);
        assert list != null;
        list.setAdapter(adapter);

        assert sendSelected != null;
        sendSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAudio = ShareAudioListAdapter.audioNames;
                if(sendAudio.isEmpty()){
                    Toast.makeText(getApplicationContext(),getString(R.string.shareAudioTitle),Toast.LENGTH_LONG).show();
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
                sendAudio = audioFullName;
                sendViaBluetooth();
            }
        });
    }

    private void sendViaBluetooth(){
        BluetoothActivity.dir = dir;
        BluetoothActivity.fileNames = sendAudio;
        BluetoothActivity.type = "audio/*";
        Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        this.finish();

    }


}

