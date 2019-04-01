package com.example.worldgreen.Reports;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.R;

import java.io.Serializable;

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
    }

    void setTestingFirstImage(byte[] photo) {
        ImageView imageView = findViewById(R.id.test_first_image);
        Bitmap img = BitmapFactory.decodeByteArray(photo,0,photo.length);
        imageView.setImageBitmap(img);
    }

}