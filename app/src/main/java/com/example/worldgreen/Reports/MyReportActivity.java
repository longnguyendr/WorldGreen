package com.example.worldgreen.Reports;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.FirebaseManager.EventCallback;
import com.example.worldgreen.FirebaseManager.ReportCallback;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.R;
import com.example.worldgreen.DataModel.Report;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MyReportActivity extends AppCompatActivity implements ReportListAdapter.ItemClickListener {
    final static String TAG = "MyReportActivity";
    ReportListAdapter adapter;
    RecyclerView recyclerView;
    final ArrayList<Report> myReports = new ArrayList<Report>();

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
        getUsersReports();
    }

    void getUsersReports() {
        FirebaseManager manager = new FirebaseManager();

        adapter = new ReportListAdapter(getApplicationContext(), myReports);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        manager.getUsersReports(FirebaseAuth.getInstance().getCurrentUser().getUid(), new ReportCallback() {
            @Override
            public void onCallback(Report report) {
                myReports.add(report);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
