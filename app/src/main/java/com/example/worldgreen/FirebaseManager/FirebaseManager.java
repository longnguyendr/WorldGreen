package com.example.worldgreen.FirebaseManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseManager {

    static final String TAG = "Firebase Manager";


    //region Report methods
    //----------------------------------------------------------------------------------------------

    /**
     *
     * @param report which you want to save to the database
     * @throws Exception
     *
     */
    public void saveReport(Report report) throws Exception {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = database.getReference(user.getUid()).child("reports").push();
        HashMap<String, Object> data = new HashMap<>();
        data.put("longitude", report.getLongitude());
        data.put("latitude", report.getLatitude());
        data.put("description", report.getDescription());
        data.put("numberOfPhotos", report.getNumberOfPhotos());
        data.put("size", report.getSize());
        data.put("isAccessibleByCar", report.isAccessibleByCar());
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


        savePhotos(ref.getKey(), report.getPhotos());
    }

    private void savePhotos(String reportKey, ArrayList<Bitmap> photos) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        for (int i = 0; i < photos.size(); i++) {
            StorageReference ref = storage.getReference(user.getUid()).child("reports").child(reportKey).child(String.valueOf(i));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photos.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onSuccess: photo uploaded!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: FAILED UPLOAD PHOTO");
                        }
                    });
        }
    }

    private void getPhotos(String uid, String repKey, int numberOfImages, final ReportPhotosCallback reportPhotosCallback) {
        final ArrayList<Bitmap> photos = new ArrayList<>();
        final long ONE_MEGABYTE = 1024 * 1024;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        for (int i = 0; i < numberOfImages; i++) {
            StorageReference ref = storage.getReference().child(uid).child("reports").child(repKey).child(String.valueOf(i));
            ref.getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.d(TAG, "onSuccess: downloaded image");
                            Bitmap img = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                            photos.add(img);
                            reportPhotosCallback.onCallback(photos);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: failed downloading image");
                        }
                    });
        }
    }

    /**
     *
     * @param reportCallback is called every time method get new photo of the report
     *
     *                       so if there is 3 reports and every report has 2 photos, reportCallback will be called 3*2 times
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
                        final String description = report.child("description").getValue(String.class);
                        final Double longitude = report.child("longitude").getValue(Double.class);
                        final Double latitude = report.child("latitude").getValue(Double.class);
                        final String ownerId = user.getKey();
                        final String reportKey = report.getKey();
                        final int numberOfPhotos = report.child("numberOfPhotos").getValue(int.class);
                        final String size = report.child("size").getValue(String.class);
                        final boolean isAccessibleByCar = report.child("isAccessibleByCar").getValue(boolean.class);
                        getPhotos(user.getKey(), reportKey, numberOfPhotos, new ReportPhotosCallback() {
                            @Override
                            public void onCallback(ArrayList<Bitmap> photos) {
                                Log.d(TAG, "onCallback: on data change on callback photos called");
                                if (photos.size() == numberOfPhotos) {
                                    Report r = new Report(longitude, latitude, description, ownerId ,reportKey, photos, size, isAccessibleByCar);
                                    reports.add(r);
                                    reportCallback.onCallback(reports);
                                }
                            }
                        });

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


    /**
     *
     * @param userId - id of user whose reports we want to fetch
     * @param reportCallback - is called every time method get new photo of the report
     *                       so if there is 3 reports and every report has 2 photos, reportCallback will be called 3*2 times
     */

    public void getUsersReports(final String userId, final ReportCallback reportCallback) {
        final ArrayList<Report> reports = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference().child(userId);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot report : dataSnapshot.child("reports").getChildren()) {
                    final String description = report.child("description").getValue(String.class);
                    final Double longitude = report.child("longitude").getValue(Double.class);
                    final Double latitude = report.child("latitude").getValue(Double.class);
                    final String reportKey = report.getKey();
                    final int numberOfPhotos = report.child("numberOfPhotos").getValue(int.class);
                    final String size = report.child("size").getValue(String.class);
                    final boolean isAccessibleByCar = report.child("isAccessibleByCar").getValue(boolean.class);
                    getPhotos(userId, reportKey, numberOfPhotos, new ReportPhotosCallback() {
                        @Override
                        public void onCallback(ArrayList<Bitmap> photos) {
                            if (photos.size() == numberOfPhotos) {
                                Report r = new Report(longitude, latitude, description, userId, reportKey, photos, size, isAccessibleByCar);
                                reports.add(r);
                                reportCallback.onCallback(reports);
                            }

                        }
                    });

                }
                reportCallback.onCallback(reports);
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
                    if (reportKey.equals(report.getKey())) {
                        final int longitude = report.child("longitude").getValue(int.class);
                        final int latitude = report.child("latitude").getValue(int.class);
                        final String description = report.child("description").getValue(String.class);
                        final int numberOfPhotos = report.child("numberOfPhotos").getValue(int.class);
                        final String size = report.child("size").getValue(String.class);
                        final boolean isAccessibleByCar = report.child("isAccessibleByCar").getValue(boolean.class);
                        getPhotos(creatorUserId, reportKey, numberOfPhotos, new ReportPhotosCallback() {
                            @Override
                            public void onCallback(ArrayList<Bitmap> photos) {
                                if (photos.size() == numberOfPhotos) {
                                    Report r = new Report(longitude,latitude,description,creatorUserId,reportKey, photos, size, isAccessibleByCar);
                                    currentReportCallback.onCallback(r);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootRef.addValueEventListener(eventListener);
    }



    //endregion


    //region Event methods
    //----------------------------------------------------------------------------------------------

    public void saveEvent(Event event) throws Exception {
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
     * @param eventCallback callBack waits until all photos of report are downloaded and then is called
     *                      so if there are 3 events, callback should be called 3 times
     *
     */

    public void getAllEvents(final EventCallback eventCallback) {
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
                        getCurrentReport(reportKey, reportCreatorUid, new CurrentReportCallback() {
                            @Override
                            public void onCallback(Report report) {
                                if (report.getNumberOfPhotos() == report.getPhotos().size()) {
                                    Event e = new Event(description,title,date,report);
                                    events.add(e);
                                    eventCallback.onCallback(events);
                                }
                            }
                        });
                    }
                }
                eventCallback.onCallback(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootRef.addListenerForSingleValueEvent(eventListener);
    }

    /**
     *
     * @param userId id of user whose events we want to fetch
     * @param eventCallback callBack waits until all photos of report are downloaded and then is called
     *                      so if there are 3 events, callback should be called 3 times
     *
     */
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
                    Log.d(TAG, "onDataChange: calling getCurrentReport");
                    getCurrentReport(reportKey, reportCreatorUid, new CurrentReportCallback() {
                        @Override
                        public void onCallback(Report report) {
                            Log.d(TAG, "onCallback: I got report from callback!");
                            if (report.getNumberOfPhotos() == report.getPhotos().size()) {
                                Event e = new Event(description,title,date,report);
                                events.add(e);
                                eventCallback.onCallback(events);
                            }
                        }
                    });
                }
                eventCallback.onCallback(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootRef.addListenerForSingleValueEvent(eventListener);
    }

    //endregion







}
