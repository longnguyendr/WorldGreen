package com.example.worldgreen;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.Menu;

import com.example.worldgreen.Donate.DonateActivity;
import com.example.worldgreen.Events.AllEventActivity;
import com.example.worldgreen.Events.CreateEventActivity;
import com.example.worldgreen.Maps.MapsActivity;
import com.example.worldgreen.Reports.AllReportActivity;
import com.example.worldgreen.Users.LoginActivity;
import com.example.worldgreen.Reports.CreateReportActivity;
import com.example.worldgreen.Users.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignOut, btnCreateReport, btnViewProfile, btnViewAllReport, btnViewAllEvent, btnDonate, btnMaps;
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
//        btnMaps = findViewById(R.id.maps_button);
//        btnMaps.setOnClickListener(this);
        btnSignOut = findViewById(R.id.Logout_button);
        btnSignOut.setOnClickListener(this);
        btnViewAllReport = findViewById(R.id.view_all_report_button);
        btnViewAllReport.setOnClickListener(this);
        btnViewAllEvent = findViewById(R.id.view_all_event_button);
        btnViewAllEvent.setOnClickListener(this);
        btnDonate = findViewById(R.id.donate_button);
        btnDonate.setOnClickListener(this);
        btnCreateReport = findViewById(R.id.create_report_button);
        btnCreateReport.setOnClickListener(this);
        btnViewProfile = findViewById(R.id.profile_button);
        btnViewProfile.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nav, menu);
        return super.onCreateOptionsMenu(menu);
    }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Logout_button:
                auth.signOut();
                Toast.makeText(MainActivity.this, "Sign out Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.create_report_button:
                startActivity(new Intent(MainActivity.this, CreateReportActivity.class ));
                break;
            case R.id.view_all_report_button:
                startActivity(new Intent(MainActivity.this, AllReportActivity.class));
                break;
            case R.id.view_all_event_button:
                startActivity(new Intent(MainActivity.this, AllEventActivity.class));
                break;
            case R.id.profile_button:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.donate_button:
                startActivity(new Intent(MainActivity.this, DonateActivity.class));
                break;
//            case R.id.maps_button:
//                startActivity(new Intent(MainActivity.this, MapsActivity.class));
//                break;
                default:break;
        }
    }
}
