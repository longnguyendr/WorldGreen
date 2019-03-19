package com.example.worldgreen;

import android.content.Context;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CreateReportActivity extends AppCompatActivity {

    static final String TAG = "CreateReportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        setupCreateButton();
        setupGetButton();
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
                getReport();
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

    void getReport() {
        ArrayList<Report> rep = new ArrayList<Report>();
        FirebaseManager manager = new FirebaseManager();
        Log.d(TAG, "getReport: " + manager.getAllReports().size()); //ASYNCHRONOUS FIX IT!
    }

    void resetUI() {
        EditText description = (EditText) findViewById(R.id.create_report_description);
        description.setText(null);
    }

    void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }
}
