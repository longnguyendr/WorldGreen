package com.example.worldgreen.FirebaseManager;

import com.example.worldgreen.DataModel.Report;

import java.util.ArrayList;

public interface ReportCallback {
    void onCallback(ArrayList<Report> reports);
}
