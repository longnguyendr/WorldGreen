package com.example.worldgreen.Events;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.Donate.DonateActivity;
import com.example.worldgreen.FirebaseManager.EventCallback;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.MainActivity;
import com.example.worldgreen.R;
import com.example.worldgreen.Reports.AllReportActivity;
import com.example.worldgreen.Reports.CreateReportActivity;
import com.example.worldgreen.Reports.MyReportActivity;
import com.example.worldgreen.Users.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MyEventActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    final static String TAG = "MyEventActivity";
    EventListAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private TextView navUsername;
    final ArrayList<Event> myEvent = new ArrayList<Event>();
    FirebaseAuth mAuth;
    boolean participating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);

        progressBar = findViewById(R.id.progressBar_my_event);
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
        participating = getIntent().getBooleanExtra("participating", false);
        prepareView();

        if (participating) {
            getParticipatingEvent();
        } else {
            getUsersEvent();
        }
    }
    protected void prepareView () {
        progressBar(true);
        recyclerView = (RecyclerView) findViewById(R.id.events_list_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void progressBar(Boolean trigger) {
        if (trigger) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
    /**
     *  When event is added to participants in event, event is updated and onDataChange in FirebaseManager is called
     *  -> also onCallback is called again. That means that onCallback we have new event.
     *  But this event is already in allEvent arrayList - the only difference between new one and old one is participants
     *  if even already exist (we have in array list event with same id) we will replace this event.
     */

    protected void getUsersEvent () {
        adapter = new EventListAdapter(getApplicationContext(), myEvent);
        adapter.setItemClickListener(new EventListAdapter.itemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(MyEventActivity.this, DetailEventActivity.class);
                i.putExtra("event", myEvent.get(position));
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);

        FirebaseManager manager = new FirebaseManager();
        mAuth = FirebaseAuth.getInstance();
        manager.getUsersEvents(mAuth.getCurrentUser().getUid(), new EventCallback() {
            @Override
            public void onCallback(Event event) {
                if (eventExists(event)) {
                    replaceEvent(event);
                } else {
                    myEvent.add(event);
                }
                progressBar(false);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getParticipatingEvent() {
        adapter = new EventListAdapter(getApplicationContext(), myEvent);
        adapter.setItemClickListener(new EventListAdapter.itemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(MyEventActivity.this, DetailEventActivity.class);
                i.putExtra("event", myEvent.get(position));
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);

        FirebaseManager manager = new FirebaseManager();
        mAuth = FirebaseAuth.getInstance();
        manager.getEventsIamParticipating(mAuth.getCurrentUser(), new EventCallback() {
            @Override
            public void onCallback(Event event) {
                if (eventExists(event)) {
                    replaceEvent(event);
                } else {
                    myEvent.add(event);
                }
                progressBar(false);
                adapter.notifyDataSetChanged();
            }
        });
    }


    private boolean eventExists(Event newEvent) {
        for (Event event: myEvent) {
            if (event.getId().equals(newEvent.getId())) {
                return true;
            }
        }
        return false;
    }

    private void replaceEvent(Event newEvent) {

        List<Event> toRemove = new ArrayList<Event>();
        List<Event> toAdd = new ArrayList<Event>();
        for (Event event: myEvent) {
            if (event.getId().equals(newEvent.getId())) {
                toRemove.add(event);
                toAdd.add(newEvent);
            }
        }
        myEvent.removeAll(toRemove);
        myEvent.addAll(toAdd);
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
