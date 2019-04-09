package com.example.worldgreen.Reports;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.Events.CreateEventActivity;
import com.example.worldgreen.FirebaseManager.EventCallback;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.R;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;

public class DetailReportActivity extends AppCompatActivity {

    static final String TAG = "DetailReportActivity";
    private LinearLayout gallery;
    private LayoutInflater layoutInflater;
    Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_report);

        report = (Report) getIntent().getSerializableExtra("report");
        Log.d(TAG, "onCreate: photo size" + report.getPhotos().size());

        gallery = findViewById(R.id.photo_gallery);
        layoutInflater = LayoutInflater.from(this);

        setupButtons();
        displayData();
    }

    private void setupButtons() {
        final FirebaseManager manager = new FirebaseManager();
        Button createEvent = findViewById(R.id.test_create_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DetailReportActivity.this, CreateEventActivity.class);
                i.putExtra("report", report);
                startActivity(i);
            }
        });
    }

    private void displayData() {
        for (byte[] photo: report.getPhotos()) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            addPhoto(bitmap);
        }

        TextView description = findViewById(R.id.description);
        TextView accessibility = findViewById(R.id.accessibility);
        TextView address = findViewById(R.id.address);
        TextView position = findViewById(R.id.gps_coordinates);
        TextView size = findViewById(R.id.size);

        description.setText(report.getDescription());
        if (report.isAccessibleByCar()) {
            accessibility.setText("Is accessible by car");
        } else {
            accessibility.setText("Is not accessible by car");
        }
        address.setText("not implemented address yet");
        position.setText("lat: " + report.getLatitude() + ", long: " + report.getLongitude());
        size.setText(report.getSize());

    }

    private void addPhoto(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        View newView = layoutInflater.inflate(R.layout.create_report_item, gallery, false);
        ImageView imageView = newView.findViewById(R.id.create_report_item_imageView);
        imageView.setImageBitmap(photo);
        gallery.addView(newView);
    }



}