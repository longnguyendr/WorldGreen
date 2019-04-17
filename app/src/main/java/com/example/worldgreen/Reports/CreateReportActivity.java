package com.example.worldgreen.Reports;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldgreen.FirebaseManager.FirebaseManager;
import com.example.worldgreen.R;
import com.example.worldgreen.DataModel.Report;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateReportActivity extends AppCompatActivity {

    private static final String TAG = "CreateReportActivity";
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private static final int LOCATION_REQUEST = 2;

    private ArrayList<byte[]> photos = new ArrayList<>();
    private Boolean isAccessibleByCar = null;
    private String size;
    private LinearLayout gallery;
    private LayoutInflater layoutInflater;
    private LocationListener locationListener;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        size = getResources().getStringArray(R.array.report_size)[0];
        gallery = findViewById(R.id.photo_gallery);
        layoutInflater = LayoutInflater.from(this);

        setupCreateButton();
        setupUpdateLocationButton();
        setupCameraButton();
        setupSpinner();
        getLocation();
    }


    //region Location
    //----------------------------------------------------------------------------------------------

    void getLocation() {
        setupLocationListener();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "getLocation: showing fine access reason");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
            }
        }
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
    }

    private void setupLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                Log.d(TAG, "onLocationChanged: location changed!: " + location.getLongitude() + " " + location.getLatitude());
                updateLocationTextView(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(CreateReportActivity.this,"No permission to get location!", Toast.LENGTH_LONG).show();
                }
        }
    }

    private String getAddress(double lat, double lon) {
        String cityName = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat,lon,10);
            if (addresses.size() > 0) {
                for (Address address : addresses) {
                    if (address.getLocality() != null && address.getLocality().length() > 0) {
                        cityName = address.getLocality();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    //endregion


    //region UI methods
    //----------------------------------------------------------------------------------------------

    public void resetUI() {
        this.finish();
    }

    private void updateLocationTextView(Location location) {
        TextView locationTextView = findViewById(R.id.location_textView);
        String address = getAddress(location.getLatitude(), location.getLongitude());
        String latStr = Double.toString(location.getLatitude());
        String lonStr = Double.toString(location.getLongitude());

        locationTextView.setText("Lat: " + latStr + " Lon: " + lonStr + "address: " + address);
    }

    //endregion

    //region Setup methods
    //----------------------------------------------------------------------------------------------

    void setupCreateButton() {
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

    void setupUpdateLocationButton() {
        Button updateLocationButton = findViewById(R.id.update_location);
        updateLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: update btn clicked");
                getLocation();
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

    public void onAccessibilityRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
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

    //endregion


    //region Create report methods
    //----------------------------------------------------------------------------------------------

    void createReport() throws CreateReportException {

        EditText description = findViewById(R.id.report_description);
        EditText title = findViewById(R.id.report_title);

        if (mLocation == null) {
            throw new CreateReportException("No location.");
        }

        if (photos.isEmpty()) {
            throw new CreateReportException("Take at least 1 picture, please.");
        }

        if (description.length() < 5) {
            throw new CreateReportException("Write a longer description, please.");
        }

        if (title.length() < 5 || title.length() > 20) {
            throw new CreateReportException("Title has to have at least 5 characters and max 20 characters");
        }

        if (isAccessibleByCar == null) {
            throw new CreateReportException("Check if report is accessible by car, please.");
        }

        Report report = new Report(mLocation.getLongitude(), mLocation.getLatitude(), description.getText().toString(), title.getText().toString(), photos, photos.size() , size, isAccessibleByCar);

        FirebaseManager manager = new FirebaseManager();

        manager.saveReport(this, report);
        
    }

    //endregion

    //region Add photo methods
    //----------------------------------------------------------------------------------------------


    private void addPhoto(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        photos.add(data);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        View view = layoutInflater.inflate(R.layout.create_report_item, gallery, false);
        ImageView imageView = view.findViewById(R.id.create_report_item_imageView);
        imageView.setImageBitmap(photo);
        gallery.addView(view);
    }

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
        if (extras != null) {
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                addPhoto(imageBitmap);
            }
        }
    }

    private void openGallery(Intent data) {
        try {
            final Uri imageUri = data.getData();
            if (imageUri != null) {
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                addPhoto(selectedImage);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    //endregion


}
