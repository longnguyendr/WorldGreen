package com.example.worldgreen.Events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.MainActivity;
import com.example.worldgreen.R;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CreateEventActivity extends AppCompatActivity {

    private static final String TAG = "CreateEventActivity";
    private EditText inputTitle, inputDescription;
    private Button btnCreateEvent;
    Report report ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        CreateEventButton();

    }

    void CreateEventButton() {
        btnCreateEvent = findViewById(R.id.create_event_button);
        inputTitle = findViewById(R.id.create_event_title);
        inputDescription = findViewById(R.id.create_event_description);

        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                report = (Report) getIntent().getSerializableExtra("report");

                FirebaseManager manager = new FirebaseManager();
                Event e = new Event(inputDescription.getText().toString() ,inputTitle.getText().toString(), new Date(System.currentTimeMillis()), report);

                try {
                    manager.saveEvent(e);
                    Toast.makeText(CreateEventActivity.this, "create event successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e1) {
                    Toast.makeText(CreateEventActivity.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
                    e1.printStackTrace();
                }
            }
        });
    }

}
