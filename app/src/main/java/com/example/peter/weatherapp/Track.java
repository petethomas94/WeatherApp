package com.example.peter.weatherapp;

/**
 * Created by Peter on 03/05/2015.
 */
public class Track {

    private String mTrackName;
    private double mLat;
    private double mLong;

    public Track(String TrackName, double Lat, double Long){

        mTrackName = TrackName;
        mLat = Lat;
        mLong = Long;

    }

    public String getTrackName() {
        return mTrackName;
    }

    public void setTrackName(String trackName) {
        mTrackName = trackName;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLong() {
        return mLong;
    }

    public void setLong(double aLong) {
        mLong = aLong;
    }
}
