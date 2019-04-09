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

        prepareView();
        getUsersEvent();
    }
    protected void prepareView () {
        recyclerView = (RecyclerView) findViewById(R.id.events_list_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

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
            public void onCallback(Event events) {
                myEvent.add(events);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
