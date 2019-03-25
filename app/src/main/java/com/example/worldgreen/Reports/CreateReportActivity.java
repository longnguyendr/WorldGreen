package com.example.worldgreen.Reports;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    RelativeLayout imageHolder;
    Bitmap bitmap;
    String selectedImagePath;
    LinearLayout gallery;
    LayoutInflater layoutInflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        gallery = (LinearLayout) findViewById(R.id.create_report_gallery);
        layoutInflater = LayoutInflater.from(this);



        setupCreateButton();
        setupCameraButton();
    }

    void setupCreateButton() {
        // image cannot be null!
        Button createButton = (Button) findViewById(R.id.create_report_save_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReport();
            }
        });
    }

    void setupCameraButton() {
        Button cameraButton = (Button) findViewById(R.id.create_report_camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });
    }


    void createReport() {
        EditText description = (EditText) findViewById(R.id.create_report_description);
        Report report = new Report(123.1, 321.1, description.getText().toString());
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
        EditText description = (EditText) findViewById(R.id.create_report_description);
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
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            cameraImage(data);
        }

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            galleryImage(data);
        }
    }

    private void cameraImage(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
//        imageView.setImageBitmap(imageBitmap);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
//        ImageView img = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height/2);

        View view = layoutInflater.inflate(R.layout.create_report_item, gallery, false);
        ImageView imageView = view.findViewById(R.id.create_report_item_imageView);
        imageView.setImageBitmap(imageBitmap);
        gallery.addView(view);
        Log.d(TAG, "cameraImage: IMAGE ADDED");
    }

    private void galleryImage(Intent data) {
        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }


    //endregion


//    TEST METHODS!

//    void setupGetButton() {
//        Button getButton = (Button) findViewById(R.id.create_report_get);
//        getButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                getReport();
//                FirebaseManager manager = new FirebaseManager();
//                manager.getAllReports(new ReportCallback() {
//                    @Override
//                    public void onCallback(ArrayList<Report> reports) {
//                        Log.d(TAG, "onCallback: REPORTS: " + reports.size());
//                    }
//                });
//            }
//        });
//    }
//
//    void setupCreateEventButton() {
//        Button createEventButton = (Button) findViewById(R.id.testCreateEvent);
//        createEventButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final FirebaseManager manager = new FirebaseManager();
//                manager.getAllReports(new ReportCallback() {
//                    @Override
//                    public void onCallback(ArrayList<Report> reports) {
//                        Log.d(TAG, "onCallback: REPORTS SIZE" + reports.size());
//                        Event e = new Event("i am test event", "test event", "13-02-2020", reports.get(0));
//                        try {
//                            manager.saveEvent(e);
//                        } catch (Exception e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    void setupGetEventsButton() {
//        Log.d(TAG, "setupGetEventsButton: pressed");
//        Button getEventsButton = (Button) findViewById(R.id.testGetEvents);
//        getEventsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseManager manager = new FirebaseManager();
//                manager.getAllEvents(new EventCallback() {
//                    @Override
//                    public void onCallback(ArrayList<Event> events) {
//                        // update events method
//                        Log.d(TAG, "onCallback: ALL EVENTS SIZE: " + events.size());
//                    }
//                });
//                manager.getUsersEvents(FirebaseAuth.getInstance().getCurrentUser().getUid(), new EventCallback() {
//                    @Override
//                    public void onCallback(ArrayList<Event> events) {
//                        Log.d(TAG, "onCallback: USERS EVENTS SIZE: " + events.size());
//                    }
//                });
//            }
//        });
//
//    }
//
//    void setupMyRep() {
//        Button myRep = (Button) findViewById(R.id.testMyReports);
//        myRep.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseManager manager = new FirebaseManager();
//                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                manager.getUsersReports(uid, new ReportCallback() {
//                    @Override
//                    public void onCallback(ArrayList<Report> reports) {
//                        Log.d(TAG, "onCallback: myReports" + reports.size());
//                    }
//                });
//            }
//        });
//    }
//
//    void getReport() {
//        Intent i = new Intent(this, MyReportActivity.class);
//        i.putExtra("allReports", true);
//        startActivity(i);
//    }
//


}
