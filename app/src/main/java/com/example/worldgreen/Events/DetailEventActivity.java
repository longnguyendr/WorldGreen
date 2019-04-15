package com.example.worldgreen.Events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldgreen.DataModel.Event;

import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.FirebaseManager.FirebaseManagerCompleteMessage;
import com.example.worldgreen.R;
import com.example.worldgreen.Reports.DetailReportActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class DetailEventActivity extends AppCompatActivity {
    final static String TAG = "DetailEventActivity";
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        event = (Event) getIntent().getSerializableExtra("event");
        setupShowReportButton();
        setupGoingButton();
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

    private void setupGoingButton() {
        Button goingButton = findViewById(R.id.going_button);



        goingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseManager firebaseManager = new FirebaseManager();

                if (event.amIParticipating()) {
                    firebaseManager.removeFromGoing(FirebaseAuth.getInstance().getCurrentUser(), event, new FirebaseManagerCompleteMessage() {
                        @Override
                        public void onCallback(String completeMessage) {
                            Toast.makeText(getApplicationContext(), completeMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    firebaseManager.goingToEvent(FirebaseAuth.getInstance().getCurrentUser(), event, new FirebaseManagerCompleteMessage() {
                        @Override
                        public void onCallback(String completeMessage) {
                            Toast.makeText(getApplicationContext(), completeMessage, Toast.LENGTH_SHORT).show();

                        }
                    });
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

            titleTextView.setText(event.getTitle() + " " + event.amIParticipating());
            descriptionTextView.setText(event.getDescription());

            Date date = new Date(event.getTimestamp().getTime());
            String formattedDate = SimpleDateFormat.getDateTimeInstance().format(date);
            dateTextView.setText(formattedDate);

        }
    }

    //endregion
}
