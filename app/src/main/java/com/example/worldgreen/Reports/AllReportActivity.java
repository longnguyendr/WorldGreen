package com.example.worldgreen.Reports;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.FirebaseManager.ReportCallback;
import com.example.worldgreen.R;

import java.util.ArrayList;

public class AllReportActivity extends AppCompatActivity {
    ReportListAdapter adapter;
    RecyclerView recyclerView;
    final ArrayList<Report> allReport = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_report);

        recyclerView = (RecyclerView) findViewById(R.id.report_list_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportListAdapter(getApplicationContext(), allReport);
        recyclerView.setAdapter(adapter);

        FirebaseManager manager = new FirebaseManager();
        manager.getAllReports(new ReportCallback() {
            @Override
            public void onCallback(Report report) {
                allReport.add(report);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
