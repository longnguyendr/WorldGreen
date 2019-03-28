package com.example.worldgreen.Reports;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.worldgreen.R;

public class DetailReportActivity extends AppCompatActivity {

    LinearLayout gallery;
    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_report);

        gallery = (LinearLayout) findViewById(R.id.create_report_gallery);
        layoutInflater = LayoutInflater.from(this);
    }
}
