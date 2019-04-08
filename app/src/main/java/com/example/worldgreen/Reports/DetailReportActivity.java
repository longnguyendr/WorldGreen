package com.example.worldgreen.Reports;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.Events.CreateEventActivity;
import com.example.worldgreen.FirebaseManager.EventCallback;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.R;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;

public class DetailReportActivity extends AppCompatActivity {

    static final String TAG = "DetailReprotActivity";
    LinearLayout gallery;
    LayoutInflater layoutInflater;
    Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_report);

        report = (Report) getIntent().getSerializableExtra("report");

        if (report != null) {
            Log.d(TAG, "onCreate: report: " + report.getDescription());
            if (report.getPhotos() != null) {
                Log.d(TAG, "onCreate: photo array size " + report.getPhotos().size());
                setTestingFirstImage(report.getPhotos().get(0));
            } else {
                Log.d(TAG, "onCreate: photos is null");
            }
            
//            setTestingFirstImage(report.getPhotos().get(0));
        }

        gallery = (LinearLayout) findViewById(R.id.photo_gallery);
        layoutInflater = LayoutInflater.from(this);

        setupButtons();
    }

    void setTestingFirstImage(byte[] photo) {
        ImageView imageView = findViewById(R.id.test_first_image);
        Bitmap img = BitmapFactory.decodeByteArray(photo,0,photo.length);
        imageView.setImageBitmap(img);
    }

    void setupButtons() {
        final FirebaseManager manager = new FirebaseManager();
        Button createEvent = findViewById(R.id.test_create_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DetailReportActivity.this, CreateEventActivity.class);
                i.putExtra("report", report);
                startActivity(i);

//                Event e = new Event("tes event","current date", new Date(System.currentTimeMillis()) , report);
//                try {
//                    manager.saveEvent(e);
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
            }
        });

//        Button getEvent = findViewById(R.id.test_get_event);
//        getEvent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                manager.getAllEvents(new EventCallback() {
//                    @Override
//                    public void onCallback(Event event) {
//                        Log.d(TAG, "onCallback: I GOT EVENT " + event.getTitle() + " time: " + event.getDate());
//                    }
//                });
//            }
//        });
    }



}