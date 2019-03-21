package com.example.worldgreen.Reports;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.FirebaseManager.EventCallback;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.FirebaseManager.ReportCallback;
import com.example.worldgreen.R;
import com.example.worldgreen.DataModel.Report;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CreateReportActivity extends AppCompatActivity {

    static final String TAG = "CreateReportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        setupCreateButton();
        setupGetButton(); // btn just for test
        setupMyRep(); // btn just for test
        setupCreateEventButton(); //btn just for test
        setupGetEventsButton(); //btn just for test
    }

    void setupCreateButton() {
        Button creteButton = (Button) findViewById(R.id.create_report_save_button);
        creteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReport();
            }
        });
    }

    void setupGetButton() {
        Button getButton = (Button) findViewById(R.id.create_report_get);
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getReport();
                FirebaseManager manager = new FirebaseManager();
                manager.getAllReports(new ReportCallback() {
                    @Override
                    public void onCallback(ArrayList<Report> reports) {
                        Log.d(TAG, "onCallback: REPORTS: " + reports.size());
                    }
                });
            }
        });
    }

    void createReport() {
        EditText description = (EditText) findViewById(R.id.create_report_description);
        Report report = new Report(123.1, 321.1, description.getText().toString());
        FirebaseManager manager = new FirebaseManager();
        try {
            manager.saveReport(report);
            Toast.makeText(getApplicationContext(), "Report saved!", Toast.LENGTH_SHORT).show();
            resetUI();
        } catch (Exception e) {
            Log.d(TAG, "createReport: " + e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void setupCreateEventButton() {
        Button createEventButton = (Button) findViewById(R.id.testCreateEvent);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseManager manager = new FirebaseManager();
                manager.getAllReports(new ReportCallback() {
                    @Override
                    public void onCallback(ArrayList<Report> reports) {
                        Log.d(TAG, "onCallback: REPORTS SIZE" + reports.size());
                        Event e = new Event("i am test event", "test event", "13-02-2020", reports.get(0));
                        try {
                            manager.saveEvent(e);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    void setupGetEventsButton() {
        Log.d(TAG, "setupGetEventsButton: pressed");
        Button getEventsButton = (Button) findViewById(R.id.testGetEvents);
        getEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseManager manager = new FirebaseManager();
                manager.getAllEvents(new EventCallback() {
                    @Override
                    public void onCallback(ArrayList<Event> events) {
                        // update events method
                        Log.d(TAG, "onCallback: ALL EVENTS SIZE: " + events.size());
                    }
                });
                manager.getUsersEvents(FirebaseAuth.getInstance().getCurrentUser().getUid(), new EventCallback() {
                    @Override
                    public void onCallback(ArrayList<Event> events) {
                        Log.d(TAG, "onCallback: USERS EVENTS SIZE: " + events.size());
                    }
                });
            }
        });

    }

    void setupMyRep() {
        Button myRep = (Button) findViewById(R.id.testMyReports);
        myRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseManager manager = new FirebaseManager();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                manager.getUsersReports(uid, new ReportCallback() {
                    @Override
                    public void onCallback(ArrayList<Report> reports) {
                        Log.d(TAG, "onCallback: myReports" + reports.size());
                    }
                });
            }
        });
    }

    void getReport() {
        Intent i = new Intent(this, MyReportActivity.class);
        i.putExtra("allReports", true);
        startActivity(i);
    }

    void resetUI() {
        EditText description = (EditText) findViewById(R.id.create_report_description);
        description.setText(null);
    }

    void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }
}
