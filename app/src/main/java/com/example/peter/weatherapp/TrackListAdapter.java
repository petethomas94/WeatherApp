package com.example.peter.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Peter on 03/05/2015.
 */
public class TrackListAdapter extends ArrayAdapter {

    protected Context mContext;
    protected List<Track> mTracks;

    public TrackListAdapter(Context context, List<Track> tracks) {
        super(context, R.layout.track_list_item, tracks);
        mContext = context;
        mTracks = tracks;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){ //check if view has been created, if not make new one
            convertView = LayoutInflater.from(mContext).inflate(R.layout.track_list_item, null); //tell adapter what xml file to use to display data
            holder = new ViewHolder();

            //initialise text view within list item row
            holder.trackTitle = (TextView)convertView.findViewById(R.id.trackTitle);
            convertView.setTag(holder);
        }
        else{ //view already been made, reuse old one
            holder = (ViewHolder)convertView.getTag();
        }

        Track track = mTracks.get(position);
        holder.trackTitle.setText(track.getTrackName());  //settext view to track name

        return convertView;

    }

    public static class ViewHolder{   //accepted android practice for declaring views in adapter
        TextView trackTitle;
    }

}
