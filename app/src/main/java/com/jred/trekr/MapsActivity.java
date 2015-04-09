package com.jred.trekr;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
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
import com.google.android.gms.maps.model.PolylineOptions;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;

import java.lang.reflect.Array;

public class MapsActivity extends ActionBarActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng loc;

    // Variables for nav drawer
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        setUpMapIfNeeded();

        // Setup Nav Drawer
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
        String[] navArray = { "Emergency", "Terrain", "Hybrid", "Satellite", "Settings"};
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
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 2:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 3:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 4:
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
        LatLng montevallo = new LatLng(33.1049, -86.8628);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(montevallo, 6.75f));
        drawDemoTrail();
    }

    private void drawDemoTrail() {
        LatLng[] points = new LatLng[]
                {
                        new LatLng(33.263509000, -87.535579000),
                        new LatLng(33.263669000, -87.535346000),
                        new LatLng(33.263759000, -87.535023000),
                        new LatLng(33.263841000, -87.534721000),
                        new LatLng(33.263928000, -87.534452000),
                        new LatLng(33.263976000, -87.534181000),
                        new LatLng(33.263773000, -87.534267000),
                        new LatLng(33.263576000, -87.534358000),
                        new LatLng(33.263385000, -87.534189000),
                        new LatLng(33.263218000, -87.534031000),
                        new LatLng(33.263033000, -87.533854000),
                        new LatLng(33.262914000, -87.533613000),
                        new LatLng(33.262859000, -87.533359000),
                        new LatLng(33.262689000, -87.532973000),
                        new LatLng(33.262712000, -87.532706000),
                        new LatLng(33.262625000, -87.532498000),
                        new LatLng(33.262431000, -87.532390000),
                        new LatLng(33.262233000, -87.532282000),
                        new LatLng(33.262105000, -87.532077000),
                        new LatLng(33.262123000, -87.531717000),
                        new LatLng(33.262041000, -87.531415000),
                        new LatLng(33.262062000, -87.531110000),
                        new LatLng(33.262182000, -87.530931000),
                        new LatLng(33.262361000, -87.530776000),
                        new LatLng(33.262611000, -87.530789000),
                        new LatLng(33.262794000, -87.530855000),
                        new LatLng(33.263024000, -87.530915000),
                        new LatLng(33.263212000, -87.531002000),
                        new LatLng(33.263455000, -87.531157000),
                        new LatLng(33.263658000, -87.531375000),
                        new LatLng(33.263798000, -87.531577000),
                        new LatLng(33.263959000, -87.531853000),
                        new LatLng(33.264073000, -87.532087000),
                        new LatLng(33.264190000, -87.532324000),
                        new LatLng(33.264229000, -87.532619000),
                        new LatLng(33.264302000, -87.532871000),
                        new LatLng(33.264409000, -87.533052000),
                        new LatLng(33.264588000, -87.533274000),
                        new LatLng(33.264727000, -87.533471000),
                        new LatLng(33.264861000, -87.533649000),
                        new LatLng(33.265048000, -87.533827000),
                        new LatLng(33.265265000, -87.533984000),
                        new LatLng(33.265456000, -87.534137000),
                        new LatLng(33.265591000, -87.534318000),
                        new LatLng(33.265577000, -87.534562000),
                        new LatLng(33.265776000, -87.534547000),
                        new LatLng(33.265952000, -87.534678000),
                        new LatLng(33.266126000, -87.534776000),
                        new LatLng(33.266353000, -87.534753000),
                        new LatLng(33.266534000, -87.534812000),
                        new LatLng(33.267755000, -87.534047000),
                        new LatLng(33.267992000, -87.534121000),
                        new LatLng(33.268182000, -87.534208000),
                        new LatLng(33.268442000, -87.534205000),
                        new LatLng(33.268631000, -87.534183000),
                        new LatLng(33.269553000, -87.533567000),
                        new LatLng(33.269378000, -87.533435000),
                        new LatLng(33.269191000, -87.533298000),
                        new LatLng(33.269050000, -87.533128000),
                        new LatLng(33.269004000, -87.532940000),
                        new LatLng(33.269982000, -87.532169000),
                        new LatLng(33.270139000, -87.532013000),
                        new LatLng(33.270274000, -87.531859000),
                        new LatLng(33.270443000, -87.531650000),
                        new LatLng(33.270597000, -87.531466000),
                        new LatLng(33.270106000, -87.530380000),
                        new LatLng(33.269855000, -87.530456000),
                        new LatLng(33.269641000, -87.530553000),
                        new LatLng(33.269430000, -87.530490000),
                        new LatLng(33.269185000, -87.530352000),
                        new LatLng(33.268006000, -87.529893000),
                        new LatLng(33.267797000, -87.529710000),
                        new LatLng(33.267643000, -87.529578000),
                        new LatLng(33.267480000, -87.529369000),
                        new LatLng(33.267372000, -87.529138000),
                        new LatLng(33.266184000, -87.528505000),
                        new LatLng(33.265959000, -87.528500000),
                        new LatLng(33.265760000, -87.528580000),
                        new LatLng(33.265593000, -87.528459000),
                        new LatLng(33.265424000, -87.528261000),
                        new LatLng(33.264235000, -87.527669000),
                        new LatLng(33.264017000, -87.527726000),
                        new LatLng(33.263793000, -87.527627000),
                        new LatLng(33.263562000, -87.527578000),
                        new LatLng(33.263353000, -87.527521000),
                        new LatLng(33.263779000, -87.526909000),
                        new LatLng(33.263886000, -87.526831000),
                        new LatLng(33.263911000, -87.526590000),
                        new LatLng(33.263879000, -87.526325000),
                        new LatLng(33.263779000, -87.526350000),
                        new LatLng(33.262917000, -87.526356000),
                        new LatLng(33.262681000, -87.526324000),
                        new LatLng(33.262431000, -87.526296000),
                        new LatLng(33.262144000, -87.526237000),
                        new LatLng(33.261966000, -87.526405000),
                        new LatLng(33.262511000, -87.527210000),
                        new LatLng(33.262729000, -87.527215000),
                        new LatLng(33.262921000, -87.527324000),
                        new LatLng(33.263129000, -87.527363000),
                        new LatLng(33.262939000, -87.527288000),
                        new LatLng(33.263433000, -87.528152000),
                        new LatLng(33.263619000, -87.528220000),
                        new LatLng(33.263765000, -87.528416000),
                        new LatLng(33.263581000, -87.528657000),
                        new LatLng(33.263523000, -87.528718000),
                        new LatLng(33.264754000, -87.529202000),
                        new LatLng(33.264899000, -87.529000000),
                        new LatLng(33.265051000, -87.528877000),
                        new LatLng(33.265240000, -87.528818000),
                        new LatLng(33.265424000, -87.528758000),
                        new LatLng(33.265517000, -87.529565000),
                        new LatLng(33.265723000, -87.529551000),
                        new LatLng(33.265926000, -87.529456000),
                        new LatLng(33.265869000, -87.529692000),
                        new LatLng(33.265886000, -87.529955000),
                        new LatLng(33.266650000, -87.530233000),
                        new LatLng(33.266441000, -87.530203000),
                        new LatLng(33.266368000, -87.530268000),
                        new LatLng(33.266182000, -87.530563000),
                        new LatLng(33.266115000, -87.530799000),
                        new LatLng(33.266815000, -87.531413000),
                        new LatLng(33.267024000, -87.531477000),
                        new LatLng(33.267139000, -87.531298000),
                        new LatLng(33.267280000, -87.531146000),
                        new LatLng(33.267125000, -87.531026000),
                        new LatLng(33.267218000, -87.530794000),
                        new LatLng(33.267421000, -87.530861000),
                        new LatLng(33.267597000, -87.530958000),
                        new LatLng(33.267791000, -87.530835000),
                        new LatLng(33.267992000, -87.530749000),
                        new LatLng(33.268661000, -87.531150000),
                        new LatLng(33.268774000, -87.530931000),
                        new LatLng(33.268943000, -87.530752000),
                        new LatLng(33.269123000, -87.530759000),
                        new LatLng(33.268998000, -87.531004000),
                        new LatLng(33.268506000, -87.532003000),
                        new LatLng(33.268382000, -87.532165000),
                        new LatLng(33.268237000, -87.532333000),
                        new LatLng(33.268109000, -87.532493000),
                        new LatLng(33.268175000, -87.532752000),
                        new LatLng(33.267550000, -87.533316000),
                        new LatLng(33.267792000, -87.533131000),
                        new LatLng(33.268013000, -87.533021000),
                        new LatLng(33.268200000, -87.532976000),
                        new LatLng(33.267992000, -87.533064000),
                        new LatLng(33.266833000, -87.532919000),
                        new LatLng(33.266696000, -87.532740000),
                        new LatLng(33.266536000, -87.532560000),
                        new LatLng(33.266333000, -87.532518000),
                        new LatLng(33.266119000, -87.532457000),
                        new LatLng(33.265065000, -87.531707000),
                        new LatLng(33.265033000, -87.531447000),
                        new LatLng(33.264885000, -87.531318000),
                        new LatLng(33.264694000, -87.531282000),
                        new LatLng(33.264485000, -87.531181000),
                        new LatLng(33.263499000, -87.530407000),
                        new LatLng(33.263332000, -87.530279000),
                        new LatLng(33.263320000, -87.530482000),
                        new LatLng(33.263126000, -87.530464000),
                        new LatLng(33.262934000, -87.530357000),
                        new LatLng(33.263126000, -87.530899000),
                        new LatLng(33.263308000, -87.531016000),
                        new LatLng(33.263478000, -87.531108000),
                        new LatLng(33.263638000, -87.531224000),
                        new LatLng(33.263761000, -87.531407000),
                        new LatLng(33.264288000, -87.532689000),
                        new LatLng(33.264379000, -87.532900000),
                        new LatLng(33.264362000, -87.532946000),
                        new LatLng(33.264207000, -87.532789000),
                        new LatLng(33.264364000, -87.532907000),
                        new LatLng(33.265231000, -87.533918000),
                        new LatLng(33.265399000, -87.534064000),
                        new LatLng(33.265586000, -87.534162000),
                        new LatLng(33.265608000, -87.534370000),
                        new LatLng(33.265563000, -87.534586000),
                        new LatLng(33.264542000, -87.535304000),
                        new LatLng(33.264338000, -87.535317000),
                        new LatLng(33.264114000, -87.535332000),
                        new LatLng(33.263908000, -87.535338000),
                        new LatLng(33.263686000, -87.535339000),
                        new LatLng(33.263447000, -87.535584000),
                        new LatLng(33.263534000, -87.535623000)
                };
        PolylineOptions trail = new PolylineOptions();
        for (LatLng point : points) {
            trail.add(point);
        }
        trail.color(android.graphics.Color.RED);
        mMap.addPolyline(trail);
    }
}
