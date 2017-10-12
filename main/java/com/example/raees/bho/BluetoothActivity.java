package com.example.raees.bho;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 *
 */

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter;
    private Intent intent;
    private File f;
    private static final int REQUEST_BLU = 1;
    private static final int DURATION = 120;
    public static ArrayList<String> fileNames = new ArrayList<>();
    // directory in which the shared files are stored
    public static String dir;
    // the type of files which will be found by the file system
    // applies to both video and audio and log files
    public static String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        // initialize the bluetooth activity
        init();
        // log bluetooth activity
        // for every file to be sent
        for (String i : fileNames){
            // log the share attempt
            Logger.logActivity(i,"SHARE_ATTEMPT");
        }
    }
    // initializes the bluetooth functionality
    private void init() {
        // this is used to check the device support for bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // if the device does not support bluetooth
        if(btAdapter == null) {
            // tell the user that bluetooth is not supported
            Toast.makeText(this, getString(R.string.not_supported), Toast.LENGTH_LONG).show();
            // end; no bluetooth sending occurs
            this.finish();
        } else {
            // otherwise, this will start bluetooth
            turnOnBT();
        }
    }
    // starts the devices built in bluetooth interface
    private void turnOnBT() {
        // start the bluetooth activity built into the android device
        intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DURATION);
        startActivityForResult(intent, REQUEST_BLU);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == DURATION && requestCode == REQUEST_BLU) {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType(type);
            // this uri array is to help the sending via bluetooth
            ArrayList<Uri> uris = new ArrayList<Uri>();

            // for every file which was requested to be sent
            for(String i : fileNames) {
                // obtain the handle to the file
                f = FileSystemv2.getFile(i, dir);
                // add the file uri to the uri array
                uris.add(Uri.fromFile(f));
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

            PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

            if (appsList.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : appsList) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(this, getString(R.string.bluetooth_not_found), Toast.LENGTH_LONG).show();
                    this.finish();
                } else {
                    intent.setClassName(packageName, className);
                    startActivity(intent);
                }
            }
            this.finish();

        }
        // if the user clicks deny
        else {
            // show the user that the bluetooth was cancelled
            Toast.makeText(this, getString(R.string.bluetooth_cancelled), Toast.LENGTH_LONG).show();
            // log the activity
            // for every file which had a share attempt
            for (String i : fileNames){
                // log that the file share for the file was cancelled
                Logger.logActivity(i,"SHARE_CANCELLED");
            }
            // end the activity
            this.finish();
        }
    }
}
