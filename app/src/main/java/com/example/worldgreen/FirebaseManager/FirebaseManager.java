package com.example.worldgreen.FirebaseManager;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.DataModel.Report;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
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
        data.put("title", report.getTitle());
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

    private void savePhotos(String reportKey, ArrayList<byte[]> photos) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        for (int i = 0; i < photos.size(); i++) {
            StorageReference ref = storage.getReference(user.getUid()).child("reports").child(reportKey).child(String.valueOf(i));
            
            ref.putBytes(photos.get(i))
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

    private void getAllPhotos(String uid, String repKey, int numberOfImages, final ReportPhotosCallback reportPhotosCallback) {
        final ArrayList<byte[]> photos = new ArrayList<>();
        final long ONE_MEGABYTE = 1024 * 1024;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        for (int i = 0; i < numberOfImages; i++) {
            StorageReference ref = storage.getReference().child(uid).child("reports").child(repKey).child(String.valueOf(i));
            ref.getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.d(TAG, "onSuccess: downloaded image");
//                            Bitmap img = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
//                            ProxyBitmap proxy = new ProxyBitmap(img);
                            photos.add(bytes);
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
                        final String getTitle = report.child("title").getValue(String.class);
                        final Double longitude = report.child("longitude").getValue(Double.class);
                        final Double latitude = report.child("latitude").getValue(Double.class);
                        final String ownerId = user.getKey();
                        final String reportKey = report.getKey();
                        final int numberOfPhotos = report.child("numberOfPhotos").getValue(int.class);
                        final String size = report.child("size").getValue(String.class);
                        final boolean isAccessibleByCar = report.child("isAccessibleByCar").getValue(boolean.class);
                        getAllPhotos(user.getKey(), reportKey, numberOfPhotos, new ReportPhotosCallback() {
                            @Override
                            public void onCallback(ArrayList<byte[]> photos) {
                                if (photos.size() == numberOfPhotos) {
                                    Log.d(TAG, "onCallback: on data change on callback photos called");
                                    String title;
                                    if (getTitle == null) {
                                        title = "Unknown title";
                                    } else {
                                        title = getTitle;
                                    }
                                    Report r = new Report(longitude, latitude, description, title, ownerId ,reportKey, photos, size, isAccessibleByCar);
                                    reports.add(r);
                                    reportCallback.onCallback(r);
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
        rootRef.addListenerForSingleValueEvent(eventListener);
    }


    /**
     *
     * @param userId - id of user whose reports we want to fetch
     * @param
     */

    public void getUsersReports(final String userId, final ReportCallback reportCallback) {
        final ArrayList<Report> reports = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference().child(userId);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot report : dataSnapshot.child("reports").getChildren()) {
                    final String description = report.child("description").getValue(String.class);
                    final String getTitle = report.child("title").getValue(String.class);
                    final Double longitude = report.child("longitude").getValue(Double.class);
                    final Double latitude = report.child("latitude").getValue(Double.class);
                    final String reportKey = report.getKey();
                    final int numberOfPhotos = report.child("numberOfPhotos").getValue(int.class);
                    final String size = report.child("size").getValue(String.class);
                    final boolean isAccessibleByCar = report.child("isAccessibleByCar").getValue(boolean.class);
                    getAllPhotos(userId, reportKey, numberOfPhotos, new ReportPhotosCallback() {
                        @Override
                        public void onCallback(ArrayList<byte[]> photos) {
                            String title;
                            if (getTitle == null) {
                                title = "Unknown title";
                            } else {
                                title = getTitle;
                            }
                            if (photos.size() == numberOfPhotos) {
                                Report r = new Report(longitude, latitude, description, title, userId, reportKey, photos, size, isAccessibleByCar);
                                reports.add(r);
                                reportCallback.onCallback(r);
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        rootRef.addListenerForSingleValueEvent(eventListener);
    }



    public void getCurrentReport(final String reportKey, final String creatorUserId, final ReportCallback reportCallback) {
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
                        final String getTitle = report.child("title").getValue(String.class);
                        final int numberOfPhotos = report.child("numberOfPhotos").getValue(int.class);
                        final String size = report.child("size").getValue(String.class);
                        final boolean isAccessibleByCar = report.child("isAccessibleByCar").getValue(boolean.class);
                        getAllPhotos(creatorUserId, reportKey, numberOfPhotos, new ReportPhotosCallback() {
                            @Override
                            public void onCallback(ArrayList<byte[]> photos) {
                                String title;
                                if (getTitle == null) {
                                    title = "Unknown title";
                                } else {
                                    title = getTitle;
                                }
                                if (photos.size() == numberOfPhotos) {
                                    Report r = new Report(longitude,latitude,description, title, creatorUserId,reportKey, photos, size, isAccessibleByCar);
                                    reportCallback.onCallback(r);
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
        data.put("date", event.getTimestamp().getTime());
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
        Log.d(TAG, "getAllEvents: called");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: called");
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    for (DataSnapshot event : user.child("events").getChildren()) {

                        Log.d(TAG, "onDataChange: in event for loop");

                        final String title = event.child("title").getValue(String.class);
                        final String description = event.child("description").getValue(String.class);
                        final Long longTimestamp = event.child("date").getValue(long.class);
                        final int participantsNumber = numberOfParticipants(event);
                        final boolean amIParticipating = amIParticipating(event, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        final Timestamp timestamp = new Timestamp(longTimestamp);
                        final String creatorId = user.getKey();
                        final String eventId = event.getKey();


                        String reportKey = event.child("reportKey").getValue(String.class);
                        String reportCreatorUid = event.child("reportCreatorUid").getValue(String.class);
                        getCurrentReport(reportKey, reportCreatorUid, new ReportCallback() {
                            @Override
                            public void onCallback(Report report) {
                                if (report.getNumberOfPhotos() == report.getPhotos().size()) {

                                    Event e = new Event(description, creatorId, eventId, amIParticipating, participantsNumber, title,timestamp,report);
                                    Log.d(TAG, "onCallback: EVENT CALLBACK called");
                                    eventCallback.onCallback(e);

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
        rootRef.addListenerForSingleValueEvent(eventListener);
    }

    private boolean amIParticipating(DataSnapshot event, String myId) {

        for (DataSnapshot participants : event.child("participants").getChildren()) {
            if (myId.equals(participants.getKey())) {
                return true;
            }
        }
        return false;
    }

    private int numberOfParticipants(DataSnapshot event) {
        int counter = 0;

        for (DataSnapshot participants: event.child("participants").getChildren()) {
            counter ++;
        }

        return counter;
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
                Log.d(TAG, "onDataChange: called");
                for (DataSnapshot event : dataSnapshot.child("events").getChildren()) {

                    final String title = event.child("title").getValue(String.class);
                    final String description = event.child("description").getValue(String.class);
                    final Long longTimestamp = event.child("date").getValue(long.class);
                    final int participantsNumber = numberOfParticipants(event);
                    final boolean amIParticipating = amIParticipating(event, FirebaseAuth.getInstance().getCurrentUser().getUid());

                    final Timestamp timestamp = new Timestamp(longTimestamp);
                    final String eventId = event.getKey();

                    String reportKey = event.child("reportKey").getValue(String.class);
                    String reportCreatorUid = event.child("reportCreatorUid").getValue(String.class);
                    Log.d(TAG, "onDataChange: calling getCurrentReport");
                    getCurrentReport(reportKey, reportCreatorUid, new ReportCallback() {
                        @Override
                        public void onCallback(Report report) {
                            Log.d(TAG, "onCallback: I got report from callback!");

                            if (report.getNumberOfPhotos() == report.getPhotos().size()) {

                                Event e = new Event(description, userId, eventId, amIParticipating, participantsNumber, title,timestamp,report);
                                events.add(e);
                                eventCallback.onCallback(e);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        rootRef.addListenerForSingleValueEvent(eventListener);
    }

    public void goingToEvent(FirebaseUser user, Event event, final FirebaseManagerCompleteMessage message){

        addUserToEvent(user, event, new FirebaseManagerCompleteMessage() {
            @Override
            public void onCallback(String completeMessage) {
                message.onCallback(completeMessage);
            }
        });

        addEventToUser(user.getUid(), event, new FirebaseManagerCompleteMessage() {
            @Override
            public void onCallback(String completeMessage) {
                message.onCallback(completeMessage);
            }
        });

    }

    public void removeFromGoing(FirebaseUser user, Event event, final FirebaseManagerCompleteMessage message) {

        removeUserFromEvent(user, event, new FirebaseManagerCompleteMessage() {
            @Override
            public void onCallback(String completeMessage) {
                message.onCallback(completeMessage);
            }
        });

        removeEventFromUser(user.getUid(), event, new FirebaseManagerCompleteMessage() {
            @Override
            public void onCallback(String completeMessage) {
                message.onCallback(completeMessage);
            }
        });

    }

    private void removeUserFromEvent(FirebaseUser user, Event event, final FirebaseManagerCompleteMessage completeMessage) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(event.getCreatorId()).child("events").child(event.getId()).child("participants").child(user.getUid());

        ref.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        completeMessage.onCallback("Success remove user from event");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeMessage.onCallback(e.getMessage());
                    }
                });
    }

    private void removeEventFromUser(String userId, Event event, final FirebaseManagerCompleteMessage completeMessage) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(userId).child("participating").child(event.getId());
        ref.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        completeMessage.onCallback("Success remove event from user");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeMessage.onCallback(e.getMessage());
                    }
                });
    }

    private void addEventToUser(String userId, Event event, final FirebaseManagerCompleteMessage completeMessage){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(userId).child("participating").child(event.getId());

        HashMap<String, Object> data = new HashMap<>();
        data.put("creatorId", event.getCreatorId());
        ref.setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        completeMessage.onCallback("Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeMessage.onCallback(e.getMessage());
                    }
                });

    }

    private void addUserToEvent(FirebaseUser user, final Event event, final FirebaseManagerCompleteMessage completeMessage){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(event.getCreatorId()).child("events").child(event.getId()).child("participants").child(user.getUid());

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());

        ref.setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        completeMessage.onCallback("Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeMessage.onCallback(e.getMessage());
                    }
                });
    }


    //endregion







}
