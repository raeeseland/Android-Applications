package com.example.raees.bho;

import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Chronometer;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class RecordAudioActivity extends AppCompatActivity {

    private String outputFile;
    private MediaRecorder recorder;
    private Chronometer myChronometer;
    private String audioName;
    String filename;
    File thef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_record_audio));
        setSupportActionBar(toolbar);
        // the timer
        myChronometer = (Chronometer) findViewById(R.id.chronometer);
        // immediately start recording
        beginRecording();
    }

    /**
     * Method executed when the stop button is pressed.
     * Audio recording is stopped.
     * The file (.mp3) is saved with the name "chw_dd-mm-yyyy-hh:mm:ss.mp3"
     * Recording is logged
     * List of recordings is opened (list of recordings on MainActivity)
     * @param v The button pressed
     */
    public void stop(View v){
        //log action
        //Logger.logActivity(audioName, "RECORD_END");
        this.finish();
    }

    // starts the recording
    private void beginRecording()  {
        // the name of the audiofile
        audioName = getString(R.string.extention) + DateFormat.getDateTimeInstance().format(new Date())+".3gpp";
        // ensure that the file name is valid
        audioName = audioName.replace(':','-');
        // log action
        Logger.logActivity(audioName, "RECORD_START");
        thef = FileSystemv2.createFile(audioName, "REC");
        filename = thef.getAbsolutePath();
        // tests
        boolean check = thef.exists();
        boolean check2 = (thef!=null);
        // do the recording
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(filename);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        myChronometer.start();

    }

    private void exitRecorder(){
        //log action
        Logger.logActivity(audioName, "RECORD_END");
        if(recorder != null) {
            // stop the recording and release the recorder resource
            recorder.stop();
            recorder.release();
        }
        // repopulate the audio fragment with the new file
        AudioFragment.startUp=1;
        AudioFragment.audio.add(audioName.replace("3gpp","").replace(getString(R.string.extention),""));
        boolean c = thef.exists();
        AudioFragment.audioUris.add(Uri.fromFile(thef));
        AudioFragment.RecordingsPath.add(audioName);
        // stop the timer
        myChronometer.stop();
    }

    public void finish(){
        super.finish();
        exitRecorder();
    }
}

