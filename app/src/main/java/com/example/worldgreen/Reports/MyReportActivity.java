package com.example.worldgreen.Reports;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldgreen.Donate.DonateActivity;
import com.example.worldgreen.Events.AllEventActivity;
import com.example.worldgreen.Events.MyEventActivity;
import com.example.worldgreen.FirebaseManager.ReportCallback;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.MainActivity;
import com.example.worldgreen.R;
import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.Users.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MyReportActivity extends AppCompatActivity implements ReportListAdapter.ItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    final static String TAG = "MyReportActivity";
    ReportListAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private TextView navUsername;
    final ArrayList<Report> myReports = new ArrayList<Report>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report);

        progressBar = findViewById(R.id.progressBar_my_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.textView_nav_header_main);
        navUsername.setText(String.valueOf(user.getEmail()));
        navigationView.setNavigationItemSelectedListener(this);
        setupRecycleView();
        getReports();
    }

    void setupRecycleView() {
        progressBar(true);
        recyclerView = (RecyclerView) findViewById(R.id.reports_list_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void progressBar(Boolean trigger) {
        if (trigger) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
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
                progressBar(false);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(MyReportActivity.this, DetailReportActivity.class);
        Log.d(TAG, "onItemClick: " + adapter.getItem(position));
        Log.d(TAG, "onItemClick: " + myReports.get(position));
        i.putExtra("report", myReports.get(position));
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_create_report) {
            startActivity(new Intent(this,CreateReportActivity.class));
        } else if (id == R.id.nav_view_all_report) {
            startActivity(new Intent(this, AllReportActivity.class));
        } else if (id == R.id.nav_view_all_event) {
            startActivity(new Intent(this, AllEventActivity.class));
        } else if (id == R.id.nav_donate) {
            startActivity(new Intent(this, DonateActivity.class));
        } else if (id == R.id.nav_my_event) {
            startActivity(new Intent(this, MyEventActivity.class).putExtra("participating", false));
        } else if (id == R.id.nav_participate_event) {
            startActivity(new Intent(this, MyEventActivity.class).putExtra("participating", true));
        } else if (id == R.id.nav_my_report) {
            startActivity(new Intent(this, MyReportActivity.class));
        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Sign out Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
