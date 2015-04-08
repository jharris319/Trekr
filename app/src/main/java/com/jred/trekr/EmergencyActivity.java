        package com.jred.trekr;

        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.location.Location;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.provider.ContactsContract;
        import android.speech.tts.TextToSpeech;
        import android.support.v7.app.ActionBarActivity;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
        import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;

        import java.text.DateFormat;
        import java.util.Date;

        import android.telephony.SmsManager;


public class EmergencyActivity extends ActionBarActivity implements
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

    // Placeholder for UI elements
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;

    //For the intent and storing the phone number
    private static final String PREFS = "prefs";
    private static final String PREF_NAME1 = "name";
    SharedPreferences mSharedPreferences;
    private static final int CONTACT_PICKER_RESULT = 1001;
    protected String phone = "";
    protected String mphoneNumber = "";

    protected TextToSpeech ttsObject;

    //RT
    //Displays the Search button on the emergency activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_emergency, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //RT
    // call getItemId() on the given MenuItem to determine which item was pressed—
    // the returned ID matches the value you declared in the corresponding <item> element's android:id attribute.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                //openSearch(); //function to search for something, can be taken out, just wanted something to look at
                return true;
            case R.id.action_settings:
                //openSettings(); //function to adjust the app's settings
                return true;
            case R.id.action_addContact:
                //I could change this to if the size of the array is greater than 3 contacts then force them to delete some
                if(mphoneNumber.length() >0){
                    Toast.makeText(this, "Too Many Contacts, Must Delete "
                            ,Toast.LENGTH_LONG).show();
                }
                else {
                    doLaunchContactPicker();//Launches the intent for the contacts application
                }
                return true;
            case R.id.action_deleteContact:
                deleteContact(); // Deletes the current stored contact
                return true;
            case android.R.id.home: // Handles back button
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Launches the intent for the contacts application
    public void doLaunchContactPicker() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    //grabs the phone number of the contact that the user pressed
    //and stores it into mphoneNumber
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Cursor cursor = null;
                    phone = "";
                    try {
                        Uri result = data.getData();
                        // get the contact id from the Uri
                        String id = result.getLastPathSegment();

                        // query for everything phone
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id},
                                null);

                        int phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);

                        // let's just get the first phone
                        if (cursor.moveToFirst()) {
                            phone = cursor.getString(phoneIdx);
                            mphoneNumber = phone;
                            // Put it into memory (don't forget to commit!)
                            SharedPreferences.Editor e =
                                    mSharedPreferences.edit();
                            e.putString(PREF_NAME1, mphoneNumber);
                            e.commit();

                            // Display the contact added in a toast
                            Toast.makeText(getApplicationContext(),
                                    "Contact Added: " + mphoneNumber + "!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    } catch (Exception e) {

                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                        if (phone.length() == 0) {
                            Toast.makeText(this, "No phone number found for contact.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
    }
    //delete the contact
    protected void deleteContact(){
        mphoneNumber = "";
        SharedPreferences.Editor e =
                mSharedPreferences.edit();
        e.putString(PREF_NAME1, mphoneNumber);
        e.commit();
        Toast.makeText(this, "Contact Deleted",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        // Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Locate UI widgets here
        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);


        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();

        //Setup TextToSpeech object
        ttsObject = new TextToSpeech(getApplicationContext(), null);
        ttsObject.setSpeechRate(0.75f);


        // Access the device's key-value storage
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Read the user's name,
        // or an empty string if nothing found
        mphoneNumber = mSharedPreferences.getString(PREF_NAME1, "");
    }

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
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
        mLatitudeTextView.setText("Latitude: " + String.valueOf(mCurrentLocation.getLatitude()));
        mLongitudeTextView.setText("Longitude: " + String.valueOf(mCurrentLocation.getLongitude()));
        mLastUpdateTimeTextView.setText("Last Update: " + mLastUpdateTime);
    }

    public void sendSMSHandler(View view) {
        sendSMSMessage();
    }

    protected void sendSMSMessage() {
        // Grab the phone number textview
        // TextView tV = (TextView)findViewById(R.id.phone_no);
        // String phoneNo = tV.getText().toString();
        String phoneNo = mphoneNumber;

        String googleMapsLink = "\n\ngoogle.com/maps/place/"
                + String.valueOf(mCurrentLocation.getLatitude()) + ","
                + String.valueOf(mCurrentLocation.getLongitude());
        String message = "Send help! I'm located at:"+ "\nLatitude: " + String.valueOf(mCurrentLocation.getLatitude())
                + "\nLongitude: " + String.valueOf(mCurrentLocation.getLongitude()) + "\nas of " + mLastUpdateTime
                + googleMapsLink;
        try {
            // Try sending the message to the selected contact
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent to: " + phoneNo,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void readTextHandler(View view) {
        String readString = mLatitudeTextView.getText().toString()
                + mLongitudeTextView.getText().toString()
                + mLastUpdateTimeTextView.getText().toString();
        readText(readString);
    }

    public void readText(String readableText) {
        if (Build.VERSION.SDK_INT >= 21) {
            ttsObject.speak(readableText, TextToSpeech.QUEUE_FLUSH, null, "readText");
        }
        else {
            ttsObject.speak(readableText, TextToSpeech.QUEUE_FLUSH, null);
        }

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
}
