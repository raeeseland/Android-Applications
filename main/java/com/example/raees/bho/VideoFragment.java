package com.example.raees.bho;

/**
 * Created by Raees on 2016-08-13.
 * Currently linked to the filesystem class and functioning
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;

public class VideoFragment extends Fragment{

    /**
     * The fragment argument representing the section number for this
     * fragment.
     * Creates a video fragment
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static String[] videos;
    public static Bitmap[] thumb;
    private static Uri[] videoUris;


    public VideoFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static VideoFragment newInstance(int sectionNumber) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView list = (ListView) rootView.findViewById(R.id.PlayList);


        setVideos();

        if(videos[0].equals(getString(R.string.novideos))){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.audio_list,R.id.textView,videos);
            list.setAdapter(adapter);
        }
        else {
            ListAdapter adapter = new ListAdapter(getActivity(), videos, thumb, false);
            list.setAdapter(adapter);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(videos[0].equals(getString(R.string.novideos))){

                }
                else {
                    // play video
                    String selectedItem = videos[+position];
                    Uri selectedUri = videoUris[position];
//                System.out.println("------------" + selectedItem);
//                System.out.println("------------"+ selectedUri);
                    Intent intent = new Intent(Intent.ACTION_VIEW, selectedUri, getActivity(), PlayVideoActivity.class);
                    startActivity(intent);

                    //log action
                    Logger.logActivity(selectedUri.toString(), " Video selected");
                }

            }
        });

        return rootView;
    }

    /*
    Initialises video with the files found in findVideos()
     */
    private void setVideos(){

        // build an array list of files with all the videos in it
        ArrayList<File> myVideos = FileSystemv2.findFiles("VID");
        // if no videos are found after searching the directory
        //if (myVideos.isEmpty()){
            // search the main app directory
            //myVideos = FileSystemv2.findFiles("");
        //}
        // if videos was still not found
        if(myVideos.isEmpty()){
            // prepare to present no videos found
            videos = new String[1];
            videos[0] =  getString(R.string.novideos);
        }
        // otherwise prepare to present the list of videos in the activity
        else {
            videos = new String[myVideos.size()];
            thumb = new Bitmap[myVideos.size()];
            videoUris = new Uri[myVideos.size()];
            for (int i = 0; i < myVideos.size(); i++) {
                videos[i] = myVideos.get(i).getName().toString();
                thumb[i] = ThumbnailUtils.createVideoThumbnail(myVideos.get(i).getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                videoUris[i] = Uri.fromFile(myVideos.get(i));
            }
        }
    }
}
