package com.example.worldgreen.FirebaseManager;

import com.example.worldgreen.DataModel.Event;

import java.util.ArrayList;

public interface EventCallback {
    void onCallback(ArrayList<Event> events);
}
