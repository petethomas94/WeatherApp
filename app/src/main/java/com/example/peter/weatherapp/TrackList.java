package com.example.peter.weatherapp;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class TrackList extends ListActivity {

    public static final String TAG = TrackList.class.getSimpleName();

    protected List<Track> mTracks;    //create list of tracks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        mTracks = new ArrayList<Track>();

        for(int i = 0;i<TrackData.trackNames.length ;++i){    //populate list of tracks

            Track track = new Track(TrackData.trackNames[i],TrackData.mLat[i],TrackData.mLong[i]);

            Log.v(TAG, track.getTrackName() + " " + track.getLat() + " " + track.getLong());

            mTracks.add(i,track);

        }

        populateList();


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Track track = mTracks.get(position);

        Intent intent = new Intent(TrackList.this,MainActivity.class);

        intent.putExtra("Trackname",track.getTrackName());
        intent.putExtra("TrackLat",track.getLat());
        intent.putExtra("TrackLong",track.getLong());

        startActivity(intent);

    }

    private void populateList(){

        TrackListAdapter adapter = new TrackListAdapter(getListView().getContext(),mTracks);
        setListAdapter(adapter);

    }



}
