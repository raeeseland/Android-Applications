package com.example.raees.bho;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    EditText userNameBox;
    EditText deviceIDBox;
    // displayed user data
    String userName;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // set the user data
        populateTextViews();
    }

    public void onBackPressed(){

        // if the user data is valid
        if (checkExit(userID,userName)){
            // exit the activity
            this.finish();
        }

    }


    public void onSavePress(View view) {
        // configure the logger
        userNameBox = (EditText) findViewById(R.id.userName);
        deviceIDBox = (EditText) findViewById(R.id.deviceID);
        String uid = deviceIDBox.getText().toString();
        String uname = userNameBox.getText().toString();

        // check that the user data is valid before exiting
        if (checkExit(uid,uname)){
            // end the activity
            this.finish();
        }

    }

    private void populateTextViews(){

        // check if the logger has valid user data
        userName = Logger.getUserName();
        userID = Logger.getUserID();
        // if the user data is invalid
        if (userName==null || userID==null){
            // look for valid user data info text file
            String[] filein = Logger.getStoredUserData();
            // if there is such a valid file
            if (filein!=null && filein.length>=2){
                // present it in the text field
                userID = filein[0];
                userName = filein[1];
            }
        }
        // references to all the widgets
        EditText userNameBox = (EditText) findViewById(R.id.userName);
        // if the user name is valid
        if (userName!=null){
            // set the text view to display the user name
            userNameBox.setText(userName);
        }
        // otherwise the default text will be displayed
        EditText deviceIDBox = (EditText) findViewById(R.id.deviceID);
        // if the user id is valid
        if (userID!=null){
            deviceIDBox.setText(userID);
        }
        TextView storageDirectory = (TextView) findViewById(R.id.storagePath);
        TextView freeSpace = (TextView)findViewById(R.id.freeSpace);
        // set the fixed display areas
        storageDirectory.setText(getString(R.string.dataDirectory) + "\n" + FileSystemv2.getStoragePath());
        freeSpace.setText(getString(R.string.freeSpace) + "\n" + Long.toString(FileSystemv2.getFreeKilobytes()) + " KB");
        // set the editable ones
    }

    private boolean checkExit(String id, String name){
        // check that the user data is valid before exiting
        if (id!=null && name!=null && !id.equals("") && !name.equals("")){
            // the user data is valid
            // so configure the logger
            // save the user data
            Logger.setStoredUserData(id, name);
            // user data is stored and is valid
            return true;
        }
        // otherwise prevent the activity from ending
        else{
            // show user that user data is not valid
            Toast.makeText(this, R.string.userDataToast, Toast.LENGTH_SHORT).show();
            // exiting shall be prevented
            return false;
        }
    }
}
