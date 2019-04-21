package com.example.worldgreen.Events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.DataModel.Report;
import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.R;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateEventActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "CreateEventActivity";


    Report report ;
    Timestamp timestamp;
    Calendar c = Calendar.getInstance();
    Button createButton;
    Button chooseTimeButton;
    Button chooseDateButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        createButton = findViewById(R.id.create_event_button);
        progressBar = findViewById(R.id.progressBar_event);
        chooseDateButton = findViewById(R.id.choose_date_button);
        chooseTimeButton = findViewById(R.id.choose_time_button);
        setupUI();
    }

    //region UI & UX methods
    //----------------------------------------------------------------------------------------------

    private void setupUI() {
        setProgressBar(false);
        setupCreateEventButton();
        setupChooseDateButton();
        setupChooseTimeButton();
    }

    private void setProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void resetCreateButton() {
        setProgressBar(false);
        createButton.setClickable(true);
        chooseDateButton.setClickable(true);
        chooseTimeButton.setClickable(true);
    }

    //endregion

    //region setup methods
    //----------------------------------------------------------------------------------------------

    private void setupChooseDateButton() {

        chooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void setupChooseTimeButton() {
        chooseTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
    }

    private void setupCreateEventButton() {

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createButtonPressed();
            }
        });
    }

    private void createButtonPressed() {
        setProgressBar(true);
        createButton.setClickable(false);
        chooseDateButton.setClickable(false);
        chooseTimeButton.setClickable(false);
        try {
            createEvent();
        } catch (CreateEventException e) {
            Toast.makeText(CreateEventActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    //endregion

    //region UI methods
    //----------------------------------------------------------------------------------------------

    public void resetUI() {
        this.finish();
    }


    //region date and time methods
    //----------------------------------------------------------------------------------------------


    private void showDatePickerDialog() {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "datePicker");

    }

    private void showTimePickerDialog() {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
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

    //endregion

    //region Create event methods
    //----------------------------------------------------------------------------------------------

    private void createEvent() throws CreateEventException {
        EditText inputTitle, inputDescription;
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
        manager.saveEvent(e, this);
    }

    //endregion
}
