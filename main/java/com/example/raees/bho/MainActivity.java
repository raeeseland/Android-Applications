package com.example.raees.bho;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.audiofx.BassBoost;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int tapAudio;
    private int tapVideo;
    private int tapLog;
    private int tapSettings;



    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AppConstants(getString(R.string.app_name),getString(R.string.extention),getString(R.string.app_lang));
        FileSystemv2.setupFileSystem(this, AppConstants.APPLICATION_NAME);

        // changing the language
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(AppConstants.APPLICATION_LANGUAGE);
        res.updateConfiguration(conf, dm);

        setContentView(R.layout.activity_main);
        // initialize the count of presses
        tapAudio = 0;
        tapVideo = 0;
        tapLog = 0;
        tapSettings = 0;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        createTabs();
        // setup the the logfile
        // looks for user data
        // if it does not find it
        if (!Logger.setLogFile()){
            // go to the settings menu to create it
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;


        // increments the amounts button taps and
        // starts the required activity when the number of taps reaches 4
        switch (id){
            case R.id.shareVideo:
                if (VideoFragment.videos[0].equals(getString(R.string.novideos))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.noVideosToSend), Toast.LENGTH_SHORT).show();
                }
                else {
                    if(tapVideo == 4) {
                        intent = new Intent(this, ShareVideoActivity.class);
                        startActivity(intent);
                        tapAudio = 0;
                        tapVideo = 0;
                        tapLog = 0;
                        tapSettings=0;
                    }
                    else{
                        tapVideo++;
                        Toast.makeText(this,getString(R.string.tapshareVideos)+" "+(5-tapVideo)+" "+getString(R.string.moreTimestoShareVideos),Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.shareAudio:
                if (AudioFragment.audio.get(0).equals(getString(R.string.noRecordings))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.noRecordingsToSend), Toast.LENGTH_SHORT).show();
                }

                else {
                    if(tapAudio == 3) {
                        intent = new Intent(this, ShareAudioActivity.class);
                        startActivity(intent);
                        tapAudio = 0;
                        tapVideo = 0;
                        tapLog = 0;
                        tapSettings=0;
                    }
                    else{
                        tapAudio++;
                        Toast.makeText(this,getString(R.string.tapshareRecordings)+" "+(4-tapAudio)+" "+getString(R.string.moreTimestoShareRecordings),Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.shareLog:
                if(Logger.getLogfileList().isEmpty()){
                    Toast.makeText(getApplicationContext(), getString(R.string.noLogsToSend), Toast.LENGTH_SHORT).show();
                }
                else{
                if (tapLog == 3) {
                    intent = new Intent(this, SendLogs.class);
                    startActivity(intent);
                    tapAudio = 0;
                    tapVideo = 0;
                    tapLog = 0;
                    tapSettings=0;

                } else {
                    tapLog++;
                    Toast.makeText(this, getString(R.string.tapshareLogs)+" " + (4 - tapLog) + " "+getString(R.string.moreTimestoShareLogs), Toast.LENGTH_SHORT).show();
                }
            }

                break;

            case R.id.settings:
                if(tapSettings == 3) {
                    Logger.logActivity(" ","OPENED_SETTINGS");
                    intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    tapAudio = 0;
                    tapVideo = 0;
                    tapLog = 0;
                    tapSettings = 0;
                }
                else {
                    tapSettings++;
                    Toast.makeText(this, getString(R.string.tapSettings)+" " + (4 - tapSettings) + " "+getString(R.string.moreTimestoSettings), Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.record:
                intent = new Intent(this,RecordAudioActivity.class);
                startActivity(intent);
                tapAudio = 0;
                tapVideo = 0;
                tapLog = 0;
                tapSettings=0;
        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0)
                return VideoFragment.newInstance(0); //Video list fragment for swipe view
            else{
                return AudioFragment.newInstance(1); //Recordings list fragment for swipe veiw
            }

        }


        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";

            }
            return null;
        }
    }

    /*
    Creates Tabs for Videos and Recordings
     */
    private void createTabs(){

        //Create tabs for the Videos and Recordings
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.videoTab)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.audioTab)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

                //log tab selection action
                String tabSelected = tab.getPosition() == 0 ? getString(R.string.videoTab) : getString(R.string.audioTab);
                Logger.logActivity(" ", tabSelected + " tab Selected");
                //System.out.println("----tabSelected: "+ tabSelected);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
