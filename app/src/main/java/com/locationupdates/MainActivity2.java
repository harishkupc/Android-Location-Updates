package com.locationupdates;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.locationupdates.location.LocationHelper;
import com.locationupdates.location.LocationListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    // UI Widgets.
    private TextView mLastUpdateTimeTextView;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;

    // Labels.
    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private String mLastUpdateTimeLabel;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private boolean mRequestingLocationUpdates;

    LocationHelper locationHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Locate the UI widgets.
        mLatitudeTextView = findViewById(R.id.latitude_text);
        mLongitudeTextView = findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = findViewById(R.id.last_update_time_text);

        // Set labels.
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);

        updateValuesFromBundle(savedInstanceState);

        locationHelper = new LocationHelper(this, listener);
        if (locationHelper.isGPSEnable()) {
            locationHelper.startTracking();
        } else {
            locationHelper.startLocationRequest();
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }

    private void updateUI() {
        if (mCurrentLocation != null) {
            mLatitudeTextView.setText(String.format(Locale.ENGLISH, "%s: %f", mLatitudeLabel,
                    mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText(String.format(Locale.ENGLISH, "%s: %f", mLongitudeLabel,
                    mCurrentLocation.getLongitude()));
            mLastUpdateTimeTextView.setText(String.format(Locale.ENGLISH, "%s: %s",
                    mLastUpdateTimeLabel, mLastUpdateTime));
        }
    }

    LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            mCurrentLocation = location;
            locationHelper.stopTracking();
            updateUI();
        }

        @Override
        public void locationUpdateStarted() {
            //updateUI
            Log.d(TAG, "location Updates Started");
        }

        @Override
        public void locationUpdateStopped() {
            //updateUI
            Log.d(TAG, "location Updates Stopped");
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationHelper.isGPSEnable()) {
            locationHelper.startTracking();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case LocationHelper.REQUEST_CHECK_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        locationHelper.startTracking();
                        break;
                }
                break;
        }
    }


}