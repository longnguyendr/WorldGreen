package com.example.worldgreen;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.Menu;

import com.example.worldgreen.Donate.DonateActivity;
import com.example.worldgreen.Events.AllEventActivity;
import com.example.worldgreen.Reports.AllReportActivity;
import com.example.worldgreen.Users.LoginActivity;
import com.example.worldgreen.Reports.CreateReportActivity;
import com.example.worldgreen.Users.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private Button btnSignOut, btnCreateReport, btnViewProfile, btnViewAllReport, btnViewAllEvent, btnDonate;
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

        btnSignOut = findViewById(R.id.Logout_button);
        btnViewAllReport = findViewById(R.id.view_all_report_button);
        btnViewAllEvent = findViewById(R.id.view_all_event_button);
        btnDonate = findViewById(R.id.donate_button);
        btnCreateReport = findViewById(R.id.create_report_button);
        btnViewProfile = findViewById(R.id.profile_button);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Toast.makeText(MainActivity.this, "Sign out Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        btnCreateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateReportActivity.class ));
            }
        });
        btnViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
        btnViewAllEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AllEventActivity.class));
            }
        });
        btnViewAllReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AllReportActivity.class));
            }
        });
        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DonateActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
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
}
