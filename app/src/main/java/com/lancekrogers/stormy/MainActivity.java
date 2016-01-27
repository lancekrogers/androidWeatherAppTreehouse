package com.lancekrogers.stormy;

import android.Manifest;
import android.content.Context;

import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;

import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather mCurrentWeather;


    @Bind(R.id.timeLabel)
    TextView mTimeLabel;

    @Bind(R.id.temperatureLabel)
    TextView mTemperatureLabel;

    @Bind(R.id.summaryLabel)
    TextView mSummaryLabel;

    //  @Bind(R.id.humidityLabel) TextView mHummidityLabel;

    @Bind(R.id.iconImageView)
    ImageView mIconImageView;

    //  @Bind(R.id.precipLabel) TextView mPrecipLabel;

    @Bind(R.id.humidityValue)
    TextView mHumidityValue;

    @Bind(R.id.precipValue)
    TextView mPrecipValue;

    @Bind(R.id.refreshImageView)
    ImageView mRefreshImageView;

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;


    // Location declarations are below

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    @Bind(R.id.locationLabel)
    TextView mLocationLabel;


    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters


    double latitude = 34.8444;
    double longitude = -82.3856;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        mProgressBar.setVisibility(View.INVISIBLE);





        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast();
            }
        });

        getForecast();
        Log.d(TAG, "Main ui code is running");


    }

    private void getForecast() {
        String apiKey = "3862f7e47028cdf9432b03a026180c4c";

        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {

            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });


                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "EXCEPTION CAUGHT: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "EXCEPTION CAUGHT: ", e);
                    }

                }
            });
        } else {
            Toast.makeText(this, getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();

        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        int drawable = mCurrentWeather.getIconId();
        mIconImageView.setImageResource(drawable);

    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON:" + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);


        String formattedTime = currentWeather.getFormattedTime();
        Log.d(TAG, formattedTime);

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        boolean isNetworkAvailable = false;
        if (netInfo != null && netInfo.isConnected()) {
            isNetworkAvailable = true;
        }
        return isNetworkAvailable;

    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void updateLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            Log.v(TAG, latitude + ", " + longitude + "New lat and long");


        } else {

            Log.e(TAG, "Could not update location");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }


    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }


    @Override
    public void onConnected(Bundle bundle) {
        // Once connected with google api, get the location
        updateLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

    }
}
