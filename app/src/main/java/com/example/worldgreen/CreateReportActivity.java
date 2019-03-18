package com.example.worldgreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CreateReportActivity extends AppCompatActivity {

    static final String TAG = "CreateReportActivity";    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        setupCreateButton();
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

    void createReport() {
        TextView description = (TextView) findViewById(R.id.create_report_description);
        Report report = new Report(123.1,321.1,description.toString());
        FirebaseManager manager = new FirebaseManager();
        try {
            manager.saveReport(report);
        } catch (Exception e) {
            Log.d(TAG, "createReport: " + e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        }
    }
}
