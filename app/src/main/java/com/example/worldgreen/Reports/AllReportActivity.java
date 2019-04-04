package com.example.worldgreen.Reports;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.FirebaseManager.ReportCallback;
import com.example.worldgreen.R;

import java.util.ArrayList;

public class AllReportActivity extends AppCompatActivity {
    final static String TAG = "AllReportActivity";
    ReportListAdapter adapter;
    RecyclerView recyclerView;
    final ArrayList<Report> allReport = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_report);
        prepareView();
        getAllUsersReport();
    }
    protected void prepareView () {
        recyclerView = findViewById(R.id.report_list_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    protected void getAllUsersReport () {
        adapter = new ReportListAdapter(getApplicationContext(), allReport);
        adapter.setClickListener(new ReportListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(AllReportActivity.this, DetailReportActivity.class)
                    .putExtra("report", allReport.get(position)));
            }
        });

        recyclerView.setAdapter(adapter);

        FirebaseManager manager = new FirebaseManager();
        manager.getAllReports(new ReportCallback() {
            @Override
            public void onCallback(Report report) {
                allReport.add(report);
                Log.d(TAG, String.valueOf(allReport));
                adapter.notifyDataSetChanged();
            }
        });
    }
}
