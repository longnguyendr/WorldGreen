package com.example.worldgreen.FirebaseManager;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.DataModel.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseManager {

    static final String TAG = "Firebase Manager";

    /**
     *
     * @param report
     * @throws Exception
     *
     * Insert report which you want to save to the database
     */
    public void saveReport(Report report) throws Exception {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = database.getReference(user.getUid()).child("reports").push();

        HashMap<String, Object> data = new HashMap<>();
        data.put("longitude", report.getLongitude());
        data.put("latitude", report.getLatitude());
        data.put("description", report.getDescription());

        ref.setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void createEvent(Event event) throws Exception {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = database.getReference(user.getUid()).child("events").push();

        HashMap<String, Object> data = new HashMap<>();
        data.put("title", event.getTitle());
        data.put("description", event.getDescription());
        data.put("date", event.getDate());
        data.put("reportKey", event.getReport().getKey());
        data.put("reportCreatorUid", event.getReport().getCreatorUid());

        ref.setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     *
     * @param reportCallback
     *
     * onDataChange is asynchronous method, use ReportCallback for updating values.
     */

    public void getAllReports(final ReportCallback reportCallback) {
        Log.d(TAG, "getAllReports: PRESSED");

        final ArrayList<Report> reports = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: IN FOR");
                    Log.d(TAG, "USER: " + user.getKey());
                    for (DataSnapshot report : user.child("reports").getChildren()) {
                        String description = report.child("description").getValue(String.class);
                        Double longitude = report.child("longitude").getValue(Double.class);
                        Double latitude = report.child("latitude").getValue(Double.class);
                        String ownerId = user.getKey();
                        String reportKey = report.getKey();
                        Report r = new Report(longitude, latitude, description, reportKey, ownerId);
                        reports.add(r);
                    }
                }
                reportCallback.onCallback(reports);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        rootRef.addListenerForSingleValueEvent(eventListener);

    }

    public void getAllEvents(final EventCallback eventCallback) {
        Log.d(TAG, "getAllEvents: called");
        final ArrayList<Event> events = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    for (DataSnapshot event : user.child("events").getChildren()) {

                        final String title = event.child("title").getValue(String.class);
                        final String description = event.child("description").getValue(String.class);
                        final String date = event.child("date").getValue(String.class);
                        String reportKey = event.child("reportKey").getValue(String.class);
                        String reportCreatorUid = event.child("reportCreatorUid").getValue(String.class);
                        Log.d(TAG, "onDataChange: calling getCurrentReport");
                        getCurrentReport(reportKey, reportCreatorUid, new CurrentReportCallback() {
                            @Override
                            public void onCallback(Report report) {
                                Event e = new Event(description,title,date,report);
                                events.add(e);
                            }
                        });
                    }
                }
                Log.d(TAG, "onDataChange: calling eventCallback");
                eventCallback.onCallback(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootRef.addListenerForSingleValueEvent(eventListener);
    }

    public void getCurrentReport(final String reportKey, final String creatorUserId, final CurrentReportCallback currentReportCallback) {
        Log.d(TAG, "getCurrentReport: IM in current report!");
        Log.d(TAG, "getCurrentReport: CREATOR ID: " + creatorUserId);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference().child(creatorUserId);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot report : dataSnapshot.child("reports").getChildren()) {
                    Log.d(TAG, "onDataChange: IM IN FOR");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootRef.addValueEventListener(eventListener);
    }

    /**
     *
     * @param userId
     * @param reportCallback
     * Insert users ID - function can be used (in future) also to see others users reports, not just mine.
     */

    public void getUsersReports(final String userId, final ReportCallback reportCallback) {
        final ArrayList<Report> reports = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference().child(userId);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot report : dataSnapshot.child("reports").getChildren()) {
                    String description = report.child("description").getValue(String.class);
                    Double longitude = report.child("longitude").getValue(Double.class);
                    Double latitude = report.child("latitude").getValue(Double.class);
                    String reportKey = report.getKey();
                    Report r = new Report(longitude, latitude, description, reportKey, userId);
                    reports.add(r);
                }
                reportCallback.onCallback(reports);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        rootRef.addListenerForSingleValueEvent(eventListener);
    }

    

    public void getUsersEvents(final String userId, final EventCallback eventCallback) {
        final ArrayList<Event> events = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference().child(userId);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot event : dataSnapshot.child("events").getChildren()) {

                    final String title = event.child("title").getValue(String.class);
                    final String description = event.child("description").getValue(String.class);
                    final String date = event.child("date").getValue(String.class);
                    String reportKey = event.child("reportKey").getValue(String.class);
                    String reportCreatorUid = event.child("reportCreatorUid").getValue(String.class);

                    getCurrentReport(reportKey, reportCreatorUid, new CurrentReportCallback() {
                        @Override
                        public void onCallback(Report report) {
                            Event e = new Event(description,title,date,report);
                            events.add(e);
                        }
                    });

                }
                eventCallback.onCallback(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootRef.addValueEventListener(eventListener);
    }




}
