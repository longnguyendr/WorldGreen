package com.example.worldgreen.Events;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldgreen.DataModel.Event;

import com.example.worldgreen.Donate.DonateActivity;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.MainActivity;
import com.example.worldgreen.R;
import com.example.worldgreen.Reports.AllReportActivity;
import com.example.worldgreen.Reports.CreateReportActivity;
import com.example.worldgreen.Reports.DetailReportActivity;
import com.example.worldgreen.Reports.MyReportActivity;
import com.example.worldgreen.Users.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class DetailEventActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    final static String TAG = "DetailEventActivity";
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        event = (Event) getIntent().getSerializableExtra("event");
        setupShowReportButton();
        setupGoingButton();
        updateUI();
    }

    //region setup methods
    //----------------------------------------------------------------------------------------------

    private void setupShowReportButton() {
        Button showReportButton = findViewById(R.id.show_report_button);
        showReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getReport() != null) {
                    Intent i = new Intent(DetailEventActivity.this, DetailReportActivity.class);
                    i.putExtra("report", event.getReport());
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setupGoingButton() {
        Button goingButton = findViewById(R.id.going_button);



        goingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseManager firebaseManager = new FirebaseManager();

                if (event.amIParticipating()) {
                    firebaseManager.removeFromGoing(FirebaseAuth.getInstance().getCurrentUser(), event, getApplicationContext());
                } else {
                    firebaseManager.goingToEvent(FirebaseAuth.getInstance().getCurrentUser(), event, getApplicationContext());
                }
            }
        });
    }

    //endregion


    //region UI methods
    //----------------------------------------------------------------------------------------------

    private void updateUI() {
        if (event != null) {
            TextView titleTextView = findViewById(R.id.event_title);
            TextView descriptionTextView = findViewById(R.id.event_description);
            TextView dateTextView = findViewById(R.id.event_date);
            TextView participatingTextView = findViewById(R.id.people_going);

            titleTextView.setText(event.getTitle());
            descriptionTextView.setText(event.getDescription());
            participatingTextView.setText(event.getParticipantsNumber() + " people going");

            Date date = new Date(event.getTimestamp().getTime());
            String formattedDate = SimpleDateFormat.getDateTimeInstance().format(date);
            dateTextView.setText(formattedDate);

        }
    }

    //endregion

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
        } else if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
