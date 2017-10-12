package com.example.raees.bho;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SendLogs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_logs);
        sendViaBluetooth();
    }

    // starts the devices built in bluetooth interface
    private void sendViaBluetooth(){
        BluetoothActivity.dir = FileSystemv2.getAppDirectory("DATA").getAbsolutePath();
        BluetoothActivity.fileNames = Logger.getLogfileList();
        BluetoothActivity.type = "text/plain";
        Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        this.finish();
    }
}
