package com.example.worldgreen.Events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.FirebaseManager.EventCallback;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MyEventActivity extends AppCompatActivity {
    final static String TAG = "MyEventActivity";
    EventListAdapter adapter;
    RecyclerView recyclerView;
    final ArrayList<Event> myEvent = new ArrayList<Event>();
    FirebaseAuth mAuth;
    boolean participating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);
        participating = getIntent().getBooleanExtra("participating", false);
        prepareView();

        if (participating) {
            getParticipatingEvent();
        } else {
            getUsersEvent();
        }
    }
    protected void prepareView () {
        recyclerView = (RecyclerView) findViewById(R.id.events_list_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                myEvent.add(event);
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
}
