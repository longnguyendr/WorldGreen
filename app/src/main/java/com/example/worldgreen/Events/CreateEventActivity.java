package com.example.worldgreen.Events;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.MainActivity;
import com.example.worldgreen.R;

import org.w3c.dom.Text;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CreateEventActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "CreateEventActivity";
    private EditText inputTitle, inputDescription;
    private Button btnCreateEvent;
    Report report ;
    Timestamp timestamp;
    Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        CreateEventButton();
        setupChooseDateButton();
        setupChooseTimeButton();
    }

    private void setupChooseDateButton() {
        Button chooseDateButton = findViewById(R.id.choose_date_button);
        chooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void setupChooseTimeButton() {
        Button chooseTimeButton = findViewById(R.id.choose_time_button);
        chooseTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "datePicker");

    }

    private void showTimePickerDialog() {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
    }

    void CreateEventButton() {
        btnCreateEvent = findViewById(R.id.create_event_button);


        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createEvent();
                } catch (CreateEventException e) {
                    Toast.makeText(CreateEventActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
        });
    }

    private void createEvent() throws CreateEventException {
        inputTitle = findViewById(R.id.create_event_title);
        inputDescription = findViewById(R.id.create_event_description);
        report = (Report) getIntent().getSerializableExtra("report");

        if (inputTitle.length() < 5 || inputTitle.length() > 20) {
            throw new CreateEventException("Title has to have at least 5 characters and max 20 characters");
        }

        if (inputDescription.length() < 5) {
            throw new CreateEventException("Description has to have at least 5 characters");
        }

        if (report == null) {
            throw new CreateEventException("No report selected, something went wrong.");
        }

        if (timestamp == null) {
            throw new CreateEventException("Select date, please");
        }

        long currentTimestamp = System.currentTimeMillis();
        long selectedTimestamp = timestamp.getTime();

        if (selectedTimestamp < currentTimestamp) {
            throw  new CreateEventException("Date has to be in future.");
        }

        long diff = selectedTimestamp - currentTimestamp;

        if (diff < 10 * 60 * 1000) {
            throw new CreateEventException("New event can start at least in 10 minutes, not sooner.");
        }


        FirebaseManager manager = new FirebaseManager();
        Event e = new Event(inputDescription.getText().toString() ,inputTitle.getText().toString(), timestamp, report);

        try {
            manager.saveEvent(e);
            Toast.makeText(CreateEventActivity.this, "create event successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e1) {
            Toast.makeText(CreateEventActivity.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
        }


    }

    private void updateDateTextView() {
        TextView dateTextView = findViewById(R.id.date_textView);

        if (timestamp == null) {
            timestamp = new Timestamp(c.getTimeInMillis());
        } else {
            timestamp.setTime(c.getTimeInMillis());
        }


        Date date = new Date(timestamp.getTime());
        String formattedDate = SimpleDateFormat.getDateTimeInstance().format(date);
        dateTextView.setText(formattedDate);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateTextView();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        updateDateTextView();
    }
}
