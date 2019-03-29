package com.example.worldgreen.Events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.FirebaseManager.EventCallback;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.R;

import java.util.ArrayList;

public class MyEventActivity extends AppCompatActivity {
    final static String TAG = "MyEventActivity";
    EventListAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);

        recyclerView = (RecyclerView) findViewById(R.id.events_list_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseManager manager = new FirebaseManager();
        manager.getAllEvents(new EventCallback() {
            @Override
            public void onCallback(ArrayList<Event> events) {
                adapter = new EventListAdapter(getApplicationContext(), events);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
