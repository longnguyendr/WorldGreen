package com.example.worldgreen;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;

import com.example.worldgreen.Donate.DonateActivity;
import com.example.worldgreen.Events.AllEventActivity;
import com.example.worldgreen.Events.CreateEventActivity;
import com.example.worldgreen.Events.MyEventActivity;
import com.example.worldgreen.Maps.MapsActivity;
import com.example.worldgreen.Reports.AllReportActivity;
import com.example.worldgreen.Reports.MyReportActivity;
import com.example.worldgreen.Users.LoginActivity;
import com.example.worldgreen.Reports.CreateReportActivity;
import com.example.worldgreen.Users.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
//    implements View.OnClickListener {

    private Button btnSignOut, btnCreateReport, btnViewProfile, btnViewAllReport, btnViewAllEvent, btnDonate, btnMaps;
    private TextView navUsername;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        setContentView(R.layout.activity_main);

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("Mainactivity", "Username: " + user.getEmail());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.textView_nav_header_main);
        navUsername.setText(String.valueOf(user.getEmail()));
        navigationView.setNavigationItemSelectedListener(this);

//        btnMaps = findViewById(R.id.maps_button);
//        btnMaps.setOnClickListener(this);
//        btnSignOut = findViewById(R.id.Logout_button);
//        btnSignOut.setOnClickListener(this);
//        btnViewAllReport = findViewById(R.id.view_all_report_button);
//        btnViewAllReport.setOnClickListener(this);
//        btnViewAllEvent = findViewById(R.id.view_all_event_button);
//        btnViewAllEvent.setOnClickListener(this);
//        btnDonate = findViewById(R.id.donate_button);
//        btnDonate.setOnClickListener(this);
//        btnCreateReport = findViewById(R.id.create_report_button);
//        btnCreateReport.setOnClickListener(this);
//        btnViewProfile = findViewById(R.id.profile_button);
//        btnViewProfile.setOnClickListener(this);

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

    /**
     * Nav-drawer item selected event
     * @param item
     * @return
     */
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
            startActivity(new Intent(this, MyEventActivity.class).putExtra("participating", false));
        } else if (id == R.id.nav_participate_event) {
            startActivity(new Intent(this, MyEventActivity.class).putExtra("participating", true));
        } else if (id == R.id.nav_my_report) {
            startActivity(new Intent(this, MyReportActivity.class));
        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Sign out Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_home) { }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_nav, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_sign_out:
//                auth.signOut();
//                Toast.makeText(MainActivity.this, "Sign out Successful", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                finish();
//                return true;
//            default: return false;
//        }
//    }
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.Logout_button:
//                auth.signOut();
//                Toast.makeText(MainActivity.this, "Sign out Successful", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                finish();
//                break;
//            case R.id.create_report_button:
//                startActivity(new Intent(MainActivity.this, CreateReportActivity.class ));
//                break;
//            case R.id.view_all_report_button:
//                startActivity(new Intent(MainActivity.this, AllReportActivity.class));
//                break;
//            case R.id.view_all_event_button:
//                startActivity(new Intent(MainActivity.this, AllEventActivity.class));
//                break;
//            case R.id.profile_button:
//                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//                break;
//            case R.id.donate_button:
//                startActivity(new Intent(MainActivity.this, DonateActivity.class));
//                break;
//            case R.id.maps_button:
//                startActivity(new Intent(MainActivity.this, MapsActivity.class));
//                break;
//                default:break;
//        }
//    }
}
