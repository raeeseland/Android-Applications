package com.example.raees.bho;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;


public class AudioFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     * Creates an audio fragment
     * Class is linked to the file system class and functioning
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView list;
    //private ArrayAdapter<String> itemsAdapter;
    private ArrayAdapter<String> itemsAdapter;
    public static ArrayList<String> audio;
    public static ArrayList<String> RecordingsPath;
    public static ArrayList<Uri> audioUris;
    public static int startUp = 0;
    View row;


    public AudioFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AudioFragment newInstance(int sectionNumber) {
        AudioFragment fragment = new AudioFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        row = inflater.inflate(R.layout.audio_list,null,true);
        TextView text = (TextView)row.findViewById(R.id.textView);
        text.setTextColor(Color.BLUE);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        list = (ListView) rootView.findViewById(R.id.PlayList);

        setRecordings();
        setAdapter();
        return rootView;
    }

    public void onResume() {
        super.onResume();
        // if there are recordings and the first item is the no recordings found
        // from before
        if (audio.get(0).equals(getString(R.string.noRecordings)) && startUp == 1) {
            audio.remove(0);
            itemsAdapter.remove(getString(R.string.noRecordings));
            audio.get(0);
        }

        if (itemsAdapter != null)
            itemsAdapter.notifyDataSetChanged();
    }


    /*
    Initialises audio with the files found in findAudio()
     */
    private void setRecordings() {

        // build an array list with all required recordings in it
        ArrayList<File> myAudio = FileSystemv2.findFiles("REC");
        // if no audio recordings are found
        if (myAudio.isEmpty()) {
            // prepare to present no recordings found
            audio = new ArrayList<>();
            audioUris = new ArrayList<>();
            RecordingsPath = new ArrayList<>();
            audio.add(getString(R.string.noRecordings)); //if no recordings are found
            // indicates whether there are audio recordings present
            // zero means no recordings
            AudioFragment.startUp = 0;
        }
        // otherwise prepare to present the list of recordings in the activity
        else {
            audio = new ArrayList<>();
            RecordingsPath = new ArrayList<>();
            audioUris = new ArrayList<>();
            for (int i = 0; i < myAudio.size(); i++) {
                audio.add(myAudio.get(i).getName().toString().replace("3gpp", "").replace(getString(R.string.extention), ""));
                RecordingsPath.add(myAudio.get(i).getName().toString());

                audioUris.add(Uri.fromFile(myAudio.get(i)));
            }
        }
    }


    private void setAdapter() {
        itemsAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.audio_list, R.id.textView, audio);

        itemsAdapter.notifyDataSetChanged();
        list.setAdapter(itemsAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Play recordings
                String selected = ((TextView) view.findViewById(R.id.textView)).getText().toString();
                if(audio.get(0).equals("No Recordings Found")){

                }

                else {
                    String selectedItem = audio.get(position);
                    Uri selectedUri = audioUris.get(position);
                    //System.out.println("------------" + selectedItem);
                    //System.out.println("------------" + selectedUri);
                    Intent intent = new Intent(Intent.ACTION_VIEW, selectedUri, getActivity(), PlayVideoActivity.class);
                    startActivity(intent);
                    // log action
                    //Logger.logActivity(selectedUri.toString(), " Recording selected");
                }
            }
        });
    }
}
