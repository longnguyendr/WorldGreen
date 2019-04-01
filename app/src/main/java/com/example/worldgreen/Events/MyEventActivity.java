package com.example.worldgreen.Events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.FirebaseManager.EventCallback;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MyEventActivity extends AppCompatActivity {
    final static String TAG = "MyEventActivity";
    EventListAdapter adapter;
    RecyclerView recyclerView;
    final ArrayList<Event> myEvent = new ArrayList<Event>();
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);

        recyclerView = (RecyclerView) findViewById(R.id.events_list_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventListAdapter(getApplicationContext(), myEvent);
        recyclerView.setAdapter(adapter);

        FirebaseManager manager = new FirebaseManager();
        mAuth = FirebaseAuth.getInstance();
        manager.getUsersEvents(mAuth.getCurrentUser().getUid(), new EventCallback() {
            @Override
            public void onCallback(Event events) {
                myEvent.add(events);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
