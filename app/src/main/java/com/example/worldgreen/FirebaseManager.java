package com.example.worldgreen;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

    void saveReport(Report report) throws Exception {


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

    ArrayList<Report> getAllReports() {
        Log.d(TAG, "getAllReports: PRESSED");
        final ArrayList<Report> reports = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    //get user
                    Log.d(TAG, "USER: " + user.getKey());
                    for (DataSnapshot report : user.child("reports").getChildren()) {
                        String description = report.child("description").getValue(String.class);
                        Double longitude = report.child("longitude").getValue(Double.class);
                        Double latitude = report.child("latitude").getValue(Double.class);
                        Report r = new Report(longitude, latitude, description);
                        reports.add(r);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        rootRef.addListenerForSingleValueEvent(eventListener);

        return reports;
    }

    void getUsersReports() {

    }

}
