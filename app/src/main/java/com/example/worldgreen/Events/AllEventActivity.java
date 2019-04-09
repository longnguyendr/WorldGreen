package com.example.worldgreen.Events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.FirebaseManager.EventCallback;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AllEventActivity extends AppCompatActivity {
    final static String TAG = "AllEventActivity";
    EventListAdapter adapter;
    RecyclerView recyclerView;
    final ArrayList<Event> allEvent = new ArrayList<Event>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_event);

        prepareView();
        getAllUsersEvent();
    }

    protected void prepareView() {
        recyclerView = findViewById(R.id.events_list_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    protected void getAllUsersEvent() {
        adapter = new EventListAdapter(getApplicationContext(), allEvent);
        adapter.setItemClickListener(new EventListAdapter.itemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(AllEventActivity.this, DetailEventActivity.class)
                        .putExtra("event",allEvent.get(position)));
            }
        });
        recyclerView.setAdapter(adapter);

        FirebaseManager manager = new FirebaseManager();
        manager.getAllEvents(new EventCallback() {
            @Override
            public void onCallback(Event event) {
                allEvent.add(event);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
