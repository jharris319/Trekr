package com.jred.trekr;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class MapsActivity extends ActionBarActivity
    implements POIDialog.NoticeDialogListener, POIDialog.OnFragmentInteractionListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng loc;

    // Variables for nav drawer
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    // DB Stuff
    private TrailDataSource dbLink;

    private ArrayList<Polyline> polylines;
    private ArrayList<Marker> markers;

    private POI poi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Database objects
        dbLink = new TrailDataSource(this);
        dbLink.open();

        setUpMapIfNeeded();

        // Setup Nav Drawer
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        polylines = new ArrayList<>();
        markers = new ArrayList<>();
        poi = new POI();
        //drawDemoTrail();
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            loc = new LatLng(location.getLatitude(), location.getLongitude());
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /** START: Navigation drawer functions **/

    private void addDrawerItems() {
        String[] navArray = { "Emergency", "Record Trail", "Select Trail", "Select POI", "Clear Trails", "Clear POIs","Terrain", "Hybrid", "Satellite", "Settings"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Clicks on the nav menu are handled here
                // On click, an integer position value will be set
                switch (position) {
                    case 0:
                        startEmergencyActivity();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 1:
                        startRecordingActivity();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 2:
                        startSelectTrailActivity();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 3:
                        startSelectPOIActivity();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 4:
                        clearPolylines();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 5:
                        clearMarkers();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 6:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 7:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 8:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 9:
                        Toast.makeText(MapsActivity.this, "Settings Requested", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Used to start EmergencyActivity from drawer OnClickListener
    private void startEmergencyActivity() {
        Intent intent = new Intent(this, EmergencyActivity.class);
        startActivity(intent);
    }

    // Used to start RecordingActivity from drawer OnClickListener
    private void startRecordingActivity() {
        Intent intent = new Intent(this, RecordingActivity.class);
        startActivity(intent);
    }

    // Used to start RecordingActivity from drawer OnClickListener
    private void startSelectTrailActivity() {
        Intent intent = new Intent(this, SelectTrail.class);
        startActivityForResult(intent, 1);
    }

    // Used to start RecordingActivity from drawer OnClickListener
    private void startSelectPOIActivity() {
        Intent intent = new Intent(this, SelectPOI.class);
        startActivityForResult(intent, 2);
    }

    /** END: Navigation drawer functions **/

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                poi.setLocation(point);
                showPOIDialog();
            }
        });


        Trail tempTrail;
        try {
            tempTrail = dbLink.findTrail(1);
            ArrayList<LatLng> pathValues = tempTrail.getPathValues();
            LatLng firstPoint = pathValues.get(0);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstPoint, 17f));
        }
        catch (NullPointerException e) {
            LatLng montevallo = new LatLng(33.1049, -86.8628);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(montevallo, 6.75f));
        }


    }

    private void drawTrail(Trail trail) {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        int randomColor = Color.rgb(r,g,b);
        ArrayList<LatLng> points = trail.getPathValues();
        PolylineOptions polyTrail = new PolylineOptions();
        for (LatLng point : points) {
            polyTrail.add(point);
        }
        polyTrail.color(randomColor);
        Polyline polyFinal = mMap.addPolyline(polyTrail);
        polylines.add(polyFinal);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 17f));
    }

    private void clearPolylines() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();
    }

    private void clearMarkers() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                Trail trail = dbLink.findTrail(Long.parseLong(data.getDataString()));
                drawTrail(trail);
                // Do something with the contact here (bigger example below)
            }
        }
        else if (requestCode == 2) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                POI poi = dbLink.findPOI(Long.parseLong(data.getDataString()));
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(poi.getLocation().latitude, poi.getLocation().longitude))
                        .title(poi.getPOIName())
                        .snippet(poi.getDescription());
                markers.add(mMap.addMarker(marker));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17f));
                // Do something with the contact here (bigger example below)
            }
        }
    }

    public void showPOIDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new POIDialog();
        dialog.show(getFragmentManager(), "POIDialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String trail_name, String poi_name, String poi_description) {
        poi.setPOIName(poi_name);
        poi.setDescription(poi_description);
        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(poi.getLocation().latitude, poi.getLocation().longitude))
                .title(poi.getPOIName())
                .snippet(poi_description);
        markers.add(mMap.addMarker(marker));
        dbLink.addPOI(poi, trail_name);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
