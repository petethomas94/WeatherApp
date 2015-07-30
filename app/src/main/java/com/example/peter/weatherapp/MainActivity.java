package com.example.peter.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather mCurrentWeather;


    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.tempLabel) TextView mTemperatureLabel;    //use butterknife library to initialize views
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;

    @InjectView(R.id.locationLabel) TextView mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);   //code completion

        final Track track = new Track("",0.0,0.0);
        Intent intent = getIntent();
        track.setTrackName(intent.getExtras().getString("Trackname"));   //how to handle intent data
        track.setLat(intent.getExtras().getDouble("TrackLat"));
        track.setLong(intent.getExtras().getDouble("TrackLong"));

        mLocation.setText(track.getTrackName());   //set location name

        mProgressBar.setVisibility(View.INVISIBLE);   //set refresh button

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getForeCast(mXCoord, mYCoord);
                Log.v(TAG, track.getLat() + " " + track.getLong());
                getForeCast(track.getLat(),track.getLong());
            }
        });


        getForeCast(track.getLat(),track.getLong());

        Log.d(TAG, "Main UI Code is running");



    }

    private void getForeCast(double latitude, double longitude) {
       String apiKey = "60f6756a7aee9e1ff0471fd58e41346b";    //api key from forecast.io

       String forecastURL = "https://api.forecast.io/forecast/" + apiKey +   //url
                "/" + latitude + "," + longitude;

        Log.v(TAG, forecastURL);

        if(isNetworkAvailable()) { // check if data connection available

            toggleRefresh();
            OkHttpClient client = new OkHttpClient();           //create new client from library
            Request request = new Request.Builder().url(forecastURL).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {   //starts background thread
                @Override
                public void onFailure(Request request, IOException e) {

                    runOnUiThread(new Runnable() {   //run on main thread
                        @Override
                        public void run() {
                            toggleRefresh();       //set refresh button to viewable
                        }
                    });

                    alertUserAboutError();     //send error message

                }

                @Override
                public void onResponse(Response response) throws IOException {  //what to do with return information

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) { //checking for response
                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {          //allows Displa to be updated from main thread
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

                       } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception Caught", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception Caught", e);
                    }

                }
            });
        }
        else{
            Toast.makeText(this, getString(R.string.network_unavailable_message), Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {

        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {

        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be:");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() +"%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);

        }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        String timezone  = forecast.getString("timezone");
        Log.i(TAG, "From JSON" + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;


    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if(networkInfo != null && networkInfo.isConnected() ){  //check network connectivity/availability

            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {

        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");

    }

}
