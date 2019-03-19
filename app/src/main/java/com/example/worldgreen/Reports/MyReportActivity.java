package com.example.worldgreen.Reports;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.FirebaseManager.ReportCallback;
import com.example.worldgreen.R;
import com.example.worldgreen.DataModel.Report;

import java.util.ArrayList;

public class MyReportActivity extends AppCompatActivity {
    final static String TAG = "MyReportActivity";
    ReportListAdapter adapter;
    Boolean allReports = true;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report);

        setupRecycleView();
        getReports();
    }

    void setupRecycleView() {
        recyclerView = (RecyclerView) findViewById(R.id.reports_list_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    void getReports() {
        if (getIntent().getExtras() != null) {
            allReports = getIntent().getExtras().getBoolean("allReports");
        }

        if (allReports) {
            getAllReports();
        } else {
            getUsersReports();
        }
    }

    void getAllReports() {
        FirebaseManager manager = new FirebaseManager();
        manager.getAllReports(new ReportCallback() {
            @Override
            public void onCallback(ArrayList<Report> reports) {
                adapter = new ReportListAdapter(getApplicationContext(), reports);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    void getUsersReports() {
        FirebaseManager manager = new FirebaseManager();
//        manager.getUsersReports();
    }
}
