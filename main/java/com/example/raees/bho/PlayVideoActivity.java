package com.example.raees.bho;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.raees.bho.R.id.fullscreen_content_controls;
import static com.example.raees.bho.R.id.videoProgress;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayVideoActivity extends AppCompatActivity {

    private static Uri fileUri = null;
    private static VideoView vid;
    private static long duration = 0;
    private static SeekBar videoProgress;
    private static boolean isAudio = false;
    private static boolean completed = false;
    private static String fileName = "";

    /*For updating the video's progress on the progress bar.
    * Runs every second.*/
    private Runnable onEverySecond = new Runnable() {

        @Override
        public void run() {
            if (videoProgress != null) {
                videoProgress.setProgress(vid.getCurrentPosition());
            }

            if (vid.isPlaying()) {
                videoProgress.postDelayed(onEverySecond, 500);
            }

        }
    };

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_play_video);

        mVisible = true;
        mControlsView = findViewById(fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("----- mContentView pressed");
                if (!isAudio) {
//                    hide controls if video
                    toggle();
                }
            }
        });

        //get and set uri of file to be played
        Intent started = getIntent();
        fileUri = started.getData();
        vid = (VideoView) mContentView;
        vid.setVideoURI(fileUri);

        //set file name from Uri
        File theFile = new File(URI.create(fileUri.toString()));
        fileName = theFile.getName();

        //check if video or audio
        //<editor-fold desc="Description">

        isAudio = !fileUri.toString().endsWith(".mp4");
        if (isAudio) {
            //if not video (i.e file is audio) set background to speaker icon
            findViewById(R.id.placeholder).setVisibility(View.VISIBLE);

            //log playing of audio
            Logger.logActivity(fileName, "REPORT_PLAY");
        } else {
            Logger.logActivity(fileName, "VIEW_PLAY");
        }
        //</editor-fold>

        //start playing video and log
        vid.requestFocus();
        completed = false;
        vid.start();
        //start thread to update progress bar
        videoProgress = (SeekBar) (findViewById(R.id.videoProgress));
        videoProgress.setProgress(0);
        videoProgress.postDelayed(onEverySecond, 500);


        //once player is ready, set progress bar max to duration of video
        //<editor-fold desc="Description">
        vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = vid.getDuration();
                videoProgress.setMax((int) duration);
            }
        });
        //</editor-fold>

        //set on completion listener: what to do when video/audio is completed
        //<editor-fold desc="Description">
        vid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            //log action
                                            String type = "VIEW";
                                            if (isAudio) {
                                                type = "REPORT";
                                            }
                                            Logger.logActivity(fileName, type + "_END");
                                            completed = true; //file has played to completion
                                            finish();
                                        }
                                    }
        );
        //</editor-fold>

        // controlling volume using the VerticalSeekBar
        //<editor-fold desc="VerticalSeekBar Volume control">
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        VerticalSeekBar volControl = (VerticalSeekBar) findViewById(R.id.volumeBar);
        volControl.setMax(maxVolume);
        volControl.setProgress(curVolume);
        volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                System.out.println("-----Volume progress changed");
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
            }
        });
        //</editor-fold>

        //<editor-fold desc="onSeekBarChangeListener for setting video progress">
        //add on change listener
        videoProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                if (arg2) { //if user has moved progress bar, update videos progress
                    vid.seekTo(progress); //set video progress
                    String seekPosition = convertMillis(progress);
                    //log action
                    if (!isAudio){
                        Logger.logActivity(fileName, "VIEW_SEEK " + seekPosition);
                    }
                    else{
                        Logger.logActivity(fileName, "REPORT_SEEK " + seekPosition);
                    }
                }
            }
        });
        //</editor-fold>
        //video is playing, so disable play button and enable pause button.
        findViewById(R.id.play).setEnabled(false);
        findViewById(R.id.pause).setEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        if (isAudio) {
            //don't hide controls if audio
            return;
        }

        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);

        findViewById(R.id.volumeBar).setVisibility(View.INVISIBLE);
        findViewById(R.id.videoProgress).setVisibility(View.INVISIBLE);
        findViewById(R.id.ear).setVisibility(View.INVISIBLE);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);

        findViewById(R.id.volumeBar).setVisibility(View.VISIBLE);
        findViewById(R.id.videoProgress).setVisibility(View.VISIBLE);
        findViewById(R.id.ear).setVisibility(View.VISIBLE);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    public void videoPressed(View v) {
        System.out.println("----- mContentView pressed");
        if (!isAudio) {
            toggle();
        }
    }

    /**
     * Resume playing of the current video or audio file
     * @param view the view pressed
     */
    public void play(View view) {
//        System.out.println("---Play button pressed");
        vid.start();

        //set timer for moving progress bar
        videoProgress.postDelayed(onEverySecond, 500);

        //disable play button, enable pause
        findViewById(R.id.play).setEnabled(false);
        findViewById(R.id.pause).setEnabled(true);

        if (!isAudio) {
            Logger.logActivity(fileName, "VIEW_PLAY");
        }
        else{
            Logger.logActivity(fileName, "REPORT_PLAY");
        }
    }

    /**
     * Pause the playing of the current video or audio file
     * @param view The view pressed
     */
    public void pause(View view) {
        System.out.println("---Pause button pressed");
        vid.pause();

        //disable pause button, enable play
        findViewById(R.id.play).setEnabled(true);
        findViewById(R.id.pause).setEnabled(false);

        //log action
        if (!isAudio){
            Logger.logActivity(fileName, "VIEW_PAUSE");
        }
        else{
            Logger.logActivity(fileName, "REPORT_PAUSE");
        }

    }

    /**
     * Send the current video/audio file to another device via Bluetooth
     * @param view the view pressed
     */
    public void share(View view) {
        //System.out.println("---Share button pressed");
        Logger.logActivity(fileName, "SHARE_ATTEMPT");

        if(isAudio){
            BluetoothActivity.dir = FileSystemv2.getAppDirectory("REC").getAbsolutePath();
            BluetoothActivity.type = "audio/*";
        }else {
            BluetoothActivity.dir = FileSystemv2.getAppDirectory("VID").getAbsolutePath();
            BluetoothActivity.type = "video/*";
        }

        //set names of file to be sent
        ArrayList<String> file = new ArrayList<String>();
        file.add(fileName);

        BluetoothActivity.fileNames = file;

        Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    /**
     * Convert the time given in milliseconds to the format mm:ss
     * @param millis Time in milliseconds
     * @return String with time in the format mm:ss
     */
    private String convertMillis(long millis) {
        return String.format("%tM", millis) + ":" + String.format("%tS", millis);
    }

    public void onBackPressed() {
        //System.out.println("back pressed");
        if (!completed) {
            //log exit before completion with time of video
            String timeClosed = convertMillis(vid.getCurrentPosition());
            if (!isAudio) {
                Logger.logActivity(fileName, "VIEW_ENDED_BY_USER " + timeClosed);
            }
            else
                Logger.logActivity(fileName, "REPORT_ENDED_BY_USER " + timeClosed);
        }
        //Logger.logActivity("no file", "back button pressed");
        super.onBackPressed();
    }

    public void finish() {
        super.finish();
    }
}
