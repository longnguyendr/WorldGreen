package com.example.worldgreen.Users;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.worldgreen.Events.MyEventActivity;
import com.example.worldgreen.R;
import com.example.worldgreen.Reports.MyReportActivity;

public class ProfileActivity extends AppCompatActivity {

    private Button btnViewMyReport, btnViewMyEvent, btnParticipatingEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btnViewMyEvent = findViewById(R.id.view_my_event_button);
        btnViewMyReport = findViewById(R.id.view_my_report_button);
        btnParticipatingEvent = findViewById(R.id.view_events_i_am_participating_button);

        btnViewMyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MyReportActivity.class));
            }
        });
        btnViewMyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, MyEventActivity.class);
                i.putExtra("participating", false);
                startActivity(i);
            }
        });
        btnParticipatingEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, MyEventActivity.class);
                i.putExtra("participating", true);
                startActivity(i);
            }
        });
    }
}
