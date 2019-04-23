package com.example.worldgreen.Reports;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.Donate.DonateActivity;
import com.example.worldgreen.Events.AllEventActivity;
import com.example.worldgreen.Events.MyEventActivity;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.FirebaseManager.ReportCallback;
import com.example.worldgreen.MainActivity;
import com.example.worldgreen.MapManager.PermissionUtils;
import com.example.worldgreen.Maps.OnMapAndViewReadyListener;
import com.example.worldgreen.R;
import com.example.worldgreen.Users.LoginActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.worldgreen.MapManager.MapManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.LogManager;

public class AllReportActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnInfoWindowClickListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        GoogleMap.OnMyLocationButtonClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    final static String TAG = "AllReportActivity";
    final ArrayList<Report> allReport = new ArrayList<Report>();
    private GoogleMap mMap;
    /**Keeps track of the selected marker.**/
    private Marker mSelectedMarker;
    private boolean mPermissionDenied = false;
    private LocationListener locationListener;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private HashMap<Marker , Report > mReportMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_report);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.getUiSettings().setZoomControlsEnabled(false);
        getAllUsersReport();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        enableMyLocation();

    }
    protected void getAllUsersReport () {
        FirebaseManager manager = new FirebaseManager();
        manager.getAllReports(new ReportCallback() {
            @Override
            public void onCallback(Report report) {
                allReport.add(report);
                for(Report i : allReport) {
                    LatLng Locations = new LatLng(i.getLatitude(), i.getLongitude());
                    addMarkersToMap(Locations , i);

                }

            }
        });
    }

    private void addMarkersToMap(LatLng Locations ,Report i) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(Locations)
                        .title(i.getTitle())
                        .snippet(i.getDescription()));
                mReportMap.put(marker, i);
//                Log.d(TAG,"----add marker mReportMap: " + mReportMap.get(marker).getDescription());
//                mMap.setInfoWindowAdapter(new ReportMapInfoAdapter(this.getApplicationContext()));
    }

    @Override
    public void onMapClick(final LatLng point) {
        // Any showing info window closes when the map is clicked.
        // Clear the currently selected marker.
        mSelectedMarker = null;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // The user has re-tapped on the marker which was already showing an info window.
        if (marker.equals(mSelectedMarker)) {
            // The showing info window has already been closed - that's the first thing to happen
            // when any marker is clicked.
            // Return true to indicate we have consumed the event and that we do not want the
            // the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            mSelectedMarker = null;
            return true;
        }

        mSelectedMarker = marker;

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
//            MapManager mManager = new MapManager();
//            mManager.checkLocationListener();
            checkLocationListener();
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,20, locationListener, null);
        }
    }
    private void checkLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng CurrentCoord = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        mSelectedMarker = marker;
        Log.d(TAG,"----infowindow mReportMap: " + mReportMap.get(marker).getTitle());
        Toast.makeText(AllReportActivity.this, "on inforWindow Click", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "marker position" + marker.getPosition());
        startActivity(new Intent(this, DetailReportActivity.class).putExtra("report", mReportMap.get(marker)));
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_create_report) {
            startActivity(new Intent(this,CreateReportActivity.class));
        } else if (id == R.id.nav_view_all_report) {
            startActivity(new Intent(this, AllReportActivity.class));
        } else if (id == R.id.nav_view_all_event) {
            startActivity(new Intent(this, AllEventActivity.class));
        } else if (id == R.id.nav_donate) {
            startActivity(new Intent(this, DonateActivity.class));
        } else if (id == R.id.nav_my_event) {
            startActivity(new Intent(this, MyEventActivity.class));
        } else if (id == R.id.nav_my_report) {
            startActivity(new Intent(this, MyReportActivity.class));
        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Sign out Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
