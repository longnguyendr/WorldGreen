package com.example.worldgreen.Reports;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.R;
import com.example.worldgreen.DataModel.Report;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class CreateReportActivity extends AppCompatActivity {

    static final String TAG = "CreateReportActivity";
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_REQUEST = 1;
    private ArrayList<Bitmap> photos = new ArrayList<>();
    Boolean isAccessibleByCar = null;
    String size;

    LinearLayout gallery;
    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        size = getResources().getStringArray(R.array.report_size)[0];
        gallery = findViewById(R.id.photo_gallery);
        layoutInflater = LayoutInflater.from(this);


        setupCreateButton();
        setupCameraButton();
        setupSpinner();

    }

    void setupCreateButton() {
        // image cannot be null!
        Button createButton = findViewById(R.id.save_report_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createReport();
                } catch (CreateReportException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    void setupSpinner() {
        Spinner spinner = findViewById(R.id.report_size);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.report_size, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                size = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void setupCameraButton() {
        Button cameraButton = findViewById(R.id.add_photo_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });
    }

    public void onAccessibilityRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.accessibility_option_yes:
                if (checked)
                    isAccessibleByCar = true;
                    break;
            case R.id.accessibility_option_no:
                if (checked)
                    isAccessibleByCar = false;
                    break;
        }
    }



    void createReport() throws CreateReportException {

        EditText description = findViewById(R.id.report_description);

        if (photos.isEmpty()) {
            throw new CreateReportException("Take at least 1 picture, please.");
        }

        if (description.length() < 5) {
            throw new CreateReportException("Write a longer description, please.");
        }

        if (isAccessibleByCar == null) {
            throw new CreateReportException("Check if report is accessible by car, please.");
        }


        Report report = new Report(123.1, 321.1, description.getText().toString(), photos, size, isAccessibleByCar);
        FirebaseManager manager = new FirebaseManager();
        try {
            manager.saveReport(report);
            Toast.makeText(getApplicationContext(), "Report saved!", Toast.LENGTH_SHORT).show();
            resetUI();
        } catch (Exception e) {
            Log.d(TAG, "createReport: " + e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    void resetUI() {
        EditText description = findViewById(R.id.report_description);
        description.setText(null);
    }

    //region Camera methods
    //----------------------------------------------------------------------------------------------


    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);

        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        dispatchGalleryIntent();
                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        dispatchTakePictureIntent();
                    }
                });
        myAlertDialog.show();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }

    private void dispatchGalleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            openCamera(data);
        }

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            openGallery(data);
        }
    }

    private void openCamera(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        addPhoto(imageBitmap);
    }

    private void openGallery(Intent data) {
        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            addPhoto(selectedImage);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void addPhoto(Bitmap photo) {
        photos.add(photo);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        View view = layoutInflater.inflate(R.layout.create_report_item, gallery, false);
        ImageView imageView = view.findViewById(R.id.create_report_item_imageView);
        imageView.setImageBitmap(photo);
        gallery.addView(view);
    }

    //endregion


//    TEST METHODS ejkejej junk

    //
//    void setupSaveEventButton() {
//        Button saveEventBtn = (Button) findViewById(R.id.create_test_save_event);
//        saveEventBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseManager manager = new FirebaseManager();
//                Event e = new Event("test event 1", "I am test", "12-03-2019",testReport);
//                try {
//                    manager.saveEvent(e);
//                } catch (Exception e1) {
//                    Log.d(TAG, "onClick: save event error");
//                    e1.printStackTrace();
//                }
//            }
//        });
//    }
//
//    void setupGetEventsButton() {
//        Button getEventBtn = (Button) findViewById(R.id.create_test_get_events);
//        getEventBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseManager manager = new FirebaseManager();
//                manager.getAllEvents(new EventCallback() {
//                    @Override
//                    public void onCallback(ArrayList<Event> events) {
//                        Log.d(TAG, "onCallback: get event called");
//                        Log.d(TAG, "onCallback: events size: " + events.size());
//                        if (events.size() >= 1) {
//                            Log.d(TAG, "onCallback: event photos size" + (events.size() - 1) + ". :" + events.get(events.size() - 1).getReport().getPhotos().size());
//                        }
//
//                    }
//                });
//            }
//        });
//    }


//    void setupGetButton() {
//        Button getButton = (Button) findViewById(R.id.create_report_get_all_reports);
//        getButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getReports();
//            }
//        });
//    }
//

    //    void getReports() {
//        FirebaseManager manager = new FirebaseManager();
//        manager.getAllReports(new ReportCallback() {
//            @Override
//            public void onCallback(ArrayList<Report> reports) {
//                Log.d(TAG, "onCallback: reports: " + reports.size());
//                if (reports.size() >= 1 ) {
////                    testReport = reports.get(1);
//                    Log.d(TAG, "onCallback: rep " + reports.get(reports.size() -1 ) + " number of images (got it from bitmap array) " + reports.get(reports.size() - 1).getPhotos().size());
//                    Log.d(TAG, "onCallback: rep " + reports.get(reports.size() -1 ) + " accessibility " + reports.get(reports.size() - 1).isAccessibleByCar());
//                    Log.d(TAG, "onCallback: rep " + reports.get(reports.size() -1 ) + " size " + reports.get(reports.size() - 1).getSize());
//                }
//            }
//        });
//    }

}
