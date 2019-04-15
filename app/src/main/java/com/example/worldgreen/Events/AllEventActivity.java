package com.example.worldgreen.Events;

import android.app.usage.UsageEvents;
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
import java.util.Iterator;
import java.util.List;

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


    /**
     *  When event is added to participants in event, event is updated and onDataChange in FirebaseManager is called
     *  -> also onCallback is called again. That means that onCallback we have new event.
     *  But this event is already in allEvent arrayList - the only difference between new one and old one is participants
     *  if even already exist (we have in array list event with same id) we will replace this event.
     */

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
                if (eventExists(event)) {
                    replaceEvent(event);
                } else {
                    allEvent.add(event);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private boolean eventExists(Event newEvent) {
        for (Event event: allEvent) {
            if (event.getId().equals(newEvent.getId())) {
                return true;
            }
        }
        return false;
    }

    private void replaceEvent(Event newEvent) {

        List<Event> toRemove = new ArrayList<Event>();
        List<Event> toAdd = new ArrayList<Event>();
        for (Event event: allEvent) {
            if (event.getId().equals(newEvent.getId())) {
                toRemove.add(event);
                toAdd.add(newEvent);
            }
        }
        allEvent.removeAll(toRemove);
        allEvent.addAll(toAdd);
    }
}
