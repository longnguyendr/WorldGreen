package com.example.worldgreen.Events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldgreen.DataModel.Event;

import com.example.worldgreen.R;
import com.example.worldgreen.Reports.DetailReportActivity;

public class DetailEventActivity extends AppCompatActivity {
    final static String TAG = "DetailEventActivity";
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        event = (Event) getIntent().getSerializableExtra("event");
        setupShowReportButton();
        updateUI();
    }

    //region setup methods
    //----------------------------------------------------------------------------------------------

    private void setupShowReportButton() {
        Button showReportButton = findViewById(R.id.show_report_button);
        showReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getReport() != null) {
                    Intent i = new Intent(DetailEventActivity.this, DetailReportActivity.class);
                    i.putExtra("report", event.getReport());
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //endregion


    //region UI methods
    //----------------------------------------------------------------------------------------------

    private void updateUI() {
        if (event != null) {
            TextView titleTextView = findViewById(R.id.event_title);
            TextView descriptionTextView = findViewById(R.id.event_description);
            TextView dateTextView = findViewById(R.id.event_date);

            titleTextView.setText(event.getTitle());
            descriptionTextView.setText(event.getDescription());
            dateTextView.setText(event.getDate().toString());

        }
    }

    //endregion
}
