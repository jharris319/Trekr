package com.jred.trekr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.Date;
import java.util.ArrayList;

import com.jred.trekr.TrailDataSource;


public class RecordingActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // The fastest rate for active location updates.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Provides the entry point to Google Play services.
    protected GoogleApiClient mGoogleApiClient;

    // Stores parameters for requests to the FusedLocationProviderApi
    protected LocationRequest mLocationRequest;

    // Represents a geographical location.
    protected Location mCurrentLocation;

    // Time when the location was updated represented as a String.
    protected String mLastUpdateTime;

    // Trail Data
    private boolean recording;
    private String trailName;
    private ArrayList<LatLng> pathValues = new ArrayList<>();
    private Trail trail;
    private TrailDataSource dbLink;
    private LatLng lastLocation;

    private float distance;

    // Placeholder for UI elements
    protected TextView mObjCount;
    protected Chronometer mChrono;
    protected TextView mDist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        // Locate UI widgets here
        mObjCount = (TextView)findViewById(R.id.obj_count);
        mChrono = (Chronometer)findViewById(R.id.chronometer);
        mDist = (TextView)findViewById(R.id.tv_distance);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();

        Context contextNew = this;
        dbLink = new TrailDataSource(contextNew);
        dbLink.open();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recording, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** START: Google Play Services **/

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.

        startLocationUpdates();
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            // Check for successful location update
            if (mCurrentLocation != null) {
                // All clear, update UI elements
                updateUI();
            }
            else {
                // Unable to get last location, bail out
                Toast.makeText(this, "Unable to update location", Toast.LENGTH_SHORT).show();
            }
        }
        else updateUI();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(android.location.Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (recording) {
            LatLng currLocation = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            pathValues.add(currLocation);
            if (lastLocation == null) {
                lastLocation = currLocation;
                distance = 0;
            }
            else {
                float[] results = new float[1];
                Location.distanceBetween(lastLocation.latitude,lastLocation.longitude,
                                        currLocation.latitude, currLocation.longitude, results);
                distance += results[0];
                lastLocation = currLocation;
            }
        }
        updateUI();
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
        mObjCount.setText("Objects: " + String.valueOf(pathValues.size()));
        mDist.setText("Distance: " + String.format("%.2f", (distance * 3.28084)));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /** END: Google Play Services **/

    public void recToggleHandler(View view) {
        // Check Toggle State
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            // Start Recording
            recording = true;
            mChrono.start();
        } else {
            // Stop Recording
            recording = false;
            mChrono.stop();
        }
    }

    public void doneHandler(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Save Trail");
        alert.setMessage("Enter Trail Name");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            // Save Trail
            trailName = input.getText().toString();
            trail = new Trail(trailName,pathValues);
            dbLink.addTrail(trail);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
           // Canceled
            }
        });

        alert.show();
    }
}
